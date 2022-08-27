package pl.edu.pw.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class WebAuthAdviceController extends ResponseEntityExceptionHandler{

    @ExceptionHandler(value={RuntimeException.class, BadCredentialsException.class, LockedException.class})
    public ResponseEntity<Object> handleLoginFailure(AuthenticationException e, HttpServletRequest request){
        System.out.println("=========in advice controller");
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("error",e.getMessage());
        return new ResponseEntity<>(body, HttpStatus.FORBIDDEN);
    }
}
