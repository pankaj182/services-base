package com.kpa.test.demo_jpa.enums;

import java.util.EnumSet;
import java.util.Optional;

public enum Race {
    WHITE(0),
    AFRICAN_AMERICAN(1),
    ASIAN(3),
    AMERICAN_INDIAN(2),
    PACIFIC_ISLANDER(4),
    OTHER(5);

    private final int ordinal;
    Race(int ordinal) {
        this.ordinal = ordinal;
    }

    public int getOrdinal(){
        return this.ordinal;
    }

    public static Race of(int ordinal) {
        Optional<Race> val = EnumSet.allOf(Race.class)
            .stream().filter(e -> e.ordinal() == ordinal)
            .findFirst();
        return val.orElse(OTHER);
    }
}
