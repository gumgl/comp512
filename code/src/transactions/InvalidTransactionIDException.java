package transactions;

public class InvalidTransactionIDException extends Exception {
    public InvalidTransactionIDException(String message) {
        super(message);
    }
}
