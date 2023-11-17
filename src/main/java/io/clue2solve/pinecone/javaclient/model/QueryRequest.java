package io.clue2solve.pinecone.javaclient.model;

import lombok.*;
import org.json.JSONObject;

import java.util.List;

/**
 * This class is used to create a JSON object for the request body of the query API.
 * The JSON object is then converted to a string and sent as the request body.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class
QueryRequest {
    @NonNull
    private String namespace;
    @NonNull
    private String indexName;
    @NonNull
    private List<Double> vector;
    private boolean includeMetadata;
    private boolean includeValues;
    @NonNull
    private int top_k = 10;

    /**
     * This method is used to create a JSON object from the QueryRequest object.
     * @return JSONObject
     */
    public  JSONObject getRequestAsJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("namespace", this.getNamespace());
        jsonObject.put("indexname", this.getIndexName());
        jsonObject.put("includeValues", this.isIncludeValues());
        jsonObject.put("includeMetadata", this.isIncludeMetadata());
        jsonObject.put("top_k", getTop_k());
        jsonObject.put("vector", this.getVector());

        return jsonObject;
    }

    /**
     * This method is used to convert the JSON object to a string.
     * @return JSONObject Stringified JSON object.
     */
    @Override
    public  String toString() {
        return this.getRequestAsJson().toString();
    }
}


