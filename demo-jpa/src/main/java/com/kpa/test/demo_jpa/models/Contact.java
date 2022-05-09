package com.kpa.test.demo_jpa.models;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class Contact {
    private final String countryCode;
    private final String phone;
    private final String primaryEmailId;
    private String alternatePhone;
    private String secondaryEmailId;
    private String fax;
}
