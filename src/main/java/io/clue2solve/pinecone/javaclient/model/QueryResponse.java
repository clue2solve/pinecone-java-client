package io.clue2solve.pinecone.javaclient.model;

import java.util.List;
import java.util.UUID;

import lombok.*;

/**
 * This is the response object for the query API.
 * It contains the id of the vector that was queried, the score of the vector, and the metadata associated with the vector.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class QueryResponse {
    private UUID id;
    private double score;
    private List<Double> values;

    private String metadata; // this is a stringified JSON object, so you can parse it into a JSON object.

}
