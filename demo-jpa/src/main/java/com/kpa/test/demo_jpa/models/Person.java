package com.kpa.test.demo_jpa.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.kpa.test.demo_jpa.enums.Gender;
import com.kpa.test.demo_jpa.enums.SexualOrientation;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
public class Person {
    private final String id;
    private final String name;
    private int age;
    private final Gender gender;
    private SexualOrientation sexualOrientation;
    private Location location;
}
