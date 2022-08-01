package pl.edu.pw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class AddFavoriteReceiver {

    @NotBlank
    private String name;

    @NotBlank
    private String accountNumber;
}
