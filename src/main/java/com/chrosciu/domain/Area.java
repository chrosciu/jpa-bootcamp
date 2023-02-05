package com.chrosciu.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;
import java.util.Set;

@Getter
@Setter
@ToString(exclude = "companies")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@NamedEntityGraph(name = Area.WITH_COMPANIES, attributeNodes = @NamedAttributeNode(Area_.COMPANIES))
public class Area {
    public static final String WITH_COMPANIES = "areaWithCompanies";

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @OneToMany(mappedBy = "area", cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, orphanRemoval = true)
    Set<Company> companies;
}
