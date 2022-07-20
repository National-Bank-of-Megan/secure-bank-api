package pl.edu.pw.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.edu.pw.auth.logic.CredentialGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.*;

@Data
@NoArgsConstructor
@Entity
@Table
@EqualsAndHashCode(exclude = {"exchanges","accountDevices"})
public class Account implements UserDetails {
    private static final int ACCOUNT_NUMBER_LENGTH = 26;
    private static final int CLIENT_ID_LENGTH = 8;

    @Id
    private String clientId;

    @Column(unique = true)
    @NotBlank
    private String accountNumber;

    @Column
    private String secret;

    @Column
    private boolean shouldBeVerified = false;

    @Column
    @NotBlank
    private String password;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY, optional = false)
    private AccountDetails accountDetails;

    @OneToMany(
            mappedBy = "account",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<AccountHash> accountHashList = new ArrayList<>();

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "current_hash_id")
    private AccountHash currentAuthenticationHash;

    @OneToMany(
            mappedBy = "account",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @MapKey(name="currency")
    private Map<Currency, SubAccount> subAccounts = new HashMap<>();

    @Column
    @OneToMany(mappedBy = "account")
    private Set<CurrencyExchange> exchanges;

    @OneToMany(
            mappedBy = "account",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Device> accountDevices = new ArrayList<>();

    @OneToMany(
            mappedBy = "receiver",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Transfer> receiverTransfers = new ArrayList<>();

    @OneToMany(
            mappedBy = "sender",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Transfer> senderTransfers = new ArrayList<>();

    public Account(Set<String> existingClientIds, Set<String> existingAccountNumbers, String encryptedPassword) {
        this.clientId = generateClientId(existingClientIds);
        this.accountNumber = generateAccountNumber(existingAccountNumbers);
        this.password = encryptedPassword;
    }

    public void setAccountDetails(AccountDetails accountDetails) {
        if (accountDetails == null) {
            if (this.accountDetails != null) {
                this.accountDetails.setAccount(null);
            }
        } else {
            accountDetails.setAccount(this);
        }
        this.accountDetails = accountDetails;
    }

    private String generateClientId(Set<String> existingClientIds) {
        return CredentialGenerator.generateUniqueCredentials(existingClientIds, CLIENT_ID_LENGTH);
    }

    private String generateAccountNumber(Set<String> existingAccountNumbers) {
        return CredentialGenerator.generateUniqueCredentials(existingAccountNumbers, ACCOUNT_NUMBER_LENGTH);
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

    public void removeDevice(Device device) {
        if (device != null) {
            this.accountDevices.remove(device);
            device.setAccount(null);
        }
    }

    public void addSubAccounts(Currency[] currencies) {
        for (Currency currency : currencies) {
            addSubAccount(currency);
        }
    }

    public void addSubAccount(Currency currency) {
        this.subAccounts.put(currency, new SubAccount(this, currency));
        this.subAccounts.get(currency).setAccount(this);
    }

    public void setCurrentAuthenticationHash(AccountHash accountHash) {
        this.currentAuthenticationHash = accountHash;
    }

    public void addCurrencyBalance(Currency currency, double amount) {
        SubAccount subAccount = this.subAccounts.get(currency);
        subAccount.addToBalance(amount);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public String getUsername() {
        return clientId;
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
