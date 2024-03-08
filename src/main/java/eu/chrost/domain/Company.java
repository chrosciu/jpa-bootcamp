package eu.chrost.domain;

import eu.chrost.validator.MirexProhibited;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedQuery;
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
@NamedQuery(name = Company.FIND_BY_TYPE, query = "from Company c where c.companyType = :companyType")
public class Company {
    public static final String FIND_BY_TYPE = "Company.findByType";

    @GeneratedValue
    @Id
    private Long id;

    @Size(max = 20)
    private String name;

    private int size;

    @Enumerated(EnumType.STRING)
    private CompanyType companyType;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    private Area area;
}
