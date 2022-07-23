package pl.edu.pw.exception;

public class SubAccountBalanceException extends RuntimeException {
    public SubAccountBalanceException(String message) {
        super(message);
    }
}
