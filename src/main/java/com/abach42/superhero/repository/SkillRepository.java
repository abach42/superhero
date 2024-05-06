package com.abach42.superhero.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.abach42.superhero.entity.Skill;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long>{
}