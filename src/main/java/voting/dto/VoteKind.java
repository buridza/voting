package voting.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class VoteKind {
    private String kind;
    private int voteCount;

    public VoteKind(String kind) {
        this.kind = kind;
    }

    public  int increment() {
        return voteCount++;
    }
}
