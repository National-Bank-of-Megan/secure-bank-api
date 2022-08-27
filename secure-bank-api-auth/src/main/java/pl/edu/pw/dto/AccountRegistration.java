package pl.edu.pw.dto;

import lombok.Data;
import pl.edu.pw.security.validation.ValidPassword;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class AccountRegistration {

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is missing")
    @Size(max = 255, message = "Email cannot be longer than 255 characters")
    private String email;

    @NotBlank(message = "First name is missing")
    @Size(min = 3, max = 50, message = "First name must be from 3 to 50 characters long")
//    @Pattern(regexp="^[a-zA-Z]+$") polish letters not allowed
    private String firstName;

    @NotBlank(message = "Last name is missing")
    @Size(min = 3, max = 50, message = "Last name must be from 3 to 50 characters long")
//    @Pattern(regexp="^[a-zA-Z]+$")
    private String lastName;

    @NotBlank(message = "Password cannot be blank")
    @ValidPassword(message = "Password must be from 10 to 20 characters long, must contain 1 digit, " +
            "1 upper case letter, 1 lower case letter and 1 special character")
    private String password;

//    @NotBlank(message = "Device name cannot be blank")
//    @Size(min = 3, max = 200, message = "Device name must be from 3 to 200 characters long")
//    private String deviceName;

    @NotBlank(message = "Device fingerprint cannot be blank")
    @Size(min = 8, max = 255, message = "Device fingerprint must be from 8 to 255 characters long")
    private String deviceFingerprint;

    private String ip;
}
