package com.kpa.test.demo_jpa.controllers;

import com.kpa.test.demo_jpa.models.Person;
import com.kpa.test.demo_jpa.repositories.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class PersonController {

    private final PersonRepository personRepository;

    @Autowired
    PersonController(final PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @GetMapping("/person/names/{name}")
    public List<Person> getPersonByName(@PathVariable String name,
                                        @RequestParam(required = false) boolean distinct,
                                        @RequestParam(required = false) boolean asc,
                                        @RequestParam(required = false) boolean ignoreCase) {
        return personRepository.findByNameIgnoreCase(name);
    }

    @GetMapping("/person/names")
    public List<Person> getAllPersons(@RequestParam(defaultValue = "100") int limit) {
        return personRepository.findAll();
    }

    @PutMapping("/person/names")
    public Person savePerson(@RequestBody Person person) {
        return personRepository.save(person);
    }
}
