package io.clue2solve.pinecone.javaclient.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
        return "DeleteRequest(namespace=" + this.getNamespace() + ", ids=" + java.util.Arrays.deepToString(this.getIds()) + ", deleteAll=" + this.isDeleteAll() + ")";
    }

}
