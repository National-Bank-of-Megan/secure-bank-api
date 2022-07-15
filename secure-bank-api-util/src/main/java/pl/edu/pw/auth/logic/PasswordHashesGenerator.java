package pl.edu.pw.auth.logic;

import org.springframework.security.crypto.password.PasswordEncoder;
import pl.edu.pw.dto.PartPasswordHash;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class PasswordHashesGenerator {
    private static final int PASSWORD_HASHES_AMOUNT = 5;
    private static final int PASSWORD_HASH_LENGTH = 6;

    public static List<PartPasswordHash> generatePasswordHashes(String password, PasswordEncoder passwordEncoder) {
        List<PartPasswordHash> passwordHashes = new ArrayList<>();
        SecureRandom random = new SecureRandom();
        int passwordLength = password.length();

        for (int i = 0; i < PASSWORD_HASHES_AMOUNT; i++) {
            boolean uniqueCombination = true;
            List<Integer> randomPasswordCharacters;
            do {
                randomPasswordCharacters = random.ints(0, passwordLength).distinct().
                        limit(PASSWORD_HASH_LENGTH).boxed().sorted().toList(); // TODO: consider defining own algorithm with more restrictions
                for (PartPasswordHash passwordHash : passwordHashes) {
                    if (passwordHash.getDigitsLocations().equals(randomPasswordCharacters)) {
                        uniqueCombination = false;
                        break;
                    }
                }
            } while (!uniqueCombination);

            StringBuilder partPassword = new StringBuilder();
            for (Integer randomPasswordCharacter : randomPasswordCharacters) {
                partPassword.append(password.charAt(randomPasswordCharacter));
            }
            String partPasswordHashed = passwordEncoder.encode(partPassword.toString());
            passwordHashes.add(new PartPasswordHash(partPasswordHashed, randomPasswordCharacters));
        }
        return passwordHashes;
    }
}
