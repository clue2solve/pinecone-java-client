package io.clue2solve.pinecone.javaclient.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.json.JSONObject;

/**
 * This class is used to create a DeleteRequest object, which is then used to create a JSON object
 * that is sent to the Pinecone server.
 *  * Currently supports only the Delete operation for Common environments,
 *  * the DeleteAll operation is not supported on GCP Starter environments, and thus the client does not either

 */
@Getter
@Setter
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
    public String toString() {
        //return a JSON String lile this : {\"deleteAll\":\"false\",\"ids\":[\"abcd1234\"],\"namespace\":\"default\"}
        JSONObject jsonObject = new JSONObject();
        //jsonObject.put("indexName", this.getIndexName()); // Delete works on namespace level, not index level
        jsonObject.put("namespace", this.getNamespace());
        //jsonObject.put("deleteAll", this.isDeleteAll()); // not supported for GCP Starter environments, thus not supported here
        jsonObject.put("ids", this.getIds());
        return jsonObject.toString();
    }

}
