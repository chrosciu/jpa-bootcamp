package com.chrosciu.domain;

import lombok.Builder;

import javax.validation.constraints.NotBlank;

@Builder
public class Person {
    @NotBlank
    private String name;
}
