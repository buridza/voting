package voting.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class VoteDao {
    private String voteName;
    private String voteUri;
    private List<String> excludeId;
    private List<VoteKind> voteOptions;
    private LocalDateTime expireDate;
    private String statisticUri;
    private String domainName;
    private boolean locked;
    private String adminId;

    public List<VoteKind> getVoteOptions() {
        if(voteOptions == null) {
            voteOptions = new ArrayList<>();
        } return voteOptions;
    }
    public List<String> getExcludeId() {
        if(excludeId == null) {
            excludeId = new ArrayList<>();
        } return excludeId;
    }
}
