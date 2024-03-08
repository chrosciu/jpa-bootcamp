package eu.chrost.domain;

import eu.chrost.validator.MirexProhibited;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "companies")
@MirexProhibited
public class Company {
    @GeneratedValue
    @Id
    private Long id;

    @Size(max = 20)
    private String name;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    private Area area;
}
