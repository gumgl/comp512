package transactions;

public class InvalidTransactionIDException extends RuntimeException {
    public InvalidTransactionIDException(int tid) {
        super(String.format("%d is not a valid transaction ID",tid));
    }
}
