package pl.edu.pw.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Data
@EqualsAndHashCode
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String name;

    @Column
    private String ip;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_number")
    private Account account;

    public Device(String name, String ip) {
        this.name = name;
        this.ip = ip;
    }
}
