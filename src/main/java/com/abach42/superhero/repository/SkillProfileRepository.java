package com.abach42.superhero.repository;

import com.abach42.superhero.entity.SkillProfile;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SkillProfileRepository extends JpaRepository<SkillProfile, Long> {

    List<SkillProfile> findBySuperheroIdOrderBySkillId(Long superheroId);

    Optional<SkillProfile> findBySuperheroIdAndSkillId(Long superheroId, Long skillId);
}