package io.clue2solve.pinecone.javaclient.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryRequest {
    private String indexName;
    private List<Double> queryVector;
    private boolean includeMetadata;
    private boolean includeValues;
}