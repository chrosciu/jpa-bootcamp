package com.chrosciu.dto;

import com.chrosciu.domain.Area;
import lombok.Value;

@Value
public class AreaAndCount {
    Area area;
    Long count;
}
