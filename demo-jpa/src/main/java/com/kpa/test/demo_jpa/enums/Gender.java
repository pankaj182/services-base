package com.kpa.test.demo_jpa.enums;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

public enum Gender {
    MALE( "m", "male", "he"),
    FEMALE( "f", "female", "she"),
    TRANSGENDER("t", "transgender", "both"),
    OTHER( "other");

    private final List<String> aliases;
    Gender(String... aliases){
        this.aliases = List.of(aliases);
    }

    public Gender getGender(String gender) {
        Optional<Gender> val = EnumSet.allOf(Gender.class).stream().filter(e -> e.aliases.contains(gender.toLowerCase())).findFirst();
        return val.orElse(OTHER);
    }
}
