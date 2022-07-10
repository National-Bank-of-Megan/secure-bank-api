package pl.edu.pw.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.edu.pw.auth.logic.CredentialGenerator;

import javax.persistence.*;
import java.util.*;

@Data
@Entity
@Table
@EqualsAndHashCode
public class Account implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long clientNumber;

    @Column(unique = true)
    private String accountNumber;

    @Column
    private String password;

    @OneToMany(
        mappedBy = "account",
        cascade = CascadeType.ALL,
        orphanRemoval = true
    )
    private List<AccountHash> accountHashList = new ArrayList<>();

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "current_hash_id")
    private AccountHash currentAuthenticationHash;

    @Column
    @OneToMany(mappedBy = "account")
    private Set<CurrencyExchange> exchanges;

    @OneToMany(
            mappedBy = "account",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Device> accountDevices = new ArrayList<>();

//    @Column
//    @OneToMany(mappedBy = )
//    private Set<Transfer> transfers;

    public Account(Set<String> existingAccountNumbers, String encryptedPassword) {
        this.accountNumber = generateAccountNumber(existingAccountNumbers);
        this.password = encryptedPassword;
    }

    public Account(Long clientNumber, String password) {
        this.clientNumber = clientNumber;
        this.password = password;
    }

    public Account() {

    }

    private String generateAccountNumber(Set<String> existingAccountNumbers) {
        return CredentialGenerator.generateUniqueAccountNumber(existingAccountNumbers);
    }

    public void addAllAccountHashes(Collection<AccountHash> accountHashes) {
        for (AccountHash accountHash : accountHashes) {
            addAccountHash(accountHash);
        }
    }

    public void addAccountHash(AccountHash accountHash) {
        if (accountHash != null) {
            this.accountHashList.add(accountHash);
            accountHash.setAccount(this);
        }
    }

    public void removeAccountHash(AccountHash accountHash) {
        if (accountHash != null) {
            this.accountHashList.remove(accountHash);
            accountHash.setAccount(null);
        }
    }

    public void addDevice(Device device) {
        if (device != null) {
            this.accountDevices.add(device);
            device.setAccount(this);
        }
    }

    public void removeDeice(Device device) {
        if (device != null) {
            this.accountDevices.remove(device);
            device.setAccount(null);
        }
    }

    public void setCurrentAuthenticationHash(AccountHash accountHash) {
        this.currentAuthenticationHash = accountHash;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return clientNumber.toString();
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
