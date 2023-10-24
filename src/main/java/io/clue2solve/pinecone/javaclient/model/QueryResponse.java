package io.clue2solve.pinecone.javaclient.model;

import java.util.List;
import java.util.UUID;

import lombok.*;

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
