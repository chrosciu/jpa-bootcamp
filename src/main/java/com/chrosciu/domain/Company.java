package com.chrosciu.domain;

import com.chrosciu.validator.JanuszProhibited;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
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
public class Company {
    @GeneratedValue
    @Id
    private Long id;

    @NotBlank
    @JanuszProhibited
    private String name;
}
