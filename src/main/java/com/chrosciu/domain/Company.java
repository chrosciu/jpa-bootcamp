package com.chrosciu.domain;

import com.chrosciu.listener.CompanyListener;
import com.chrosciu.validator.JanuszProhibited;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Slf4j
@EntityListeners(CompanyListener.class)
public class Company {
    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    @JanuszProhibited
    private String name;

    @ManyToOne(optional = false, cascade = {CascadeType.PERSIST})
    private Area area;

    @PrePersist
    private void prePersist() {
        this.name = (this.name != null) ? this.name.toUpperCase() : null;
    }
}
