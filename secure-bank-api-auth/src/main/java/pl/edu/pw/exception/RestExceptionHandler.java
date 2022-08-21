package pl.edu.pw.exception;

import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
@Log4j2
public class RestExceptionHandler {
    private static final String INTERNAL_SERVER_ERROR_DEFAULT_MESSAGE = "Internal server error. " +
            "Please report this issue to administrators of the system";
    private static final String AUTHENTICATION_FAILED_MESSAGE = "Invalid client id or password";

    @ExceptionHandler({ AuthenticationException.class })
    public ResponseEntity<Object> handleAuthenticationException(Exception e, WebRequest request) {
        if (e instanceof InsufficientAuthenticationException) {
            return new ResponseEntity<>(AUTHENTICATION_FAILED_MESSAGE, HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({ IllegalArgumentException.class })
    public ResponseEntity<Object> handleIllegalArgumentException(Exception e, WebRequest request) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ InvalidCurrencyException.class })
    public ResponseEntity<Object> handleInvalidCurrencyException(Exception e, WebRequest request) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ ExternalApiException.class })
    public ResponseEntity<Object> handleExternalApiException(Exception e, WebRequest request) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler({ InvalidCredentialsException.class, JWTVerificationException.class })
    public ResponseEntity<Object> handleInvalidCredentialsException(Exception e, WebRequest request) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({ RuntimeException.class })
    public ResponseEntity<Object> handleRuntimeExceptions(Exception e, WebRequest request) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handle(Exception e) {
        if (e instanceof NullPointerException) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(INTERNAL_SERVER_ERROR_DEFAULT_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
