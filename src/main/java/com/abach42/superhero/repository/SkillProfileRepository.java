package com.abach42.superhero.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.abach42.superhero.entity.SkillProfile;

@Repository
public interface SkillProfileRepository extends JpaRepository<SkillProfile, Long> {
    public List<SkillProfile> findBySuperheroIdOrderBySkillId(Long superheroId);
    public Optional<SkillProfile> findBySuperheroIdAndSkillId(Long superheroId, Long skillId);
}