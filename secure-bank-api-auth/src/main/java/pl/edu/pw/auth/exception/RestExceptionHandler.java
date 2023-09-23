package pl.edu.pw.auth.exception;

import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import pl.edu.pw.core.exception.InvalidCurrencyException;

@RestControllerAdvice
@Log4j2
public class RestExceptionHandler {
    private static final String INTERNAL_SERVER_ERROR_DEFAULT_MESSAGE = "Internal server error. " +
            "Please report this issue to administrators of the system";
    private static final String AUTHENTICATION_FAILED_MESSAGE = "Invalid client id or password";
    private static final String FIELD_VALIDATION_FAILURE_DEFAULT_MESSAGE = "One of the provided fields is invalid";

    private static final String SQL_ERROR_DEFAULT_MESSAGE = "Some error happened, try again later";


    private ErrorMessageBody buildErrorMessageBody(Exception e) {
        return ErrorMessageBody.builder().message(e.getMessage()).build();
    }

    private ErrorMessageBody buildErrorMessageBody(String message) {
        return ErrorMessageBody.builder().message(message).build();
    }

    @ExceptionHandler({AuthenticationException.class})
    public ResponseEntity<Object> handleAuthenticationException(Exception e, WebRequest request) {
        if (e instanceof InsufficientAuthenticationException) {
            return new ResponseEntity<>(buildErrorMessageBody(AUTHENTICATION_FAILED_MESSAGE), HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(buildErrorMessageBody(e), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    // TODO: in the future override all messages, then remove if block
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e, WebRequest request) {
        String errorMessage = FIELD_VALIDATION_FAILURE_DEFAULT_MESSAGE;
        if (e.getFieldError() != null) {
            errorMessage = e.getFieldError().getDefaultMessage();
        }
        return new ResponseEntity<>(buildErrorMessageBody(errorMessage), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<Object> handleResourceNotFoundException(Exception e, WebRequest request) {
        return new ResponseEntity<>(buildErrorMessageBody(e), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<Object> handleIllegalArgumentException(Exception e, WebRequest request) {
        return new ResponseEntity<>(buildErrorMessageBody(e), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({InvalidCurrencyException.class})
    public ResponseEntity<Object> handleInvalidCurrencyException(Exception e, WebRequest request) {
        return new ResponseEntity<>(buildErrorMessageBody(e), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ExternalApiException.class})
    public ResponseEntity<Object> handleExternalApiException(Exception e, WebRequest request) {
        return new ResponseEntity<>(buildErrorMessageBody(e), HttpStatus.SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler({InvalidCredentialsException.class, JWTVerificationException.class, BadCredentialsException.class, LockedException.class})
    public ResponseEntity<Object> handleInvalidCredentialsException(Exception e, WebRequest request) {
        return new ResponseEntity<>(buildErrorMessageBody(e), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({DataAccessException.class})
    public ResponseEntity<Object> handleDataAccessException(Exception e, WebRequest request) {
        return new ResponseEntity<>(SQL_ERROR_DEFAULT_MESSAGE, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<Object> handleRuntimeExceptions(Exception e, WebRequest request) {
        if (e instanceof AsyncRequestTimeoutException) {
            return new ResponseEntity<>(e.getCause(), HttpStatus.BAD_REQUEST);
        }
        if (e instanceof DeviceNotFoundException)
            return new ResponseEntity<>(e.getCause(), HttpStatus.NETWORK_AUTHENTICATION_REQUIRED);
        return new ResponseEntity<>(buildErrorMessageBody(e), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handle(Exception e) {
        if (e instanceof NullPointerException) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(buildErrorMessageBody(INTERNAL_SERVER_ERROR_DEFAULT_MESSAGE), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(DeviceNotFoundException.class)
    public ResponseEntity<Object> handleDeviceNotFoundException(Exception e) {
        return new ResponseEntity<>(buildErrorMessageBody(e.getMessage()), HttpStatus.UNAUTHORIZED);
    }
}
