package com.chrosciu.domain;

import com.chrosciu.dto.CompanyNameAndType;
import com.chrosciu.listener.CompanyListener;
import java.util.ArrayList;
import javax.persistence.CascadeType;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQuery;
import javax.persistence.PostLoad;
import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreRemove;
import javax.persistence.PreUpdate;
import javax.persistence.SqlResultSetMapping;
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
@EntityListeners(CompanyListener.class)
@NamedQuery(name = Company.FIND_BY_TYPE, query = "from Company c where c.companyType = :companyType")
@SqlResultSetMapping(
    name = Company.COMPANY_NAME_AND_TYPE,
    classes = {
        @ConstructorResult(
            targetClass = CompanyNameAndType.class,
            columns = {
                @ColumnResult(name = "name"),
                @ColumnResult(name = "companyType")
            }
        )
    }
)
@NamedNativeQuery(name = Company.FIND_BY_NAME, query = "select * from company where name = :name", resultClass = Company.class)
public class Company {
    public static final String FIND_BY_TYPE = "Company.findByType";
    public static final String COMPANY_NAME_AND_TYPE = "Company.nameAndTypeMapping";
    public static final String FIND_BY_NAME = "Company.findByName";

    @GeneratedValue
    @Id
    private Long id;

    @NotBlank
    //@JanuszProhibited
    private String name;

    @ManyToOne(cascade = {CascadeType.PERSIST}, fetch = FetchType.LAZY)
    private Area area;

    private int size;

    @Enumerated(EnumType.STRING)
    private CompanyType companyType;

    public void assignArea(Area area) {
        this.area = area;
        if (area != null) {
            if (null == area.getCompanies()) {
                area.setCompanies(new ArrayList<>());
            }
            area.getCompanies().add(this);
        }
    }

    @PrePersist
    public void prePersist() {
        log.info("### prePersist");
//        this.name = Optional.ofNullable(this.name).map(String::toUpperCase).orElse(null);
    }

    @PostPersist
    public void postPersist() {
        log.info("### postPersist");
//        if ("Januszex".equals(this.name)) {
//            throw new IllegalStateException("Januszex not allowed");
//        }
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
