package eu.chrost.domain;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.NamedAttributeNode;
import jakarta.persistence.NamedEntityGraph;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "companies")
@Entity
@Slf4j
@NamedEntityGraph(name = Area.WITH_COMPANIES, attributeNodes = @NamedAttributeNode(Area_.COMPANIES))
class Area {
    static final String WITH_COMPANIES = "areaWithCompanies";

    @GeneratedValue
    @Id
    private Long id;

    private String name;

    @OneToMany(mappedBy = "area", cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, orphanRemoval = true)
    private List<Company> companies;
}
