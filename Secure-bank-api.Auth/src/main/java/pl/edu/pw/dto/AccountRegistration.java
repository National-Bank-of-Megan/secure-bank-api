package pl.edu.pw.dto;

import lombok.Data;

@Data
public class AccountRegistration {
    private String socialSecurityNumber;
    private String password;
}