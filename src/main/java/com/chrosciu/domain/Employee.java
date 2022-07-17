package com.chrosciu.domain;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Slf4j
public class Employee {
    @GeneratedValue
    @Id
    private Long id;

    private String firstName;

    private String lastName;

    @ManyToOne(cascade = CascadeType.PERSIST)
    private Team team;
}
