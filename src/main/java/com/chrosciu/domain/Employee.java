package com.chrosciu.domain;

import com.chrosciu.validator.NoCommonNames;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.validation.constraints.Size;
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
@NoCommonNames
@Slf4j
public class Employee {

    @GeneratedValue
    @Id
    private Long id;

    private String firstName;

    @Size(max = 20)
    private String lastName;

    @ManyToOne(cascade = {CascadeType.PERSIST})
    private Team team;

    @PrePersist
    public void prePersist() {
        log.info("### prePersist");
    }

    @PostPersist
    public void postPersist() {
        log.info("### postPersist");
    }

    @PreUpdate
    public void preUpdate() {
        log.info("### preUpdate");
    }

    @PostUpdate
    public void postUpdate() {
        log.info("### postUpdate");
    }

    @PreRemove
    public void preRemove() {
        log.info("### preRemove");
    }

    @PostRemove
    public void postRemove() {
        log.info("### postRemove");
    }

    @PostLoad
    public void postLoad() {
        log.info("### postLoad");
    }
}
