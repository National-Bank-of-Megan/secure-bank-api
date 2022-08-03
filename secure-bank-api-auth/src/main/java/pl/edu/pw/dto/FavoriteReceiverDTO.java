package pl.edu.pw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class FavoriteReceiverDTO {
    private Long id;
    private String name;
    private String accountNumber;
}
