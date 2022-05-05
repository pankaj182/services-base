package com.kpa.test.demo_jpa.models;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class Location {
    private long pin;
    private Country country;
    private String state;
    private String area;
}
