package pl.edu.pw.transfer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class KlikCodeResponse {
    private String klikCode;
    private LocalDateTime generateDate;

}
