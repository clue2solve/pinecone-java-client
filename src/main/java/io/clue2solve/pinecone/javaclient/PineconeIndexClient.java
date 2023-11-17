package io.clue2solve.pinecone.javaclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.clue2solve.pinecone.javaclient.model.ConfigureIndexRequest;
import io.clue2solve.pinecone.javaclient.model.CreateCollectionRequest;
import io.clue2solve.pinecone.javaclient.model.CreateIndexRequest;
import io.clue2solve.pinecone.javaclient.utils.OkHttpClientWrapper;
import io.clue2solve.pinecone.javaclient.utils.OkHttpLoggingInterceptor;
import okhttp3.*;

import java.io.IOException;

public class PineconeIndexClient {
    private final OkHttpClientWrapper client;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final String collectionsBaseURL = "https://controller.us-west4-gcp.pinecone.io/collections";
    private final String apiKey;

    public PineconeIndexClient(String environment, String apiKey) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new OkHttpLoggingInterceptor())
                .build();
        this.client = new OkHttpClientWrapper(okHttpClient);
        this.objectMapper = new ObjectMapper();
        this.baseUrl = "https://controller." + environment + ".pinecone.io/databases";
        this.apiKey = apiKey;
    }

    public String listIndexes() throws IOException {
        return executeRequest(new Request.Builder()
                .url(baseUrl)
                .addHeader("Api-Key", apiKey)
                .build());
    }

    public String createIndex(CreateIndexRequest createIndexRequest) throws IOException {
        return executeRequest(createJsonRequest(createIndexRequest, baseUrl, "POST"));
    }

    public String deleteIndex(String indexName) throws IOException {
        return executeRequest(new Request.Builder()
                .url(baseUrl + "/" + indexName)
                .delete()
                .addHeader("Api-Key", apiKey)
                .build());
    }

    public String describeIndex(String indexName) throws IOException {
        return executeRequest(new Request.Builder()
                .url(baseUrl + "/" + indexName)
                .addHeader("Api-Key", apiKey)
                .build());
    }

    public String configureIndex(String indexName, ConfigureIndexRequest configureIndexRequest) throws IOException {
        return executeRequest(createJsonRequest(configureIndexRequest, baseUrl + "/" + indexName, "PATCH"));
    }

    public String listCollections() throws IOException {
        return executeRequest(new Request.Builder()
                .url(collectionsBaseURL)
                .addHeader("Api-Key", apiKey)
                .build());
    }

    public String createCollection(CreateCollectionRequest createCollectionRequest) throws IOException {
        return executeRequest(createJsonRequest(createCollectionRequest, collectionsBaseURL, "POST"));
    }

    public String describeCollection(String collectionName) throws IOException {
        return executeRequest(new Request.Builder()
                .url(collectionsBaseURL + "/" + collectionName)
                .addHeader("Api-Key", apiKey)
                .build());
    }

    public String deleteCollection(String collectionName) throws IOException {
        return executeRequest(new Request.Builder()
                .url(collectionsBaseURL + "/" + collectionName)
                .delete()
                .addHeader("Api-Key", apiKey)
                .build());
    }

    private String executeRequest(Request request) throws IOException {
        try (Response response = client.newCall(request).execute()) {
            return response.body() != null ? response.body().string() : null;
        }
    }

    private Request createJsonRequest(Object requestObject, String url, String method) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        String json = objectMapper.writeValueAsString(requestObject);
        RequestBody body = RequestBody.create(json, mediaType);

        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .addHeader("accept", "text/plain")
                .addHeader("content-type", "application/json")
                .addHeader("Api-Key", apiKey);

        switch (method.toUpperCase()) {
            case "POST":
                requestBuilder.post(body);
                break;
            case "PATCH":
                requestBuilder.patch(body);
                break;
            case "PUT":
                requestBuilder.put(body);
                break;
            // Add other HTTP methods as needed
        }
        return requestBuilder.build();
    }

    // Add other methods for managing indexes here
}
