package com.kpa.test.demo_jpa.enums;

import java.util.EnumSet;
import java.util.Optional;

public enum SexualOrientation {
    STRAIGHT,
    LESBIAN,
    GAY,
    BISEXUAL,
    QUEER,
    OTHER;

    public SexualOrientation getSexualOrientation(String orientation){
        Optional<SexualOrientation> val = EnumSet.allOf(SexualOrientation.class)
            .stream().filter(e -> e.name().equalsIgnoreCase(orientation))
            .findFirst();
        return val.orElse(OTHER);
    }
}
