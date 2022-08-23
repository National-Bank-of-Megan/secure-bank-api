package pl.edu.pw.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class VerifyCodeRequest {

    @NotBlank(message = "Client id cannot be blank")
    @Size(max = 8, message = "Client id can be no longer than 8 characters")
    private String clientId;

    @Pattern(regexp = "[\\d]{6}", message = "One time password must be 6 digits long")
    private String code;
}
