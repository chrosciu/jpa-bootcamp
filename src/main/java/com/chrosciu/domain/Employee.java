package com.chrosciu.domain;

import com.chrosciu.validator.NoCommonNames;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@NoCommonNames
public class Employee {

    @GeneratedValue
    @Id
    private Long id;

    private String firstName;

    @Size(max = 20)
    private String lastName;

}
