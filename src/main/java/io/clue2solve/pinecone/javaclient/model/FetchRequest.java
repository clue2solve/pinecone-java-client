package io.clue2solve.pinecone.javaclient.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;
import java.util.HashMap;
import java.util.Map;

/**
 * This is the request object for the fetch API.
 * It contains the indexName, namespace, and the list of ids to be fetched.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FetchRequest {
    private String indexName;
    private String nameSpace;
    private String[] ids;

    /**
     * This method is used to create a JSON object from the FetchRequest object.
     * @return JSONObject
     */
    public String toString() throws RuntimeException{
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = new HashMap<>();
        map.put("indexName", this.getIndexName());
        map.put("namespace", this.getNameSpace());
        map.put("ids", this.getIds());
        try {
            try {
                return objectMapper.writeValueAsString(map);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    //getIDsAsString returns a string of IDs separated by commas
    public String getIDsAsString() {
        StringBuilder idsString = new StringBuilder();
        for (String id : ids) {
            idsString.append(id).append(",");
        }
        return idsString.substring(0, idsString.length() - 1);
    }
}
