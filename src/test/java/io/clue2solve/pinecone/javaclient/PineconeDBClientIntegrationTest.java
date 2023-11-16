package io.clue2solve.pinecone.javaclient;

import io.clue2solve.pinecone.javaclient.model.*;
import okhttp3.Response;
import org.junit.jupiter.api.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import com.fasterxml.jackson.databind.ObjectMapper;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PineconeDBClientIntegrationTest {

    Logger logger = Logger.getLogger(PineconeDBClientIntegrationTest.class.getName());
        String indexName ;
        String nameSpace  ;
        private PineconeDBClient pineconeDBClient;
        private String uuidString;
        private UpsertRequest upsertRequest;
        private String expectedQueryResponse;
        private String expectedUpsertResponse;
        private QueryRequest queryRequest;
        private DeleteRequest deleteRequest;
        private FetchRequest fetchRequest;


        @BeforeEach
        public void setUp() throws IOException {
            Properties properties = new Properties();
            properties.load(new FileInputStream("src/test/resources/test.properties"));
            String environment = properties.getProperty("testEnvironment");
            String projectId = properties.getProperty("testProjectId");
            String apiKey = properties.getProperty("testApiKey");
            pineconeDBClient = new PineconeDBClient(environment, projectId, apiKey);
            indexName = properties.getProperty("testIndex");
            nameSpace = properties.getProperty("testNamespace");


            // Generate a UUID and convert it to a string
            uuidString = UUID.fromString("b1ce7f35-41fc-4159-a9ab-a24c4de2abcd").toString();
    
            // Create metadata
            Map<String, String> metadata = new HashMap<>();
            metadata.put("page", "460");
            metadata.put("source", "/tmp/tmpsa5b18gg/tmp.pdf");
            metadata.put("text", "PooledDataBuffer  is an extension of DataBuffer");
    
            // Convert metadata to JSON string
            ObjectMapper objectMapper = new ObjectMapper();
            String metadataJson = objectMapper.writeValueAsString(metadata);
    
            // Create UpsertVector
            UpsertVector upsertVector = UpsertVector.builder()
                .id(uuidString)
                .values(Arrays.asList(0.94, 0.69, 0.23))
                .metadata(metadataJson)
                .build();
    
            // Create UpsertRequest
            upsertRequest = UpsertRequest.builder()
                .indexName(indexName)
                .namespace(nameSpace)
                .upsertVectorsList(Collections.singletonList(upsertVector))
                .build();

            //Create a Query Request with the values above
            queryRequest = QueryRequest.builder()
                .indexName(indexName)
                .namespace(nameSpace)
                .top_k(10)
                .includeMetadata(true)
                .includeValues(true)
                .queryVector(Arrays.asList(0.94, 0.69, 0.23))
                .build();

            //Create a Fetch Request with the values above
            fetchRequest = FetchRequest.builder()
                .indexName(indexName)
                .nameSpace(nameSpace)
                .ids(new String[]{uuidString})
                .build();


            //Create a Delete Request with the values above
            deleteRequest = DeleteRequest.builder()
                .indexName(indexName)
                .namespace(nameSpace)
                .ids(new String[]{uuidString})
                .build();
                
            // Set expected response
            expectedQueryResponse = "{\"code\":3,\"message\":\"Vector dimension 2 does not match the dimension of the index 1536\",\"details\":[]}";
            expectedUpsertResponse = "{\"upsertedCount\":1}";
        }

    @Test
    @Order(-1)
    public void     testDescribeIndexStats() throws IOException {
        // String indexName = testIndexName; // replace with your test index name
        Response response = pineconeDBClient.describeIndexStats(indexName);
        assertEquals(200, response.code());
        // Add more assertions based on your response
    }
    @Test
    @Order(0)
    public void testUpsert() throws IOException {
        String response = pineconeDBClient.upsert(upsertRequest);
        assertNotNull(response);

        // Assert that the response matches the expected JSON
        assertEquals(expectedUpsertResponse, response);
    }
    @Test
    @Order(1)
    public void testQuery() throws IOException {
       
        List<QueryResponse> queryResponses = pineconeDBClient.query(queryRequest);
        assertNotNull(queryResponses);

        // Assert that the queryResponses contains the upserted vector
        boolean containsUpsertedVector = queryResponses.stream()
            .anyMatch(response -> uuidString.equals(response.getId().toString()));
        assertTrue(containsUpsertedVector);
    }

    @Test
    @Order(2)
    public void testFetch() throws IOException {
        FetchResponse fetchResponse = pineconeDBClient.fetch(fetchRequest);
        assertNotNull(fetchResponse);
        //assert only when getID is not null
        if (fetchResponse.getId() != null) {
            assertEquals(uuidString, fetchResponse.getId().toString());
        }
        // Add more assertions based on your fetchResponse
    }




    @Test
    @Order(3)
    public void testDelete() throws IOException {
        String response = pineconeDBClient.delete(deleteRequest);
        assertNotNull(response);
        // Add more assertions based on your response
    }
}