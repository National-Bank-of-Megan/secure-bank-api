package pl.edu.pw.auth.util;

import lombok.experimental.UtilityClass;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.edu.pw.auth.dto.PartPasswordHash;
import pl.edu.pw.auth.dto.UpdatedAccountHash;
import pl.edu.pw.auth.service.account.AuthServiceImpl;
import pl.edu.pw.core.domain.Account;
import pl.edu.pw.core.domain.AccountHash;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class PasswordUtil {
    public void addAccountHashes(Account account, String rawPassword, PasswordEncoder passwordEncoder) {
        List<PartPasswordHash> partPasswordHashes = PasswordHashesGenerator.generatePasswordHashes(rawPassword, passwordEncoder);
        List<AccountHash> accountHashes = partPasswordHashes.stream().map(AuthServiceImpl.AccountHashMapper::map).toList();
        account.addAllAccountHashes(accountHashes);
        account.setCurrentAuthenticationHash(accountHashes.get(0));
    }

    public void updateAccountHashes(Account account, String rawPassword, PasswordEncoder passwordEncoder) {
        List<PartPasswordHash> partPasswordHashes = PasswordHashesGenerator.generatePasswordHashes(rawPassword, passwordEncoder);
        List<UpdatedAccountHash> updatedAccountHashes = partPasswordHashes.stream().map(PasswordUtil::map).toList();
        List<AccountHash> accountHashList = account.getAccountHashList();
        for (int i = 0; i < accountHashList.size(); i++) {
            AccountHash accountHash = accountHashList.get(i);
            accountHash.setPasswordPart(updatedAccountHashes.get(i).getPasswordPart());
            accountHash.setPasswordPartCharactersPosition(updatedAccountHashes.get(i).getPasswordPartCharactersPosition());
        }
        account.setCurrentAuthenticationHash(accountHashList.get(0));
    }

    private static UpdatedAccountHash map(PartPasswordHash partPasswordHash) {
        List<String> hashLocations = partPasswordHash.getDigitsLocations().stream().map(String::valueOf).collect(Collectors.toList());
        String passwordPartCharactersPosition = String.join(" ", hashLocations);
        return new UpdatedAccountHash(
                partPasswordHash.getHash(),
                passwordPartCharactersPosition
        );
    }
}
