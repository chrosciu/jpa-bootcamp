package com.chrosciu.domain;

import com.chrosciu.listener.TeamListener;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedAttributeNode;
import javax.persistence.NamedEntityGraph;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@ToString(exclude = {"employees"})
@EntityListeners({TeamListener.class})
@NamedEntityGraph(name = Team.WITH_EMPLOYEES, attributeNodes = @NamedAttributeNode("employees"))
public class Team {

    public static final String WITH_EMPLOYEES = "teamWithEmployees";

    @GeneratedValue
    @Id
    private Long id;

    @Version
    private long version;

    private String name;

    @Enumerated(EnumType.STRING)
    private TeamType teamType;

    @OneToMany(mappedBy = "team", cascade = {CascadeType.REMOVE, CascadeType.PERSIST}, orphanRemoval = true)
    private List<Employee> employees;
}
