package com.abach42.superhero.entity;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

/**
 * Brings together aggregate root {@link Superhero} and skill {@link Skill} identified by
 * `Superhero.id` and `Skill.id`, unique for every {@link Superhero} and filled by `intensity` of
 * skill. Works as single or list filtered by `Superhero.id`.
 */
@Entity
@Access(AccessType.FIELD)
public class SkillProfile {

    /* Normally, this record is fully identified by superheroId and skill,
     * but FE developers like ids
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "skill_profile_id")
    private Long id;

    private Long superheroId;

    @Column(nullable = false)
    private Integer intensity;

    @ManyToOne(targetEntity = Skill.class)
    @JoinColumn(name = "skill_id")
    private Skill skill;

    protected SkillProfile() {
    }

    public SkillProfile(Long superheroId, Integer intensity, Skill skill) {
        this.superheroId = superheroId;
        this.intensity = intensity;
        this.skill = skill;
    }

    public Long getId() {
        return id;
    }

    public Long getSuperheroId() {
        return superheroId;
    }

    public void setSuperheroId(Long superheroId) {
        this.superheroId = superheroId;
    }

    public Integer getIntensity() {
        return intensity;
    }

    public void setIntensity(Integer intensity) {
        this.intensity = intensity;
    }

    public Skill getSkill() {
        return skill;
    }

    public void setSkill(Skill skill) {
        this.skill = skill;
    }
}
