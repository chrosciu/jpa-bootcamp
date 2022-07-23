package com.chrosciu.dto;

import com.chrosciu.domain.CompanyType;
import lombok.Value;

@Value
public class CompanyTypeAndCount {
    CompanyType type;
    Long count;
}
