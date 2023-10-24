package io.clue2solve.pinecone.javaclient.model;

import lombok.*;
import org.json.JSONObject;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryRequest {
    private String indexName;
    private List<Double> queryVector;
    private boolean includeMetadata;
    private boolean includeValues;
    private int top_k = 10;

    public  JSONObject getRequestAsJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("indexName", this.getIndexName());
        jsonObject.put("includeValues", this.isIncludeValues());
        jsonObject.put("includeMetadata", this.isIncludeMetadata());
        jsonObject.put("top_k", getTop_k());
        jsonObject.put("vector", this.getQueryVector());

        return jsonObject;
    }

    public  String toString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("indexName", this.getIndexName());
        jsonObject.put("includeValues", this.isIncludeValues());
        jsonObject.put("includeMetadata", this.isIncludeMetadata());
        jsonObject.put("top_k", getTop_k());
        jsonObject.put("queryVector", this.getQueryVector());

        return jsonObject.toString();
    }
}