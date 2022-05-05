package com.kpa.test.demo_jpa.controllers;

import com.kpa.test.demo_jpa.enums.Gender;
import com.kpa.test.demo_jpa.enums.SexualOrientation;
import com.kpa.test.demo_jpa.models.Country;
import com.kpa.test.demo_jpa.models.Location;
import com.kpa.test.demo_jpa.models.Person;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PersonController {

    @GetMapping("/person/names/{name}")
    public Person getPersonByName(@PathVariable String name) {
        Location location = Location.builder()
            .country(Country.INDIA)
            .state("KARNATAKA")
            .pin(560103L)
            .area("Bellandur")
            .build();
        Person person = Person.builder()
            .name("Pankaj")
            .age(27)
            .id("1")
            .gender(Gender.MALE)
            .sexualOrientation(SexualOrientation.STRAIGHT)
            .location(location)
            .build();
        return person;
    }

    @GetMapping("/person/names?limit={limit}")
    public Person getAllPersons(@PathVariable String name,
                                @RequestParam(defaultValue = "100") int limit) {
        Location location = Location.builder()
            .country(Country.INDIA)
            .state("KARNATAKA")
            .pin(560103L)
            .area("Bellandur")
            .build();
        Person person = Person.builder()
            .name("Pankaj")
            .age(27)
            .id("1")
            .gender(Gender.MALE)
            .sexualOrientation(SexualOrientation.STRAIGHT)
            .location(location)
            .build();
        return person;
    }

    @PutMapping("/person/names")
    public boolean savePerson(@RequestBody Person person) {
        return true;
    }
}
