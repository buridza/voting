package voting.dto;

import voting.exceptions.VoteException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class VoteBuilder {
    private VoteDao voteDao;

    private VoteBuilder(VoteDao voteDao) {
        this.voteDao = voteDao;
    }


    public static Builder newBuild(List<VoteDao> storage, Vote vote) throws VoteException {
        Optional<VoteDao> voteTheme = storage.stream().filter(voteDao -> voteDao.getVoteUri().equals(vote.getUri())).findFirst();
        if (voteTheme.isPresent()) {
            return new VoteBuilder(voteTheme.get()).new Builder(vote);
        }
        throw new VoteException("Голосование не найдено");

    }

    public class Builder {
        private Vote vote;

        private Builder(Vote vote) {
            this.vote = vote;
        }

        public Builder isStarted() throws VoteException {
            if (voteDao.isLocked()) {
                return this;
            }
            throw new VoteException("Голосование ещё не началось");
        }

        public Builder voteDateIsExpire() throws VoteException {
            if (voteDao.getExpireDate().isBefore(LocalDateTime.now())) {
                return this;
            }
            throw new VoteException("Голосование завершено");
        }

        public Builder validateByUserID() throws VoteException {
            Optional<String> first = voteDao.getExcludeId().stream().filter(cookies -> cookies.equals(vote.getCookie())).findFirst();
            if (first.isPresent()) {
                throw new VoteException("Ваш голос уже засчитан");
            }
            return this;
        }

        private VoteKind findVoteKind() throws VoteException {
            Optional<VoteKind> voteIncrement = voteDao.getVoteOptions().stream().filter(voteKind -> voteKind.getKind().equals(vote.getVoteKind())).findFirst();
            if (voteIncrement.isPresent()) {
                return voteIncrement.get();
            }
            throw new VoteException("Ваш вариант не найден");
        }

        public synchronized VoteDao increment() throws VoteException {
            findVoteKind().increment();
            return voteDao;
        }

    }
}
