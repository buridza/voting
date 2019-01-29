package voting.service;

import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import voting.dto.MessageTransfer;
import voting.dto.Vote;
import voting.dto.VoteBuilder;
import voting.dto.VoteDao;
import voting.dto.VoteKind;
import voting.exceptions.VoteException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.springframework.util.StringUtils.isEmpty;
import static voting.utils.Utils.createUri;

@Service
public class VotingService implements IVotingService {

    private final List<VoteDao> storage = Collections.synchronizedList(new ArrayList<>());

    public VoteDao createVote(VoteDao voteDao) {
        if (voteDao != null) {
            voteDao.setAdminId("");
            voteDao.setLocked(false);
            voteDao.setVoteUri(setNewUri());
            voteDao.setStatisticUri(setNewStatisticUri());
            storage.add(voteDao);
        }

        return voteDao;
    }

    private synchronized void setNewExpireDateTime(VoteDao voteDao) {
        if (voteDao.getExpireDate() == null) {
            voteDao.setExpireDate(LocalDateTime.now().plusHours(1));
        } else if (voteDao.getExpireDate().isBefore(LocalDateTime.now())) {
            voteDao.setExpireDate(LocalDateTime.now().plusHours(1));
        }
    }

    private synchronized String setNewUri() {
        List<String> uris = storage.stream().map(VoteDao::getVoteUri).collect(Collectors.toList());
        List<String> statisticUris = storage.stream().map(VoteDao::getStatisticUri).collect(Collectors.toList());

        while (true) {
            String voteUri = createUri();
            if (uris.contains(voteUri) || statisticUris.contains(voteUri))
                continue;
            return voteUri;
        }
    }

    private synchronized String setNewStatisticUri() {
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

            if (vote != null)
                VoteBuilder
                        .newBuild(storage, vote)
                        .isStarted()
                        .voteDateIsExpire()
                        .validateByUserID()
                        .increment();

            messageTransfer.setSuccessfulMessage("Ваш голос засчитан");
        } catch (Exception e) {
            messageTransfer.setStatusCode(1);
            messageTransfer.setErrorMessage(e.getMessage());
        }
        return messageTransfer;
    }

    public synchronized List<VoteKind> getStatistic(String statisticUri) throws VoteException {
        if (isEmpty(statisticUri)) {
            throw new VoteException("Статистика не доступна");
        } else {
            Optional<VoteDao> first = storage.stream().filter(voteDao -> voteDao.getStatisticUri().contains(statisticUri)).findFirst();
            if (first.isPresent()) {
                return first.get().getVoteOptions();
            } else throw new VoteException("Статистика не доступна");
        }
    }

    @Override
    public synchronized VoteDao updateVote(VoteDao voteDao) {
        if (voteDao != null) {
            storage.stream().filter(dao -> dao.getVoteUri().equals(voteDao.getVoteUri())).findFirst().ifPresent(dao -> {
                dao.setVoteOptions(voteDao.getVoteOptions());
                dao.setExpireDate(voteDao.getExpireDate());
                dao.setVoteName(voteDao.getVoteName());
            });
        }
        return voteDao;
    }


    public synchronized MessageTransfer clear() {
        if (storage.stream().noneMatch(VoteDao::isLocked)) {
            storage.clear();
            return MessageTransfer.builder().successfulMessage("Хранилище очищено").build();
        }
        return MessageTransfer.builder().errorMessage("Не все голосования завершены").statusCode(1).build();
    }

    public List<VoteDao> showAll() {
        return storage;
    }

    public synchronized MessageTransfer stopVoting(String voteUri) {
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

    public synchronized MessageTransfer startVoting(String voteUri) {
        Optional<VoteDao> first = storage.stream().filter(dao -> !isEmpty(dao.getVoteUri())).filter(uri -> uri.getVoteUri().equals(voteUri)).findFirst();
        if (first.isPresent()) {
            VoteDao voteDao = first.get();
            voteDao.setLocked(true);
            voteDao.getVoteOptions().forEach(voteKind -> voteKind.setVoteCount(0));
            voteDao.setAdminId(((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getRequestedSessionId());
            setNewExpireDateTime(voteDao);
            return new MessageTransfer("Голосование запущено", 0);
        }
//            return new MessageTransfer(1, "У вас нет прав на управление этим голосованием");
        return new MessageTransfer(1, "Голосование не найдено");
    }

}
