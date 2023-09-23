package pl.edu.pw.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdatedAccountHash {
    private String passwordPart;
    private String passwordPartCharactersPosition;
}
