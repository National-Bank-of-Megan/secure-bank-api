package pl.edu.pw.domain;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Entity
@Table
@Data
@NoArgsConstructor
public class AccountDetails {

    @Id
    private String clientId;

    @Column
    private String firstName;

    @Column
    private String lastName;

    @Column(nullable = false)
    @Email
    @NotBlank
    private String email;

    @Column
    private String phone;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name = "client_id")
    private Account account;

    public AccountDetails(String firstName, String lastName, String email, String phone) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
    }
}
