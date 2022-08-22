package pl.edu.pw.dto;

import lombok.Data;
import pl.edu.pw.security.validation.ValidPassword;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
public class AccountRegistration {

    @Email
    @NotBlank
    @Size(max = 255)
    private String email;

    @NotBlank
    @Size(min = 3, max = 50)
//    @Pattern(regexp="^[a-zA-Z]+$") polish letters not allowed
    private String firstName;

    @NotBlank
    @Size(min = 3, max = 50)
//    @Pattern(regexp="^[a-zA-Z]+$")
    private String lastName;

    @ValidPassword(message = "Password must be from 10 to 20 characters long, must contain 1 digit, " +
            "1 upper case letter, 1 lower case letter and 1 special character")
    private String password;

    private String publicIp;

    private String localIp;
}
