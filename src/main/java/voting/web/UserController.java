package voting.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import voting.dto.MessageTransfer;
import voting.dto.Vote;
import voting.dto.VoteDao;
import voting.dto.VoteKind;
import voting.exceptions.VoteException;
import voting.service.VotingService;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
public class UserController {
    private final VotingService votingService;

    @Autowired
    public UserController(VotingService votingService) {
        this.votingService = votingService;
    }

    @GetMapping(value = "/create/{voteName}")
    public VoteDao createVoting(@PathVariable("voteName") String voteName,
                                @RequestParam("themes") List<String> themes,
                                HttpServletRequest httpServletRequest) {

//        return votingService.createVote(voteName);
        VoteDao voteDao = new VoteDao();
        voteDao.setVoteName(voteName);
        voteDao.setVoteOptions(themes.stream().map(VoteKind::new).collect(Collectors.toList()));

        StringBuffer requestURL = httpServletRequest.getRequestURL();
        String substring = requestURL.substring(0, requestURL.indexOf("/"));

        voteDao.setDomainName(substring);
        return votingService.createVote(voteDao);
    }

    @GetMapping(value = "/addNew")
    public void addThemes(HttpServletRequest httpServletRequest) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        System.out.println(request.getRemoteAddr());

        List<Cookie> x = Arrays.asList(httpServletRequest.getCookies());
        System.out.println(httpServletRequest.getRequestURL());

    }

    @GetMapping(value = "/vote/{uri}/{kind}")
    public MessageTransfer newVote(@PathVariable String uri, @PathVariable String kind, HttpServletRequest request) {
        return votingService.vote(new Vote(uri, kind, request.getRequestedSessionId()));
    }

    @GetMapping(value = "/statistic/{uri}")
    public VoteDao getStatistic(@PathVariable String uri) throws VoteException {
        return votingService.getStatistic(uri);
    }

    @GetMapping(value = "/stop/{voteUri}")
    public MessageTransfer stopVoting(@PathVariable String voteUri) {
        return votingService.stopVoting(voteUri);
    }

    @GetMapping(value = "/clear")
    public void clear() {
        votingService.clear();
    }

    @GetMapping("showAll")
    public List<VoteDao> showAll() {
        return votingService.showAll();
    }

    @GetMapping("/start/{voteUri}")
    public MessageTransfer startVoting(@PathVariable String voteUri) {
        return votingService.startVoting(voteUri);
    }
}
