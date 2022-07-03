package pl.edu.pw.auth.logic;

import java.security.SecureRandom;
import java.util.Set;

public class CredentialGenerator {
    private static final int ACCOUNT_NUMBER_LENGTH = 26;


    public static String generateUniqueAccountNumber(Set<String> existingAccountNumbers) {
        SecureRandom random = new SecureRandom();
        String uniqueAccountNumber = null;
        boolean accountNumberExists = true;
        do {
            String accountNumber = generateAccountNumber(random);
            if (!existingAccountNumbers.contains(accountNumber)) {
                accountNumberExists = false;
                uniqueAccountNumber = accountNumber;
            }
        } while(accountNumberExists);

        return uniqueAccountNumber;
    }

    private static String generateAccountNumber(SecureRandom random) {
        int minDigit = 0;
        int maxDigit = 9;
        StringBuilder accountNumber = new StringBuilder(ACCOUNT_NUMBER_LENGTH);

        for (int i = 0; i < ACCOUNT_NUMBER_LENGTH; i++) {
            int randomDigit = random.nextInt(minDigit, maxDigit+1);
            accountNumber.append(randomDigit);
        }
        return accountNumber.toString();
    }
}
