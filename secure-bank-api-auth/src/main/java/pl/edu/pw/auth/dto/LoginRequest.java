package pl.edu.pw.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class LoginRequest {

    @NotNull
    private String clintId;
    @NotNull
    private String password;
}
