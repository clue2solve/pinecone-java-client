package io.clue2solve.pinecone.javaclient;

import static org.junit.jupiter.api.Assertions.*;


import io.clue2solve.pinecone.javaclient.model.UpsertRequest;
import okhttp3.*;

import org.junit.jupiter.api.TestInstance;

import java.io.IOException;

import static org.mockito.Mockito.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class PineconeDBClientFixMe {

    private PineconeDBClient client;
    private OkHttpClient mockHttpClient;
    private Call mockCall;
    private Response mockResponse;
    private ResponseBody mockResponseBody;

//    @BeforeAll
    public void setUp() throws IOException {
        // Mocking the external dependencies
        mockHttpClient = mock(OkHttpClient.class);
        mockCall = mock(Call.class);
        mockResponse = mock(Response.class);
        mockResponseBody = mock(ResponseBody.class);

        // When newCall is called on the mockHttpClient, return the mockCall
        when(mockHttpClient.newCall(any(Request.class))).thenReturn(mockCall);

        // When execute is called on the mockCall, return the mockResponse
        when(mockCall.execute()).thenReturn(mockResponse);

        // When body is called on the mockResponse, return the mockResponseBody
        when(mockResponse.body()).thenReturn(mockResponseBody);

        // When string is called on the mockResponseBody, return a sample response
        when(mockResponseBody.string()).thenReturn("Sample Response");

        // Create an instance of PineconeDBClient with the mocked OkHttpClient
        client = new PineconeDBClient("testEnv", "testProjectId", "testApiKey") {
            @Override
            protected OkHttpClient buildClient() {
                return mockHttpClient;
            }
        };
    }

//    @Test
    public void testUpsert() throws IOException {
        UpsertRequest mockUpsertRequest = mock(UpsertRequest.class);
        when(mockUpsertRequest.getIndexName()).thenReturn("testIndex");
        when(mockUpsertRequest.toString()).thenReturn("{}"); // Assuming UpsertRequest's toString() returns a JSON string

        String response = client.upsert(mockUpsertRequest);

        assertEquals("Sample Response", response);
    }
}
