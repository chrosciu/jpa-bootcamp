package com.chrosciu.domain;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Slf4j
@ToString(exclude = "companies")
@NamedEntityGraph(name = Area.WITH_COMPANIES, attributeNodes = @NamedAttributeNode("companies"))
public class Area {
    public static final String WITH_COMPANIES = "areaWithCompanies";

    @GeneratedValue
    @Id
    private Long id;

    private String name;

    @OneToMany(mappedBy = "area", cascade = {CascadeType.REMOVE})
    private List<Company> companies;
}
