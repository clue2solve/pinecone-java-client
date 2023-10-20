package io.clue2solve.pinecone.javaclient;

import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONObject;
import org.json.JSONArray;

import java.io.IOException;
import java.util.List;

public class PineconeDBClient {
    private static final Logger LOG = LoggerFactory.getLogger(PineconeDBClient.class);

    private OkHttpClient client;
    private String environment;
    private String projectId;
    private String apiKey;

    public PineconeDBClient(String environment, String projectId, String apiKey) {
        this.client = new OkHttpClient();
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
        Request request = prepareRequest(indexName, url, null);
        try {
            Response response = client.newCall(request).execute();
            return response;
        } catch (IOException e) {
            LOG.error("Error fetching index stats for index: {}", indexName, e);
            throw e;
        }
    }

    public Response query(String indexName, boolean includeValues, boolean includeMetadata, List<Double> vector) throws IOException {
        LOG.info("Querying index: {}", indexName);

        String url = buildUrl(indexName, EndPoints.QUERY.toString());
        JSONObject jsonBody = constructJson(indexName, includeValues, includeMetadata, vector);

        MediaType mediaType = MediaType.parse("application/json");

        RequestBody body = RequestBody.create(mediaType, jsonBody.toString());

        Request request = prepareRequest(indexName, url, body);

        try {
            Response response = client.newCall(request).execute();
            LOG.info("Successfully queried index: {}", indexName);
            return response;
        } catch (IOException e) {
            LOG.error("Error querying index: {}", indexName, e);
            throw e;
        }
    }

    public Response upsert(String indexName, String namespace) throws IOException {
        LOG.info("Upserting for index: {}", indexName);

        String url = buildUrl(indexName, EndPoints.UPSERT.toString() + "/vectors/upsert");
        JSONObject json = new JSONObject();
        json.put("namespace", namespace);

        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, json.toString());

        Request request = prepareRequest(indexName, url, body);

        try {
            Response response = client.newCall(request).execute();
            LOG.info("Successfully upserted for index: {}", indexName);
            return response;
        } catch (IOException e) {
            LOG.error("Error upserting for index: {}", indexName, e);
            throw e;
        }
    }

    @NotNull
    private Request prepareRequest(String indexName, String url, RequestBody body) {
        try {
            Request.Builder builder = new Request.Builder()
                    .url(url)
                    .addHeader("accept", "application/json")
                    .addHeader("content-type", "application/json")
                    .addHeader("Api-Key", apiKey);

            if (body != null) {
                builder.post(body);
            } else {
                builder.post(RequestBody.create(null, new byte[0]));
            }

            return builder.build();

        } catch (Exception e) {
            LOG.error("Error building request for index: {}", indexName, e);
            throw new RuntimeException(e);
        }
    }

    private String buildUrl(String indexName, String endpoint) {
        String formattedUrl = String.format("https://%s-%s.svc.%s.pinecone.io/%s", indexName, projectId, environment, endpoint);
        LOG.info(formattedUrl);
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
