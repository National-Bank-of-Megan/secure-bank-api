package pl.edu.pw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.edu.pw.security.validation.ValidPassword;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
public class ChangePassword {

    @NotBlank
    private String oldPassword;

    @ValidPassword
    private String newPassword;

    @Pattern(regexp="[\\d]{6}")
    private String otpCode;
}
