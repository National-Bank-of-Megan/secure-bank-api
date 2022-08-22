package pl.edu.pw.dto;

import lombok.Data;

import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class VerifyCodeRequest {
    @Size(max = 8)
    private String clientId;
    @Pattern(regexp="[\\d]{6}")
    private String code;
}
