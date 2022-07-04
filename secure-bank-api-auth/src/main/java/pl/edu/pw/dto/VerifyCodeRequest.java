package pl.edu.pw.dto;

import lombok.Data;

@Data
public class VerifyCodeRequest {
    private String clientId;
    private String code;
}
