package eu.chrost.dto;

import eu.chrost.domain.CompanyType;

public record CompanyNameAndType(String name, CompanyType type) {
    public CompanyNameAndType(String name, String type) {
        this(name, CompanyType.valueOf(type));
    }
}
