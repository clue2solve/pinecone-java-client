package io.clue2solve.pinecone.javaclient.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.*;

import java.util.HashMap;
import java.util.Map;

/**
 * This class is used to create a DeleteRequest object, which is then used to create a JSON object
 * that is sent to the Pinecone server.
 *  * Currently supports only the Delete operation for Common environments,
 *  * the DeleteAll operation is not supported on GCP Starter environments, and thus the client does not either

 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeleteRequest {

    private String indexName;
    private String namespace;
    private String[] ids;
    private boolean deleteAll;

    /**
     * This method is used to create a JSON object from the DeleteRequest object.
     * @return JSONObject Stringified JSON object.
     */
    @Override
    public String toString() {
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> map = new HashMap<>();
        map.put("indexName", this.getIndexName());
        map.put("namespace", this.getNamespace());
        map.put("ids", this.getIds());
        try {
            return objectMapper.writeValueAsString(map);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }




}
