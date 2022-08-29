package pl.edu.pw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class DeviceDTO {
    private Long id;
    private String name;
    private String ip;
    private LocalDateTime registrationDate;
    private LocalDateTime lastLoggedInDate;
    private boolean isCurrentDevice;
}
