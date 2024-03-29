package pl.edu.pw.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class SuccessfulRegistrationResponse {

    @NotBlank
    private String clientId;

    @NotBlank
    private String qr;
}
