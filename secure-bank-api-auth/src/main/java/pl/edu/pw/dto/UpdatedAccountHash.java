package pl.edu.pw.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UpdatedAccountHash {
    private String passwordPart;
    private String passwordPartCharactersPosition;
}
