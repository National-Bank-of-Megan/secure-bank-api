package pl.edu.pw.dto;

import lombok.Data;

@Data
public class AccountRegistration {
    private String password;
    private String publicIp;
    private String localIp;
}
