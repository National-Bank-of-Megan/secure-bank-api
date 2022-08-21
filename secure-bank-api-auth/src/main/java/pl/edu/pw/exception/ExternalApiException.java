package pl.edu.pw.exception;

public class ExternalApiException extends RuntimeException {
    public ExternalApiException() {
        super("Error connecting to exchangerate.host API");
    }
}
