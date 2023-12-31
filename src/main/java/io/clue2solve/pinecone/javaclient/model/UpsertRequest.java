package io.clue2solve.pinecone.javaclient.model;

import lombok.*;
import org.json.JSONObject;

import java.util.List;

/**
 * This is the request object for the upsert API.
 * It contains the indexName, namespace, and the list of vectors to be upserted.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpsertRequest {
    private String indexName;
    private String namespace;
    private List<UpsertVector> upsertVectorsList;

    public JSONObject getRequestAsJson() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("indexName", this.getIndexName());
        jsonObject.put("nameSpace", this.getNamespace());
        jsonObject.put("vectors", this.getUpsertVectorsList());

        return jsonObject;
    }
    public String toString() {
        JSONObject vectorsJson = new JSONObject();
        vectorsJson.put("indexName", this.getIndexName());
        vectorsJson.put("namespace", this.getNamespace());
        for (UpsertVector upsertVector : upsertVectorsList) {
            JSONObject vectorJson = new JSONObject();
//            vectorJson.put("namespace", this.getNamespace());
//            vectorJson.put("indexName", this.getIndexName());
            vectorJson.put("id", upsertVector.getId());
            vectorJson.put("values", upsertVector.getValues());
            vectorJson.put("metadata", new JSONObject(upsertVector.getMetadata()));
            vectorsJson.append("vectors", vectorJson);
        }
        return vectorsJson.toString();
    }
}



