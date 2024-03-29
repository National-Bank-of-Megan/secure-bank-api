package pl.edu.pw.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PartPasswordHash {
    private String hash;
    private List<Integer> digitsLocations;
}
