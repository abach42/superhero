package com.abach42.superhero.entity;

import java.time.LocalDate;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
@Access(AccessType.FIELD)
public class Superhero {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String alias;
    private String realName;
    private LocalDate dateOfBirth;
    private String gender;
    private String occupation;
    
    @Column(length = 2048)
    private String originStory;

    protected Superhero() {}

    public Superhero(
        String alias, 
        String realName, 
        LocalDate dateOfBirth, 
        String gender, 
        String occupation,
        String originStory
    ) {
        //todo use setters
        this.alias = alias;
        this.realName = realName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.occupation = occupation;
        this.originStory = originStory;
    }


    public long getId() {
        return id;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getOccupation() {
        return occupation;
    }

    public void setOccupation(String occupateion) {
        this.occupation = occupateion;
    }

    public String getOriginStory() {
        return originStory;
    }
    public void setOriginStory(String originStory) {
        this.originStory = originStory;
    }
}
