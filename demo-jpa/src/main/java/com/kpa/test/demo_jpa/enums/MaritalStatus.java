package com.kpa.test.demo_jpa.enums;

import java.util.EnumSet;

public enum MaritalStatus {
    MARRIED('m'),
    UNMARRIED('u'),
    DIVORCED('d'),
    SEPARATED('s'),
    WIDOWED('w'),
    OTHER('o');

    private final char shortName;
    MaritalStatus(char shortName){
        this.shortName = shortName;
    }

    public static MaritalStatus of(char shortName) {
        return EnumSet.allOf(MaritalStatus.class)
            .stream().filter(e -> e.shortName == shortName)
            .findFirst().orElse(OTHER);
    }

    public char shortName() {
        return this.shortName;
    }
}
