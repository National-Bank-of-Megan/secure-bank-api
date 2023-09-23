package pl.edu.pw.auth.exception;

public class ExternalApiException extends RuntimeException {
    public ExternalApiException() {
        super("Error connecting to exchangerate.host API");
    }
}
