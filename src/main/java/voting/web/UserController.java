package voting.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import voting.dto.MessageTransfer;
import voting.dto.Vote;
import voting.dto.VoteDao;
import voting.dto.VoteKind;
import voting.exceptions.VoteException;
import voting.service.IVotingService;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@Slf4j
@RequestMapping(value = "/voting", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class UserController {
    private final IVotingService votingService;

    @Autowired
    public UserController(IVotingService votingService) {
        this.votingService = votingService;
    }


    @RequestMapping(value = "/create", method = RequestMethod.PUT)
    public VoteDao createVoting(@RequestBody VoteDao voteDao) {
        return votingService.createVote(voteDao);
    }
    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public VoteDao updateVoting(@RequestBody VoteDao voteDao) {
        return votingService.updateVote(voteDao);
    }

    @RequestMapping(value = "/vote/{uri}/{kind}", method = RequestMethod.GET)
    public MessageTransfer newVote(@PathVariable String uri, @PathVariable String kind, HttpServletRequest request) {
        return votingService.vote(new Vote(uri, kind, request.getRequestedSessionId()));
    }

    @RequestMapping(value = "/statistic/{uri}", method = RequestMethod.GET)
    public List<VoteKind> getStatistic(@PathVariable String uri) throws VoteException {
        return votingService.getStatistic(uri);
    }

    @RequestMapping(value = "/stop/{voteUri}", method = RequestMethod.GET)
    public MessageTransfer stopVoting(@PathVariable String voteUri) {
        return votingService.stopVoting(voteUri);
    }

    @RequestMapping(value = "/clear", method = RequestMethod.DELETE)
    public MessageTransfer clear() {
        return votingService.clear();
    }

    @RequestMapping(value = "/showAll", method = RequestMethod.GET)
    public List<VoteDao> showAll() {
        return votingService.showAll();
    }

    @RequestMapping(value = "/start/{voteUri}", method = RequestMethod.POST)
    public MessageTransfer startVoting(@PathVariable String voteUri) {
        return votingService.startVoting(voteUri);
    }
}
