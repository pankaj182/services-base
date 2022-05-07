package com.kpa.test.demo_jpa.models;

import lombok.*;

import javax.persistence.*;

@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private long pin;
    private String state;
    private String area;

    @OneToOne
    private Person person;
}
