package pl.edu.pw.util;

import org.springframework.security.core.context.SecurityContextHolder;
import pl.edu.pw.domain.Account;

public class CurrentUserUtil {

    public static Account getCurrentUser() {
        return (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
