package eu.chrost.domain;

import eu.chrost.dto.CompanyNameAndType;
import eu.chrost.validator.MirexProhibited;
import jakarta.persistence.CascadeType;
import jakarta.persistence.ColumnResult;
import jakarta.persistence.ConstructorResult;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.NamedNativeQuery;
import jakarta.persistence.NamedQuery;
import jakarta.persistence.SqlResultSetMapping;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

//@Data
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "companies")
@MirexProhibited
@NamedQuery(name = Company.FIND_BY_TYPE, query = "from Company c where c.companyType = :companyType")
@SqlResultSetMapping(
        name = Company.COMPANY_NAME_AND_TYPE,
        classes = {
                @ConstructorResult(
                        targetClass = CompanyNameAndType.class,
                        columns = {
                                @ColumnResult(name = "name"),
                                @ColumnResult(name = "companyType")
                        }
                )
        }
)
@NamedNativeQuery(name = Company.FIND_BY_NAME, query = "select * from companies where name = :name", resultClass = Company.class)
public class Company {
    public static final String FIND_BY_TYPE = "Company.findByType";
    public static final String COMPANY_NAME_AND_TYPE = "Company.nameAndTypeMapping";
    public static final String FIND_BY_NAME = "Company.findByName";

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return Objects.equals(id, company.id);
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
