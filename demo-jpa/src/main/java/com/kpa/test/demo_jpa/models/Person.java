package com.kpa.test.demo_jpa.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.kpa.test.demo_jpa.enums.Gender;
import com.kpa.test.demo_jpa.enums.MaritalStatus;
import com.kpa.test.demo_jpa.enums.Race;
import com.kpa.test.demo_jpa.enums.SexualOrientation;
import lombok.*;

import javax.persistence.*;

/**
 * Demonstrates:
 * <ul>
 * <li> enum persistence </li>
 * <li> single independent entity(No mapping) </li>
 * <li> @Column, @Table, @Transient, @Id, @GeneratedValue annotations </li>
 * <li> @PostLoad & @PrePersist</li>
 * </ul>
 * @see <a href="https://www.baeldung.com/jpa-persisting-enums-in-jpa">Persisting enums in jpa</a>
 */
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
// we can choose to create table with different name using @Table
//@Table(name="user"")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private String name;
    private int age;
    private boolean active;

    /*
    * Persisting enum: Method 1
    * default: @Enumerated(EnumType.STRING)
    * uses enum.name() method
    * */
    @Column(name="sex")
    @Enumerated(EnumType.STRING)
    private Gender gender;

    /*
     * Persisting enum: Method 2
     * @Enumerated(EnumType.ORDINAL)
     * uses enum.ordinal() method
     * can't change ordering of enum
     * */
    @Column(name="sexual_orientation")
    @Enumerated(EnumType.ORDINAL)
    private SexualOrientation sexualOrientation;

    /*
     * Persisting enum: Method 3
     * We can map our enums back and forth in the @PostLoad and @PrePersist events.
     * The idea is to have two attributes in an entity.
     * The first one is mapped to a database value, and the second one is a @Transient field that holds a real enum value.
     * The transient attribute is then used by the business logic code.
     */
    @Transient
    private Race race;

    @Basic
    private int raceValue;

    @PostLoad
    void fillTransient() {
        this.race = Race.of(raceValue);
    }

    @PrePersist
    void fillPersistent() {
        this.raceValue = race.getOrdinal();
    }

    /*
     * Persisting enum: Method 4 (Preferred)
     * create a new class that implements javax.persistence.AttributeConverter and
     * annotate it with @Converter.
     */
    @Column(name="marital_status")
    private MaritalStatus maritalStatus;
}
