package io.clue2solve.pinecone.javaclient.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private int top_k = 10;

    /**
     * This method is used to create a JSON object from the QueryRequest object.
     * @return JSONObject
     */
    public String getRequestAsJson() {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = new HashMap<>();
        map.put("namespace", this.getNamespace());
        map.put("indexname", this.getIndexName());
        map.put("includeValues", this.isIncludeValues());
        map.put("includeMetadata", this.isIncludeMetadata());
        map.put("top_k", getTop_k());
        map.put("vector", this.getVector());

        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * This method is used to convert the JSON object to a string.
     * @return JSONObject Stringified JSON object.
     */
    @Override
    public  String toString() {
        return this.getRequestAsJson();
    }
}


