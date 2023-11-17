package io.clue2solve.pinecone.javaclient;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.clue2solve.pinecone.javaclient.model.ConfigureIndexRequest;
import io.clue2solve.pinecone.javaclient.model.CreateCollectionRequest;
import io.clue2solve.pinecone.javaclient.model.CreateIndexRequest;
import io.clue2solve.pinecone.javaclient.utils.OkHttpClientWrapper;
import io.clue2solve.pinecone.javaclient.utils.OkHttpLoggingInterceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.Properties;

public class PineconeIndexClient {
    private final OkHttpClientWrapper client;
    private final ObjectMapper objectMapper;
    private final String baseUrl;
    private final String collectionsBaseURL = "https://controller.us-west4-gcp.pinecone.io/collections";

    private String apiKey;

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
        Request request = new Request.Builder()
                .url(baseUrl)
                .addHeader("Api-Key", apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

     public String createIndex(CreateIndexRequest createIndexRequest) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        String json = objectMapper.writeValueAsString(createIndexRequest);
        RequestBody body = RequestBody.create(mediaType, json);

        Request request = new Request.Builder()
                .url(baseUrl)
                .post(body)
                .addHeader("accept", "text/plain")
                .addHeader("content-type", "application/json")
                .addHeader("Api-Key", apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public String deleteIndex(String indexName) throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl + "/" + indexName)
                .delete()
                .addHeader("accept", "text/plain")
                .addHeader("Api-Key", apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public String describeIndex(String indexName) throws IOException {
        Request request = new Request.Builder()
                .url(baseUrl + "/" + indexName)
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Api-Key", apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public String configureIndex(String indexName, ConfigureIndexRequest configureIndexRequest) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        String json = objectMapper.writeValueAsString(configureIndexRequest);
        RequestBody body = RequestBody.create(mediaType, json);

        Request request = new Request.Builder()
                .url(baseUrl + "/" + indexName)
                .patch(body)
                .addHeader("accept", "text/plain")
                .addHeader("content-type", "application/json")
                .addHeader("Api-Key", apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            //TODO: Handle error conditions like reaching the limit on resources : The index exceeds the project quota of 2 pods by 2 pods.
            //  or : Upgrade your account or change the project settings to increase the quota. or even the number of indexes.
            // or : updating base pod type is not supported
            return response.body().string();
        }
    }

    public String listCollections() throws IOException {
        Request request = new Request.Builder()
                .url(collectionsBaseURL)
                .get()
                .addHeader("accept", "application/json; charset=utf-8")
                .addHeader("Api-Key", apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public String createCollection(CreateCollectionRequest createCollectionRequest) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        String json = objectMapper.writeValueAsString(createCollectionRequest);
        RequestBody body = RequestBody.create(mediaType, json);

        Request request = new Request.Builder()
                .url(collectionsBaseURL)
                .post(body)
                .addHeader("accept", "text/plain")
                .addHeader("content-type", "application/json")
                .addHeader("Api-Key", apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public String describeCollection(String collectionName) throws IOException {
        Request request = new Request.Builder()
                .url(collectionsBaseURL + "/" + collectionName)
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Api-Key", apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    public String deleteCollection(String collectionName) throws IOException {
        Request request = new Request.Builder()
                .url(collectionsBaseURL + "/" + collectionName)
                .delete()
                .addHeader("accept", "text/plain")
                .addHeader("Api-Key", apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }


    // Add other methods for managing indexes here
}
