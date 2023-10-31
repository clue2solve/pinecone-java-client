## Pinecone Java Client
### Overview
The Pinecone Java Client is an unofficial Java library for interacting with PineconeDB, a vector database ideal for building vector search applications. This client library facilitates operations such as fetching index statistics, querying, and performing upsert and delete operations in PineconeDB.

### Features
- Fetch index statistics
- Query operations
U- psert operations
- Delete operations
- Requirements
- Java 8 or higher
- OkHttp3
- Jackson (for JSON processing)
- SLF4J (for logging)

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
**indexName**: Name of the index to query.

- queryVector: A list of doubles representing the query vector.
- includeMetadata: Boolean flag to include metadata in the response.
- includeValues: Boolean flag to include vector values in the response.
- top_k: Integer specifying the number of top results to return.

**QueryResponse**
**Description**: Represents the response from a query operation.
**Fields**:
- `id`: UUID of the vector.
- `score`: The score of the vector.
- `values`: List of doubles representing the vector values.
- `metadata`: String containing JSON metadata associated with the vector.


**UpsertRequest**
**Description**: Represents the request body for upsert operations.
**Fields**:
- `indexName`: Name of the index where vectors are upserted.
- `nameSpace`: Namespace of the index.
- `upsertVectorsList`: List of UpsertVector objects to be upserted.

**UpsertVector**
**Description**: Represents a single vector in an upsert operation.
**Fields**:
- `id`: Unique identifier for the vector.
- `values`: List of doubles representing the vector.
- `metadata`: String containing JSON metadata associated with the vector.

**DeleteRequest**
**Description**: Represents the request body for delete operations.
**Fields**:
- `indexName`: Name of the index from which vectors are deleted.
- `namespace`: Namespace of the index.
- `ids`: Array of string IDs representing the vectors to be deleted.
- `deleteAll`: Boolean flag to delete all vectors (not supported in GCP Starter environments).


### Contributing
Contributions to the Pinecone Java Client are welcome! Please read our contributing guidelines to get started.

## License
This project is licensed under MIT License.

## Disclaimer
This is an unofficial client library for PineconeDB and is not affiliated with, maintained, authorized, endorsed, or sponsored by Pinecone Systems Inc.