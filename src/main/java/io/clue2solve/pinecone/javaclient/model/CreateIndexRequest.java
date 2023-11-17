package io.clue2solve.pinecone.javaclient.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class CreateIndexRequest {
    //create fields that match : {\"metric\":\"cosine\",\"pods\":1,\"replicas\":1,\"pod_type\":\"p1.x1\",\"metadata_config\":{\"newKey\":\"New Value\",\"newKey-1\":\"New Value\"},\"name\":\"index-name\",\"dimension\":5}
    private String name;
    private String metric;
    private int dimension;
    private int replicas;
    private int pods;
    private String pod_type;
    private String metadata_config;

    //Emum representing The type of pod to use. One of s1, p1, or p2 appended with . and one of x1, x2, x4, or x8. For example, s1.x1, p1.x2, or p2.x4.
    public enum PodType {
        s1x1{
            public String toString() {
                return "s1.x1";
            }
        },
        s1x2{
            public String toString() {
                return "s1.x2";
            }
        },
        s1x4{
            public String toString() {
                return "s1.x4";
            }
        },
        s1x8 {
            public String toString() {
                return "s1.x8";
            }
        },
        p1x1 {
            public String toString() {
                return "p1.x1";
            }
        },
        p1x2 {
            public String toString() {
                return "p1.x2";
            }
        },
        p1x4 {
            public String toString() {
                return "p1.x4";
            }
        },
        p1x8 {
            public String toString() {
                return "p1.x8";
            }
        },
        p2x1 {
            public String toString() {
                return "p2.x1";
            }
        },
        p2x2 {
            public String toString() {
                return "p2.x2";
            }
        },
        p2x4 {
            public String toString() {
                return "p2.x4";
            }
        },
        p2x8 {
            public String toString() {
                return "p2.x8";
            }
        }

        //toString() for each with returned value looking like s1.x1, p1.x2, or p2.x4

    }
    public String toString() {
        return "{\"name\":\"" + name + "\",\"metric\":\"" + metric + "\",\"dimension\":" + dimension + ",\"replicas\":" + replicas + ",\"pods\":" + pods + ",\"pod_type\":\"" + pod_type + "\",\"metadata_config\":" + metadata_config + "}";
    }

}
