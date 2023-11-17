package io.clue2solve.pinecone.javaclient;

import io.clue2solve.pinecone.javaclient.model.ConfigureIndexRequest;
import io.clue2solve.pinecone.javaclient.model.CreateCollectionRequest;
import io.clue2solve.pinecone.javaclient.model.CreateIndexRequest;

import java.util.Properties;
import java.io.InputStream;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PineconeIndexClientIT {
    private PineconeIndexClient client;
    Properties properties;
    @BeforeEach
    public void setup() throws Exception {
        properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("test.properties")) {
            properties.load(input);
        }

        String environment = properties.getProperty("testEnvironment");
        String apiKey = properties.getProperty("testApiKey");
        client = new PineconeIndexClient(environment, apiKey);
    }

    @Order(2)
    @Test
    public void testListIndexes() throws Exception {
        String indexes = client.listIndexes();

        // Assert that the response contains the expected index
        String testIndex = properties.getProperty("testIndex");
        assertTrue(indexes.contains(testIndex));
    }

    @Test
    @Order(1)
    public void testCreateIndex() throws Exception {
        // Set the properties of the request...
        CreateIndexRequest createIndexRequest = CreateIndexRequest.builder()
                .name("test-index")
                .metric("cosine")
                .dimension(5)
                .replicas(1)
                .pods(1)
                .pod_type("p1.x1")
                // .metadata_config("{\"newKey\":\"New Value\",\"newKey-1\":\"New Value\"}") //TODO handle this feature 
                .build();

        String response = client.createIndex(createIndexRequest);
        //use the response to assert that the index was created successfully

        // Assert that the response is as expected...
    }

    @Test
    @Order(10)
    public void testDeleteIndex() throws Exception {
        String response = client.deleteIndex("test-index");

        // Assert that the response is as expected...
    }


    @Test
    @Order(3)
    public void testDescribeIndex() throws Exception {
        String response = client.describeIndex("test-index");

        // Assert that the response is as expected...
    }

    @Order(4)
    @Test
    public void testConfigureIndex() throws Exception {
        ConfigureIndexRequest configureIndexRequest = ConfigureIndexRequest.builder()
                .replicas(1)
                .pod_type("s1.x1")
                .build();

        String response = client.configureIndex("test-index", configureIndexRequest);
        //TODO: ADD BETTER ASSERTS after handling error cases in the configure method. 
        // Assert that the response is as expected...
    }

    @Test
    @Order(6)
    public void testListCollections() throws Exception {
        String response = client.listCollections();

        // Assert that the response is as expected...
    }

    @Test
    @Order(7)
    public void testCreateCollection() throws Exception {
        CreateCollectionRequest createCollectionRequest = CreateCollectionRequest.builder()
                .name("test1")
                .source("dbclient-testing")
                .build();

        String response = client.createCollection(createCollectionRequest);

        // Assert that the response is as expected...
    }

    @Test
    @Order(8)
    public void testDescribeCollection() throws Exception {
        String response = client.describeCollection("test1");

        // Assert that the response is as expected...
    }

    @Test
    @Order(9)
    public void testDeleteCollection() throws Exception {
        String response = client.deleteCollection("test1");

        // Assert that the response is as expected...
    }
    // Add other tests for PineconeIndexClient methods here
}