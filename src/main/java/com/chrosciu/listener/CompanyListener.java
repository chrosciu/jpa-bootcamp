package com.chrosciu.listener;

import com.chrosciu.domain.Company;

import javax.persistence.PostPersist;

public class CompanyListener {
    @PostPersist
    private void postPersist(Company company) {
        if (company.getName() != null && company.getName().contains("MIREX")) {
            throw new RuntimeException("Mirex not allowed");
        }
    }
}
