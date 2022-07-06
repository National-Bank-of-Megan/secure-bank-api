package pl.edu.pw.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table
@Data
public class AccountDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column private Date birthday;
    @Column private String firstname;
    @Column private String lastname;
    @Column private String email;

    @Column
    @OneToOne
    private Account account;






}
