package pl.edu.pw.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountDTO {
    private String clientId;
    private String accountNumber;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
}
