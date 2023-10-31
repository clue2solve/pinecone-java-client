package io.clue2solve.pinecone.javaclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.clue2solve.pinecone.javaclient.model.DeleteRequest;
import io.clue2solve.pinecone.javaclient.model.QueryRequest;
import io.clue2solve.pinecone.javaclient.model.QueryResponse;
import io.clue2solve.pinecone.javaclient.model.UpsertRequest;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Client to interface with PineconeDB.
 * Provides methods for fetching index statistics, querying, and performing upsert operations.
 */
public class PineconeDBClient {
    private static final Logger LOG = LoggerFactory.getLogger(PineconeDBClient.class);

    private OkHttpClient client;
    private String environment;
    private String projectId;
    private String apiKey;

    /**
     * Constructs a new PineconeDBClient.
     *
     * @param environment Environment in which the PineconeDB instance resides.
     * @param projectId   ID of the project.
     * @param apiKey      API key for authentication.
     */
    public PineconeDBClient(String environment, String projectId, String apiKey) {
        this.client = new OkHttpClient();

        this.client = new OkHttpClient.Builder()
                .addInterceptor(new LoggingInterceptor())
                .build();
        this.environment = environment;
        this.projectId = projectId;
        this.apiKey = apiKey;
    }

    /**
     * Constructs a new PineconeDBClient with a custom OkHttpClient
     */
    protected OkHttpClient buildClient() {
        return new OkHttpClient.Builder()
                .addInterceptor(new LoggingInterceptor())
                .build();
    }

    /**
     * Enumerations for various PineconeDB endpoints.
     */
    public enum EndPoints {
        DESCRIBE_INDEX_STATS {
            public String toString() {
                return "describe_index_stats";
            }
        },
        QUERY {
            public String toString() {
                return "query";
            }
        },
        UPSERT {
            public String toString() {
                return "vectors/upsert";
            }
        },
        DELETE {
            public String toString() {
                return "vectors/delete";
            }
        }

    }

    /**
     * Fetches statistics related to the described index.
     *
     * @param indexName Name of the index to be described.
     * @return Response from PineconeDB with the described statistics.
     * @throws IOException if there's an error fetching index stats.
     */
    public Response describeIndexStats(String indexName) throws IOException {
        String url = buildUrl(indexName, EndPoints.DESCRIBE_INDEX_STATS.toString());
        Request request = preparNullBodyRequest(indexName, url);
        try {
            Response response = client.newCall(request).execute();
            return response;
        } catch (IOException e) {
            LOG.error("Error fetching index stats for index: {}", indexName, e);
            throw e;
        }
    }

    /**
     * Queries PineconeDB using the provided request parameters.
     *
     * @param queryRequest Request parameters for the query.
     * @return List of QueryResponses resulting from the query.
     * @throws IOException if there's an error during the query.
     */
    public List<QueryResponse> query(QueryRequest queryRequest) throws IOException {
        String url = buildUrl(queryRequest.getIndexName(), EndPoints.QUERY.toString());
        Request request = prepareQueryRequest(queryRequest, url);
        try {
            Response response = client.newCall(request).execute();
            List<QueryResponse> queryResponses = extractQueryResponse(response.body().string());
            return queryResponses;
        } catch (IOException e) {
            LOG.error("Error querying index: {}", queryRequest.getIndexName(), e);
            throw e;
        }
    }


    /**
     * Performs an upsert operation on PineconeDB.
     *
     * @param upsertRequest Request parameters for the upsert operation.
     * @return Response string from PineconeDB as a result of the upsert operation.
     * @throws IOException if there's an error during the upsert operation.
     */
    public String upsert(@NotNull UpsertRequest upsertRequest) throws IOException {
        String url = buildUrl(upsertRequest.getIndexName(), EndPoints.UPSERT.toString() );

        Request request = prepareUpsertRequest(upsertRequest, url);

        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Performs a delete operation on PineconeDB.
     *
     * @param deleteRequest Request parameters for the delete operation.
     * @return Response string from PineconeDB as a result of the delete operation.
     * @throws IOException if there's an error during the delete operation.
     */
    public String delete(DeleteRequest deleteRequest) throws IOException {
        String url = buildUrl(deleteRequest.getIndexName(), EndPoints.DELETE.toString() );

        Request request = preparDeleteBodylRequest(deleteRequest, url);

        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Extracts the QueryResponses from the JSON response string.
     *
     * @param jsonResponseString JSON response string from PineconeDB.
     * @return List of QueryResponses.
     * @throws JsonProcessingException if there's an error processing the JSON response string.
     */
    @NotNull
    private List<QueryResponse> extractQueryResponse(String jsonResponseString) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(jsonResponseString);
        List<QueryResponse> queryResponses = new ArrayList<>();
        JsonNode matches = rootNode.get("matches");
        for (JsonNode match : matches) {
            QueryResponse queryResponse = new QueryResponse();

            queryResponse.setId(UUID.fromString(match.get("id").asText()));
            queryResponse.setScore(match.get("score").asDouble());

            List<Double> valuesList = new ArrayList<>();
            match.get("values").forEach(value -> valuesList.add(value.asDouble()));
            queryResponse.setValues(valuesList);
            queryResponse.setMetadata(match.get("metadata").toString());
            queryResponses.add(queryResponse);
        }
        return queryResponses;
    }


    /**
     * Prepares a request for the given index and endpoint.
     *
     * @param indexName Name of the index.
     * @param endpoint  Endpoint to be called.
     * @return Prepared request.
     */
    @NotNull
    private Request preparNullBodyRequest(String indexName,String url) {
        try {
            Request.Builder builder = new Request.Builder()
                    .url(url)
                    .addHeader("accept", "application/json")
                    .addHeader("content-type", "application/json")
                    .addHeader("Api-Key", apiKey);

            JSONObject json = new JSONObject();
            json.put("namespace", indexName);
            MediaType mediaType = MediaType.parse("application/json");

            builder.post(RequestBody.create(null, new byte[0]));
            return builder.build();

        } catch (Exception e) {
            LOG.error("Error building request for index: {}", indexName, e);
            throw new RuntimeException(e);
        }
    }


    /**
     * Prepares a request for the given index and endpoint.
     *
     * @param queryRequest Request parameters for the query.
     * @param url          URL to be called.
     * @return Prepared request.
     */
    @NotNull
    private Request prepareQueryRequest(QueryRequest queryRequest, String url) {
        try {
            Request.Builder builder = new Request.Builder()
                    .url(url)
                    .addHeader("accept", "application/json")
                    .addHeader("content-type", "application/json")
                    .addHeader("Api-Key", apiKey);

            JSONObject queryJsonObject = queryRequest.getRequestAsJson();
            RequestBody body = RequestBody.create(MediaType.parse("application/json"),
                    queryJsonObject.toString());
            builder.post(body);
            return builder.build();

        } catch (Exception e) {
            LOG.error("Error building Query request for index: {}", queryRequest.getIndexName(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Prepares a request for the given index and endpoint.
     *
     * @param upsertRequest Request parameters for the upsert operation.
     * @param url           URL to be called.
     * @return Prepared request.
     */
    @NotNull
    private Request prepareUpsertRequest(UpsertRequest upsertRequest, String url) {
        try {
            Request.Builder builder = new Request.Builder()
                    .url(url)
                    .addHeader("accept", "application/json")
                    .addHeader("content-type", "application/json")
                    .addHeader("Api-Key", apiKey);

            JSONObject json = new JSONObject();
            json.put("namespace", upsertRequest.getNameSpace());
            MediaType mediaType = MediaType.parse("application/json");

            builder.post(RequestBody.create(mediaType, String.valueOf(upsertRequest.toString())));
            return builder.build();

        } catch (Exception e) {
            LOG.error("Error building Upsert request for index: {}", upsertRequest.getIndexName(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Prepares a request for the given index and endpoint.
     * @param deleteRequest
     * @param url
     * @return
     */
    private Request preparDeleteBodylRequest(DeleteRequest deleteRequest, String url) {
        try {
            Request.Builder builder = new Request.Builder()
                    .url(url)
                    .addHeader("accept", "application/json")
                    .addHeader("content-type", "application/json")
                    .addHeader("Api-Key", apiKey);

            MediaType mediaType = MediaType.parse("application/json");

            builder.post(RequestBody.create(mediaType, String.valueOf(deleteRequest.toString())));
            return builder.build();

        } catch (Exception e) {
            LOG.error("Error building request for Namespace: {}", deleteRequest.getNamespace(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Builds the URL for the given index and endpoint.
     *
     * @param indexName Name of the index.
     * @param endpoint  Endpoint to be called.
     * @return URL for the given index and endpoint.
     */
    private String buildUrl(String indexName, String endpoint) {
        LOG.info("Building URL for index: {} and endpoint: {}", indexName, endpoint);
        String formattedUrl = String.format("https://%s-%s.svc.%s.pinecone.io/%s", indexName, projectId, environment, endpoint);
        LOG.info("Formatted URL : {} ",formattedUrl);
        return formattedUrl;
    }

}
