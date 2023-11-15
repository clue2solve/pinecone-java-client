package io.clue2solve.pinecone.javaclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.clue2solve.pinecone.javaclient.model.*;
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

    private OkHttpClientWrapper client;
    private String environment;
    private String projectId;
    private String apiKey;

    public PineconeDBClient(String environment, String projectId, String apiKey) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new LoggingInterceptor())
                .build();
        this.client = new OkHttpClientWrapper(okHttpClient);
        this.environment = environment;
        this.projectId = projectId;
        this.apiKey = apiKey;
    }

    public void setClient(OkHttpClientWrapper client) {
        this.client = client;
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
        },
        FETCH {
            public String toString() {
                return "vectors/fetch";
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
     * Fetches the vector and metadata for the given ID.
     *
     * @param fetchRequest Request parameters for the fetch operation.
     * @return FetchResponse containing the vector and metadata.
     * @throws IOException if there's an error during the fetch operation.
     */
    public FetchResponse fetch(FetchRequest fetchRequest) throws IOException {
        String url = buildUrl(fetchRequest.getIndexName(), EndPoints.FETCH.toString());
        Request request = prepareFetchRequest(fetchRequest, url);
        try {
            Response response = client.newCall(request).execute();
            FetchResponse fetchResponse = extractFetchResponse(response.body().string());
            return fetchResponse;
        } catch (IOException e) {
            LOG.error("Error fetching vector for ids: {}", fetchRequest.getIds(), e);
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

        Request request = preparDeletelRequest(deleteRequest, url);

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
    public List<QueryResponse> extractQueryResponse(String jsonResponseString) throws JsonProcessingException {
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
            LOG.info("QueryResponse: {}", queryResponse.toString());
            queryResponses.add(queryResponse);
        }
        return queryResponses;
    }

    private FetchResponse extractFetchResponse(String jsonResponseString) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(jsonResponseString);
        FetchResponse fetchResponse = new FetchResponse();
        JsonNode vectors = rootNode.get("vectors");
        String id = vectors.fieldNames().next();
        JsonNode vector = vectors.get(id);
        fetchResponse.setId(UUID.fromString(vector.get("id").asText()));


        JsonNode valuesJson = vector.get("values");
        ObjectMapper objectMapper = new ObjectMapper();
        List<Double> values = null;
        try {
            values = objectMapper.readValue(valuesJson.toString(), new TypeReference<List<Double>>(){});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        fetchResponse.setValues(values);
        fetchResponse.setNameSpace(rootNode.get("namespace").toString());
        fetchResponse.setMetadata(vector.get("metadata").toString());



//        //Extract only if the response is not empty
//        if(rootNode.get("id") == null) {
//            return fetchResponse;
//        }
//        fetchResponse.setId(UUID.fromString(rootNode.get("id").asText()));
//
//        List<Double> valuesList = new ArrayList<>();
//        rootNode.get("values").forEach(value -> valuesList.add(value.asDouble()));
//        fetchResponse.setValues(valuesList);
//        fetchResponse.setMetadata(rootNode.get("metadata").toString());
//
//        fetchResponse.setAdditionalProp(rootNode.get("additionalProp").toString()); //TODO: Convert this into a JSON object
//        fetchResponse.setSparseValues(rootNode.get("sparseValues").toString()); //TODO: Convert this into a JSON object
//        fetchResponse.setNameSpace(rootNode.get("namespace").toString());
//        fetchResponse.setIndexName(rootNode.get("indexName").toString());

        return fetchResponse;
    }

    /**
     * Prepares a request for the given index and endpoint.
     *
     * @param indexName Name of the index.
     * @param url  Endpoint to be called.
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
            LOG.info("Query JSON: {}", queryJsonObject.toString());
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
            json.put("namespace", upsertRequest.getNamespace());
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
    private Request preparDeletelRequest(DeleteRequest deleteRequest, String url) {
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
            LOG.error("Error building Delete request for Namespace: {}", deleteRequest.getNamespace(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Prepares a request for the given index and endpoint.
     *
     * @param fetchRequest Request parameters for the fetch operation.
     * @param url          URL to be called.
     * @return Prepared request.
     */
    private Request prepareFetchRequest(FetchRequest fetchRequest, String url) {
        try {

            HttpUrl.Builder urlBuilder = HttpUrl.parse(url)
                    .newBuilder();
            urlBuilder.addQueryParameter("ids", fetchRequest.getIDsAsString());
            urlBuilder.addQueryParameter("namespace", fetchRequest.getNameSpace());

            Request.Builder builder = new Request.Builder()
                    .url(urlBuilder.build().toString())
                    .method("GET", null)
                    .addHeader("accept", "application/json")
                    .addHeader("content-type", "application/json")
                    .addHeader("Api-Key", apiKey)
                    .header("ids", fetchRequest.getIDsAsString());

            JSONObject json = new JSONObject();
            json.put("namespace", fetchRequest.getNameSpace());
            MediaType mediaType = MediaType.parse("application/json");

//            builder.post(RequestBody.create(mediaType, String.valueOf(fetchRequest.toString())));
            builder.method("GET", null);
            return builder.build();

        } catch (Exception e) {
            LOG.error("Error building Fetch request for Namespace: {}", fetchRequest.getNameSpace(), e);
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
