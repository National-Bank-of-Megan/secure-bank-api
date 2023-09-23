package pl.edu.pw.core.util;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.edu.pw.core.domain.Account;

import java.util.Collection;

public class CurrentUserUtil {

    public static Account getCurrentUser() {
        return (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static Collection<? extends GrantedAuthority> getCurrentAuthenticationPrincipleAuthorities() {
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities();
    }
}
