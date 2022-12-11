package pl.edu.pw.util;

import org.hibernate.query.criteria.internal.SelectionImplementor;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import pl.edu.pw.domain.Account;

import java.util.Collection;

public class CurrentUserUtil {

    public static Account getCurrentUser() {
        return (Account) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    public static Collection<? extends GrantedAuthority> getCurrentAuthenticationPrincipleAuthorities(){
        return SecurityContextHolder.getContext().getAuthentication().getAuthorities();
    }
}
