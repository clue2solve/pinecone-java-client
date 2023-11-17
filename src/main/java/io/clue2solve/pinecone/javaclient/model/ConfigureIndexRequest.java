package io.clue2solve.pinecone.javaclient.model;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * This class is used to create a JSON object for the request body of the configure index API.
 * The JSON object is then converted to a string and sent as the request body.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ConfigureIndexRequest {
    private int replicas;
    private String pod_type;
}