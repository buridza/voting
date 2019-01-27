package voting.service;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import voting.dto.MessageTransfer;
import voting.dto.Vote;
import voting.dto.VoteDao;
import voting.dto.VoteKind;
import voting.exceptions.VoteException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.isEmpty;
import static voting.utils.Utils.createUri;

@Service
public class VotingService {

    private List<VoteDao> storage = new ArrayList<>();

    public VoteDao createVote(VoteDao voteDao) {
        voteDao.setVoteUri(setNewUri());
        voteDao.setStatisticUri(setNewStatisticUri());
//        setNewExpireDateTime(voteDao);

        storage.add(voteDao);

        return voteDao;
    }

    private void setNewExpireDateTime(VoteDao voteDao) {
        if (voteDao.getExpireDate() == null) {
            voteDao.setExpireDate(LocalDateTime.now().plusHours(1));
        } else if (voteDao.getExpireDate().isBefore(LocalDateTime.now())) {
            voteDao.setExpireDate(LocalDateTime.now().plusHours(1));
        }
    }

    private String setNewUri() {
        List<String> uris = storage.stream().map(VoteDao::getVoteUri).collect(Collectors.toList());
        List<String> statisticUris = storage.stream().map(VoteDao::getStatisticUri).collect(Collectors.toList());

        while (true) {

            String voteUri = createUri();
            if (uris.contains(voteUri) || statisticUris.contains(voteUri))
                continue;
            return voteUri;
        }
    }

    private String setNewStatisticUri() {
        List<String> uris = storage.stream().map(VoteDao::getStatisticUri).collect(Collectors.toList());
        List<String> statisticUris = storage.stream().map(VoteDao::getStatisticUri).collect(Collectors.toList());
        while (true) {
            String statisticUri = createUri();
            if (uris.contains(statisticUri) || statisticUris.contains(statisticUri))
                continue;
            return statisticUri;
        }
    }

    public MessageTransfer vote(Vote vote) {
        MessageTransfer messageTransfer = new MessageTransfer();
        try {
            Optional<VoteDao> voteTheme = storage.stream().filter(voteDao -> voteDao.getVoteUri().equals(vote.getUri())).findFirst();
            if (voteTheme.isPresent()) {
                if (voteTheme.get().isLocked()) {

                    if (!voteTheme.get().getExpireDate().isBefore(LocalDateTime.now())) {
                        Optional<String> cookie = voteTheme.get().getExcludeId().stream().filter(cookies -> cookies.equals(vote.getCookie())).findFirst();
                        if (!cookie.isPresent()) {
                            Optional<VoteKind> voteIncrement = voteTheme.get().getVoteOptions().stream().filter(voteKind -> voteKind.getKind().equals(vote.getVoteKind())).findFirst();
                            if (voteIncrement.isPresent()) {
                                VoteKind voteKind = voteIncrement.get();
                                voteKind.setVoteCount(voteKind.getVoteCount() + 1);
                                voteTheme.get().getExcludeId().add(vote.getCookie());
                            } else throw new VoteException("Ваш вариант не найден");
                        } else throw new VoteException("Ваш голос уже засчитан");
                    } else throw new VoteException("Голосование завершено");
                } else throw new VoteException("Голосование ещё не началось");
            } else throw new VoteException("Голосование не найдено");
            messageTransfer.setSuccessfulMessage("Ваш голос засчитан");
        } catch (Exception e) {
            messageTransfer.setStatusCode(1);
            messageTransfer.setErrorMessage(e.getMessage());
        }
        return messageTransfer;
    }

    public VoteDao getStatistic(String statisticUri) throws VoteException {
        if (isEmpty(statisticUri)) {
            throw new VoteException("Статистика не доступна");
        } else {
            Optional<VoteDao> first = storage.stream().filter(voteDao -> voteDao.getStatisticUri().contains(statisticUri)).findFirst();
            if (first.isPresent()) {
                return first.get();
            } else throw new VoteException("Статистика не доступна");
        }
    }

    public void clear() {
        System.out.println("Очищен");
        storage.clear();
    }

    public List<VoteDao> showAll() {
        return storage;
    }

    public MessageTransfer stopVoting(String voteUri) {
        Optional<VoteDao> first = storage.stream().filter(dao -> !isEmpty(dao.getVoteUri())).filter(uri -> uri.getVoteUri().equals(voteUri)).findFirst();
        if (first.isPresent()) {
            VoteDao voteDao = first.get();
            if (voteDao.getAdminId().equals(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getRequestedSessionId())) {
                first.get().setLocked(false);
                return new MessageTransfer("Голосование остановлено", 0);
            }
            return new MessageTransfer(1, "У вас нет прав на управление этим голосованием");
        }
        return new MessageTransfer(1, "Голосование не найдено");
    }

    public MessageTransfer startVoting(String voteUri) {
        Optional<VoteDao> first = storage.stream().filter(dao -> !isEmpty(dao.getVoteUri())).filter(uri -> uri.getVoteUri().equals(voteUri)).findFirst();
        if (first.isPresent()) {
            VoteDao voteDao = first.get();
            voteDao.setLocked(true);
            voteDao.setAdminId(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getRequestedSessionId());
            setNewExpireDateTime(voteDao);
            return new MessageTransfer("Голосование запущено", 0);
        }
//            return new MessageTransfer(1, "У вас нет прав на управление этим голосованием");
        return new MessageTransfer(1, "Голосование не найдено");
    }

}
