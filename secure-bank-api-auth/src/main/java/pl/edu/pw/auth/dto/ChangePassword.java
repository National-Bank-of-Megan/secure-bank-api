package pl.edu.pw.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.edu.pw.auth.security.validation.ValidPassword;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@AllArgsConstructor
public class ChangePassword {

    @NotBlank(message = "Old password cannot be blank")
    private String oldPassword;

    @NotBlank(message = "New password cannot be blank")
    @ValidPassword(message = "Password must be from 10 to 20 characters long, must contain 1 digit, " +
            "1 upper case letter, 1 lower case letter and 1 special character")
    private String newPassword;

    @NotBlank(message = "One time password cannot be blank")
    @Pattern(regexp = "[\\d]{6}", message = "One time password must contain digits only and be 6 digits long")
    private String otpCode;
}
