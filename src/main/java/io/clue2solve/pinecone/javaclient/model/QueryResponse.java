package io.clue2solve.pinecone.javaclient.model;

import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

public class QueryResponse {

    @Getter
    @Setter
    private UUID id;

    @Getter
    @Setter
    private double score;

    @Getter
    @Setter
    private List<Double> values;

    @Getter
    @Setter
    private String metadata; // this is a stringified JSON object, so you can parse it into a JSON object.
    //TODO : check if this is a stringified JSON object or a JSON object

    //toString() method
    @Override
    public String toString() {
        return "QueryResponse{" +
                "id=" + id +
                ", score=" + score +
                ", values=" + values +
                ", metadata='" + metadata + '\'' +
                '}';
    }


}
