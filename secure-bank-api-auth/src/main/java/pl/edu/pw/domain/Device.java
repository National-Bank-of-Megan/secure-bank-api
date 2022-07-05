package pl.edu.pw.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="ip")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Device {

    @Id private Long clientId;
    @Column private String ip;
    @Column private String name;

}
