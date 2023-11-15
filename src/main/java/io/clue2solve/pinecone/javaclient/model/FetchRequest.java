package io.clue2solve.pinecone.javaclient.model;

import lombok.*;
import org.json.JSONObject;

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
    public String toString() {
        JSONObject reqJson = new JSONObject();
        reqJson.put("indexName", this.getIndexName());
        reqJson.put("nameSpace", this.getNameSpace());
        reqJson.put("ids", this.getIds());
        return reqJson.toString();
    }


    //getIDsAsString returns a string of IDs separated by commas
    public String getIDsAsString() {
        String idsString = "";
        for (String id : ids) {
            idsString += id + ",";
        }
        return idsString.substring(0, idsString.length() - 1);
    }
}
