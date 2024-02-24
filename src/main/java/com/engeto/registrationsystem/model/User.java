package com.engeto.registrationsystem.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    public Long id;

    public String name;

    public String surname;

    public String personId;

    public String uuid;

    public User( String name, String surname, String personId, String uuid) {
        this.name = name;
        this.surname = surname;
        this.personId = personId;
        this.uuid = uuid;
    }

    public User( String name, String personId, String uuid) {
        this.name = name;
        this.personId = personId;
        this.uuid = uuid;
    }

    public User(Long id, String name, String surname) {
        this.id = id;
        this.name = name;
        this.surname = surname;
    }
}
