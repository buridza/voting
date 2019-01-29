package voting.service;

import voting.dto.MessageTransfer;
import voting.dto.Vote;
import voting.dto.VoteDao;
import voting.dto.VoteKind;
import voting.exceptions.VoteException;

import java.util.List;

public interface IVotingService {
    VoteDao createVote(VoteDao voteDao);
    MessageTransfer vote(Vote vote);
    List<VoteKind> getStatistic(String statisticUri) throws VoteException;
    MessageTransfer stopVoting(String voteUri);
    MessageTransfer startVoting(String voteUri);
    List<VoteDao> showAll();
    MessageTransfer clear();

    VoteDao updateVote(VoteDao voteDao);
}
