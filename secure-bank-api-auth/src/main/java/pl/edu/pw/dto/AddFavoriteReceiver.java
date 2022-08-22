package pl.edu.pw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class AddFavoriteReceiver {

    @NotBlank
    private String name;

    @Pattern(regexp="[\\d]{26}")
    private String accountNumber;
}
