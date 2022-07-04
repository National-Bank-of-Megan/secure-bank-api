package pl.edu.pw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class JwtAuthenticationResponse {

    @NonNull
    private String accessToken;
    private boolean mfa;
}
