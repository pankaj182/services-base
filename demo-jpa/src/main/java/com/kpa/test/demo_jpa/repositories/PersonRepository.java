package com.kpa.test.demo_jpa.repositories;

import com.kpa.test.demo_jpa.models.Person;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

/**
 * see <a href="https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repository-query-keywords">spring jpa query</a>
 */
public interface PersonRepository extends JpaRepository<Person, String> {
    // equality Condition
    List<Person> findByName(String name);
    List<Person> findByNameIs(String name);
    List<Person> findByNameIsNot(String name);
    List<Person> findByNameIsNull();
    List<Person> findByNameIsNotNull();

    // for boolean types
    List<Person> findByActiveTrue();
    List<Person> findByActiveFalse();

    // Similarity Condition
    List<Person> findByNameStartingWith(String prefix);
    List<Person> findByNameEndingWith(String suffix);
    List<Person> findByNameContaining(String infix);
    List<Person> findByNameLike(String likePattern);

    // comparison
    List<Person> findByAgeLessThan(Integer age);
    List<Person> findByAgeLessThanEqual(Integer age);
    List<Person> findByAgeGreaterThan(Integer age);
    List<Person> findByAgeGreaterThanEqual(Integer age);
    List<Person> findByAgeBetween(Integer startAge, Integer endAge);
    List<Person> findByAgeIn(Collection<Integer> ages);
//    List<Person> findByBirthDateAfter(ZonedDateTime birthDate);
//    List<Person> findByBirthDateBefore(ZonedDateTime birthDate);

    // logical Operator
//    List<Person> findByNameOrBirthDate(String name, ZonedDateTime birthDate);
//    List<Person> findByNameOrBirthDateAndActive(String name, ZonedDateTime birthDate, Boolean active);

    // sorting
    List<Person> findByNameOrderByName(String name);
    List<Person> findByNameOrderByNameAsc(String name);
    List<Person> findByNameOrderByNameDesc(String name);

    // limit
    List<Person> findTop3ByName(String name);

    // distinct
    List<Person> findDistinctByName(String name);

    // ignore case
    List<Person> findByNameIgnoreCase(String name);

    // instead of long names, look at @Query annotation
    List<Person> findDistinctByNameIgnoreCaseOrderByNameAsc(String name);
}
