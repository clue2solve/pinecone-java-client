package io.clue2solve.pinecone.javaclient.utils;

import lombok.NoArgsConstructor;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

@NoArgsConstructor
public class OkHttpClientWrapper {
    private OkHttpClient client;

    public OkHttpClientWrapper(OkHttpClient client) {
        this.client = client;
    }

    public Call newCall(Request request) {
        if (client == null) {
            throw new IllegalStateException("OkHttpClient is not initialized");
        }
        return client.newCall(request);
    }
}