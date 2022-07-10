package pl.edu.pw.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class AccountRegistration {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    private String publicIp;

    private String localIp;
}
