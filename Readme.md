## Pinecone Java Client
[![Java CI with Maven](https://github.com/clue2solve/pinecone-java-client/actions/workflows/maven.yml/badge.svg)](https://github.com/clue2solve/pinecone-java-client/actions/workflows/maven.yml)
### Overview
The Pinecone Java Client is an unofficial Java library for interacting with PineconeDB, a vector database ideal for building vector search applications. This client library facilitates operations such as fetching index statistics, querying, and performing upsert and delete operations in PineconeDB.

### Features
- Fetch index statistics
- Vector Query operations
- Vector Upsert operations
- Vector Delete operations
- Vector Fetch operations


### Installation
Include the following dependency in your project's build file:

```XML
    <!-- Add dependency for Pinecone Java Client -->
    <dependency>
        <groupId>io.clue2solve</groupId>
        <artifactId>pinecone-javaclient</artifactId>
        <version>1.0.0</version>
    </dependency>
```

### Usage
A Sample SpringBoot Web app that is built to show the usage of this client is available at [pinecone-console](https://github.com/clue2solve/pinecone-console), which can be used as a reference to build your own application. This project is still a WIP, and will be updated with more features and examples.

#### Initializing the Client
```java
PineconeDBClient client = new PineconeDBClient("environment", "projectId", "apiKey");
```

#### Fetching Index Statistics
```java
Response statsResponse = client.describeIndexStats("indexName");
```

#### Querying
```java
QueryRequest queryRequest = new QueryRequest(/* parameters */);
List<QueryResponse> responses = client.query(queryRequest);
```


#### Performing Upsert Operations
```java
UpsertRequest upsertRequest = new UpsertRequest(/* parameters */);
String upsertResponse = client.upsert(upsertRequest);
```

#### Deleting
```java
DeleteRequest deleteRequest = new DeleteRequest(/* parameters */);
String deleteResponse = client.delete(deleteRequest);
```

### Model Classes
The client library uses several model classes to structure the data for requests and responses. Below is a brief overview of these classes:

#### QueryRequest
**Description**: Represents the request body for query operations.
**Fields**:
- `indexName`: Name of the index to query.
- `queryVector`: A list of doubles representing the query vector.
- `includeMetadata`: Boolean flag to include metadata in the response.
- `includeValues`: Boolean flag to include vector values in the response.
- `top_k`: Integer specifying the number of top results to return.
#### Usage 
```java
QueryRequest queryRequest = QueryRequest.builder()
.indexName("myIndex")
.queryVector(Arrays.asList(1.0, 2.0, 3.0))
.includeMetadata(true)
.includeValues(true)
.top_k(5)
.build();
```
**QueryResponse**
**Description**: Represents the response from a query operation.
**Fields**:
- `id`: UUID of the vector.
- `score`: The score of the vector.
- `values`: List of doubles representing the vector values.
- `metadata`: String containing JSON metadata associated with the vector.

----
**UpsertRequest**
**Description**: Represents the request body for upsert operations.
**Fields**:
- `indexName`: Name of the index where vectors are upserted.
- `nameSpace`: Namespace of the index.
- `upsertVectorsList`: List of UpsertVector objects to be upserted.
#### Usage 
```java
List<UpsertVector> upsertVectors = Arrays.asList(
new UpsertVector("vectorId1", Arrays.asList(1.0, 2.0, 3.0), "{\"key1\":\"value1\"}"),
new UpsertVector("vectorId2", Arrays.asList(4.0, 5.0, 6.0), "{\"key2\":\"value2\"}")
);
```
```java
UpsertRequest upsertRequest = UpsertRequest.builder()
.indexName("myIndex")
.nameSpace("myNamespace")
.upsertVectorsList(upsertVectors)
.build();
```

**UpsertVector**
**Description**: Represents a single vector in an upsert operation.
**Fields**:
- `id`: Unique identifier for the vector.
- `values`: List of doubles representing the vector.
- `metadata`: String containing JSON metadata associated with the vector.
----
**DeleteRequest**
**Description**: Represents the request body for delete operations.
**Fields**:
- `indexName`: Name of the index from which vectors are deleted.
- `namespace`: Namespace of the index.
- `ids`: Array of string IDs representing the vectors to be deleted.
- `deleteAll`: Boolean flag to delete all vectors (not supported in GCP Starter environments).
#### Usage 
```java
DeleteRequest deleteRequest = DeleteRequest.builder()
.indexName("myIndex")
.namespace("myNamespace")
.ids(new String[]{"vectorId1", "vectorId2"})
.build();
```
----
**FetchRequest**
**Description**: Represents the request body for fetch operations.
**Fields**:
- `indexName`: Name of the index from which vectors are fetched.
- `nameSpace`: Namespace of the index.
- `ids`: Array of string IDs representing the vectors to be fetched.

Usage:
```java
FetchRequest fetchRequest = FetchRequest.builder()
.indexName("indexName")
.nameSpace("nameSpace")
.ids(new String[]{"id1", "id2"})
.build();
```

**FetchResponse**
**Description**: Represents the response from a fetch operation.
**Fields**:
- `id`: UUID of the fetched vector.
- `values`: List of doubles representing the vector values.
- `nameSpace`: Namespace of the index from which the vector was fetched.
- `indexName`: Name of the index from which the vector was fetched.
- `additionalProp`: Additional properties associated with the vector.
- `sparseValues`: Sparse representation of vector values, if applicable.
- `metadata`: String containing JSON metadata associated with the vector.

Usage:
```java
// Typically used to capture and process the response from a fetch operation
FetchResponse fetchResponse = /* response from fetch operation */;
```
----


### Contributing
Contributions to the Pinecone Java Client are welcome! 
## License
This project is licensed under MIT License.

## Disclaimer
This is an unofficial client library for PineconeDB and is not affiliated with, maintained, authorized, endorsed, or sponsored by Pinecone Systems Inc.