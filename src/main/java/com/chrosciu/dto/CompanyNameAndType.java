package com.chrosciu.dto;

import com.chrosciu.domain.CompanyType;
import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class CompanyNameAndType {
    String name;
    CompanyType type;

    public CompanyNameAndType(String name, String type) {
        this.name = name;
        this.type = CompanyType.valueOf(type);
    }
}
