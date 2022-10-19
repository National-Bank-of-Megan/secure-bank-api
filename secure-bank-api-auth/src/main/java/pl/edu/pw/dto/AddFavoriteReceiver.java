package pl.edu.pw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class AddFavoriteReceiver {

    @NotBlank(message = "Favorite receiver name cannot be blank")
    @Size(max = 50, message = "Favorite receiver name cannot be longer than 50 characters")
    private String name;

    @NotBlank(message = "Account number cannot be blank")
    @Pattern(regexp = "[\\d]{26}", message = "Account number must be 26 digits long")
    private String accountNumber;
}
