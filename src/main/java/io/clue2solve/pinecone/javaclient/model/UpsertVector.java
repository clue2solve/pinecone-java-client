package io.clue2solve.pinecone.javaclient.model;

import lombok.*;
import org.json.JSONObject;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpsertVector {
    private String id;
    private List<Double> values;
    private String metadata; //TODO: Check if this is a stringified JSON object or a JSON object

    //Create a JSON object from this object and then return it as a string toString()
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("values", values);
        jsonObject.put("metadata", metadata);
        return String.valueOf(jsonObject);
    }
}
