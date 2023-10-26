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
public class UpsertVector {
    /**
     * Unique identifier for the vector.
     */
    private String id;

    /**
     * List of values(the vector).
     */
    private List<Double> values;

    /**
     * Metadata associated with the vector.
     * Typically includes information about the vector, like the actua text ,  source and other infoirmation.
     */
    private String metadata; //TODO: Check if this is a stringified JSON object or a JSON object

    /**
     * This method is used to create a JSON object from the UpsertVector object.
     * @return stringified JSON object.
     */
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", id);
        jsonObject.put("values", values);
        jsonObject.put("metadata", metadata);
        return String.valueOf(jsonObject);
    }
}
