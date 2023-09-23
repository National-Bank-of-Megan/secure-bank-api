package pl.edu.pw.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor

public class JwtAuthenticationResponse {

    @NonNull
    private String accessToken;
    @NotNull
    private String refreshToken;

}
