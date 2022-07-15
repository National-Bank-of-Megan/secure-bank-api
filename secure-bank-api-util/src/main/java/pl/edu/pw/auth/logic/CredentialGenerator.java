package pl.edu.pw.auth.logic;

import java.security.SecureRandom;
import java.util.Set;

public class CredentialGenerator {

    public static String generateUniqueCredentials(Set<String> existingCredentials, int credentialLength) {
        SecureRandom random = new SecureRandom();
        String uniqueCredential = null;
        boolean credentialExists = true;
        do {
            String credential = generateCredential(random, credentialLength);
            if (!existingCredentials.contains(credential)) {
                credentialExists = false;
                uniqueCredential = credential;
            }
        } while (credentialExists);

        return uniqueCredential;
    }

    private static String generateCredential(SecureRandom random, int credentialLength) {
        int minDigit = 0;
        int maxDigit = 9;
        StringBuilder accountNumber = new StringBuilder(credentialLength);

        for (int i = 0; i < credentialLength; i++) {
            int randomDigit = random.nextInt(minDigit, maxDigit + 1);
            accountNumber.append(randomDigit);
        }
        return accountNumber.toString();
    }
}
