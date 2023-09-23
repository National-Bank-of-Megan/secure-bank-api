package pl.edu.pw.core.util;

import lombok.experimental.UtilityClass;

import java.security.SecureRandom;
import java.util.Set;

@UtilityClass
public class DataGenerator {

    public String generateUniqueNumber(Set<String> existingNumbers, int numberLength) {
        SecureRandom random = new SecureRandom();
        String uniqueNumber = null;
        boolean numberAlreadyExists = true;
        do {
            String credential = generateRandomNumber(random, numberLength);
            if (!existingNumbers.contains(credential)) {
                numberAlreadyExists = false;
                uniqueNumber = credential;
            }
        } while (numberAlreadyExists);

        return uniqueNumber;
    }

    private String generateRandomNumber(SecureRandom random, int numberLength) {
        int minDigit = 0;
        int maxDigit = 9;
        StringBuilder accountNumber = new StringBuilder(numberLength);

        for (int i = 0; i < numberLength; i++) {
            int randomDigit = random.nextInt(minDigit, maxDigit + 1);
            accountNumber.append(randomDigit);
        }
        return accountNumber.toString();
    }
}
