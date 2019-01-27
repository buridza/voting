package voting.exceptions;

public class VoteException extends Exception {
    public VoteException(String message) {
        super(message);
    }

    public VoteException(String message, Throwable cause) {
        super(message, cause);
    }

    public VoteException(Throwable cause) {
        super(cause);
    }

    public VoteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
