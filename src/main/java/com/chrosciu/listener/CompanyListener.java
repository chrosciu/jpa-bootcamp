package com.chrosciu.listener;

import com.chrosciu.domain.Company;
import java.util.Optional;
import javax.persistence.PrePersist;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CompanyListener {
    @PrePersist
    public void prePersist(Company company) {
        log.info("### prePersist");
        company.setName(Optional.ofNullable(company.getName()).map(String::toUpperCase).orElse(null));
    }
}
