package com.chrosciu.dto;

import com.chrosciu.domain.CompanyType;
import lombok.Value;

@Value
public class CompanyNameAndType {
    String name;
    CompanyType type;
}
