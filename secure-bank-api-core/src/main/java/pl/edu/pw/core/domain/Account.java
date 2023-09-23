package pl.edu.pw.core.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import pl.edu.pw.core.util.DataGenerator;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.math.BigDecimal;
import java.util.*;

@Data
@Log4j2
@NoArgsConstructor
@Entity
@Table
@EqualsAndHashCode(exclude = {"exchanges", "accountDevices", "subAccounts", "accountDetails", "accountHashList", "currentAuthenticationHash"})
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

    @Column(name = "login_attempts")
    private Long loginAttempts = 0L;

    @Column
    private boolean accountNonLocked = true;

    @Column(name = "lock_time")
    private Date lockTime;

    @Column
    @NotBlank
    private String password;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL, optional = false)
    @PrimaryKeyJoinColumn
    private Klik klik;

    @Column
    private String expoPushToken;

    @OneToOne(mappedBy = "account", cascade = CascadeType.ALL,
            fetch = FetchType.EAGER, optional = false)
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
    private List<FavoriteReceiver> favoriteReceivers = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, mappedBy = "id.clientId", fetch = FetchType.EAGER)
    @MapKey(name = "id.currency")
    private Map<pl.edu.pw.core.domain.Currency, SubAccount> subAccounts = new HashMap<>();

    @Column
    @OneToMany(mappedBy = "account", fetch = FetchType.EAGER)
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
        this.klik = new Klik(clientId);
        this.klik.setAccount(this);
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
        return DataGenerator.generateUniqueNumber(existingClientIds, CLIENT_ID_LENGTH);
    }

    private String generateAccountNumber(Set<String> existingAccountNumbers) {
        return DataGenerator.generateUniqueNumber(existingAccountNumbers, ACCOUNT_NUMBER_LENGTH);
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

    public void addSubAccounts(pl.edu.pw.core.domain.Currency[] currencies) {
        for (pl.edu.pw.core.domain.Currency currency : currencies) {
            addSubAccount(currency);
        }
    }

    public void addFavoriteReceiver(FavoriteReceiver favoriteReceiver) {
        if (favoriteReceiver != null) {
            this.favoriteReceivers.add(favoriteReceiver);
            favoriteReceiver.setAccount(this);
        }
    }

    public void addSubAccount(pl.edu.pw.core.domain.Currency currency) {
        this.subAccounts.put(currency, new SubAccount(new SubAccountId(this, currency), BigDecimal.ZERO));
        this.subAccounts.get(currency).setId(new SubAccountId(this, currency));
    }

    public void setCurrentAuthenticationHash(AccountHash accountHash) {
        this.currentAuthenticationHash = accountHash;
    }

    public void addCurrencyBalance(pl.edu.pw.core.domain.Currency currency, BigDecimal amount) {
        SubAccount subAccount = this.subAccounts.get(currency);
        subAccount.addToBalance(amount);
    }

    public void chargeCurrencyBalance(Currency currency, BigDecimal amount) {
        SubAccount subAccount = this.subAccounts.get(currency);
        subAccount.chargeFromBalance(amount);
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
        return this.accountNonLocked;
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
