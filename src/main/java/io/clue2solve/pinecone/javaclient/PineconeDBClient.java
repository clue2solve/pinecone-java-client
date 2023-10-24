package io.clue2solve.pinecone.javaclient;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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


public class PineconeDBClient {
    private static final Logger LOG = LoggerFactory.getLogger(PineconeDBClient.class);

    private OkHttpClient client;
    private String environment;
    private String projectId;
    private String apiKey;

    public PineconeDBClient(String environment, String projectId, String apiKey) {
        this.client = new OkHttpClient();

        this.client = new OkHttpClient.Builder()
                .addInterceptor(new LoggingInterceptor())
                .build();
        this.environment = environment;
        this.projectId = projectId;
        this.apiKey = apiKey;
    }

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

    private String buildUrl(String indexName, String endpoint) {
        LOG.info("Building URL for index: {} and endpoint: {}", indexName, endpoint);
        String formattedUrl = String.format("https://%s-%s.svc.%s.pinecone.io/%s", indexName, projectId, environment, endpoint);
        LOG.info("Formatted URL : {} ",formattedUrl);
        return formattedUrl;
    }


    public static JSONObject constructJson(String indexName, boolean includeValues, boolean includeMetadata, List<Double> vector) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("indexName", indexName);
        jsonObject.put("includeValues", includeValues);
        jsonObject.put("includeMetadata", includeMetadata);
        jsonObject.put("top_k", 10);
        jsonObject.put("vector", vector);

        return jsonObject;
    }
}
