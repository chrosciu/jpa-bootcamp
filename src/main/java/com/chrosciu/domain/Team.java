package com.chrosciu.domain;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
@ToString(exclude = "employees")

public class Team {
    @GeneratedValue
    @Id
    private Long id;

    private String name;

    @OneToMany(mappedBy = "team")
    private List<Employee> employees;
}
