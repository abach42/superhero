package com.abach42.superhero.skill;

import com.abach42.superhero.config.api.ApiException;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class SkillService {

    public static final String SKILLS_NOT_FOUND_MSG = "Skills not found.";
    public static final String SKILL_NOT_FOUND_MSG = "Skill not found on id ";

    private final SkillRepository skillRepository;

    public SkillService(SkillRepository skillRepository) {
        this.skillRepository = skillRepository;
    }

    public SkillListDto getSkillList() throws ApiException {
        List<SkillDto> allSkills = skillRepository.findAll().stream().map(SkillDto::fromDomain)
                .toList();

        if (allSkills.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, SKILLS_NOT_FOUND_MSG);
        }

        return new SkillListDto(allSkills);
    }

    public SkillDto getSkillConverted(Long id) {
        Skill skill = getSkill(id);
        return SkillDto.fromDomain(skill);
    }

    public Skill getSkill(Long id) {
        return skillRepository.findById(id).orElseThrow(
                () -> new ApiException(HttpStatus.NOT_FOUND, SKILL_NOT_FOUND_MSG + id)
        );
    }
}
