package pl.edu.pw.user;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.edu.pw.auth.CredentialGenerator;

import javax.persistence.*;
import java.util.Collection;
import java.util.Set;

@Data
@Entity
@Table
public class Account implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clientId;

    @Column
    private String accountNumber;

    @Column
    private String password;

    public Account(Set<String> existingAccountNumbers, String encryptedPassword) {
        this.accountNumber = generateAccountNumber(existingAccountNumbers);
        this.password = encryptedPassword;
    }

    public Account(Long clientId, String password) {
        this.clientId = clientId;
        this.password = password;
    }

    public Account() {

    }

    private String generateAccountNumber(Set<String> existingAccountNumbers) {
        return CredentialGenerator.generateUniqueAccountNumber(existingAccountNumbers);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
