package pl.edu.pw.auth.exception;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ErrorMessageBody {
    private String message;
}
