package com.abach42.superhero.service;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.abach42.superhero.dto.SkillProfileDto;
import com.abach42.superhero.dto.SkillProfileListDto;
import com.abach42.superhero.entity.Skill;
import com.abach42.superhero.entity.SkillProfile;
import com.abach42.superhero.exception.ApiException;
import com.abach42.superhero.repository.SkillProfileRepository;

@Service
public class SkillProfileService {
    public static final String SKILL_PROFILES_SUPERHERO_NOT_FOUND_MSG = "Skill profiles not found for superhero by id ";
    public static final String SKILL_PROFILE_SUPERHERO_NOT_FOUND_MSG = "Skill profile not found for superhero";
    public static final BiFunction<Long, Long, String> SKILL_PROFILE_SUPERHERO_NOT_FOUND_FN = (superheroId,
            skillId) -> String.format(SKILL_PROFILE_SUPERHERO_NOT_FOUND_MSG + " by superhero id %d and skill id %d",
                    superheroId, skillId);
    public static final String SKILL_PROFILE_SUPERHERO_NOT_CREATED_MSG = "Skill profile for superhero could not be written.";
    public static final String SKILL_PROFILE_SUPERHERO_NOT_UPDATED_MSG = "Skill profile for superhero could not be updated.";

    private final SkillProfileRepository skillProfileRepository;
    private final SuperheroService superheroService;
    private final SkillService skillService;

    public SkillProfileService(SkillProfileRepository skillProfileRepository, @Lazy SuperheroService superheroService,
            @Lazy SkillService skillService) {
        this.skillProfileRepository = skillProfileRepository;
        this.superheroService = superheroService;
        this.skillService = skillService;
    }

    public SkillProfileListDto retrieveSuperheroSkillProfileList(Long superheroId) throws ApiException {
        List<SkillProfileDto> profileList = skillProfileRepository.findBySuperheroIdOrderBySkillId(superheroId).stream()
                .map(SkillProfileDto::fromDomain).toList();
        if (profileList.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, SKILL_PROFILES_SUPERHERO_NOT_FOUND_MSG + superheroId);
        }

        return new SkillProfileListDto(profileList);
    }

    public SkillProfileDto retrieveSuperheroSkillProfile(Long superheroId, Long skillId) {
        SkillProfile skillProfile = getSuperheroSkillProfile(superheroId, skillId);
        return SkillProfileDto.fromDomain(skillProfile);
    }

    private SkillProfile getSuperheroSkillProfile(Long superheroId, Long skillId) {
        return skillProfileRepository.findBySuperheroIdAndSkillId(superheroId, skillId).orElseThrow(
                () -> new ApiException(HttpStatus.NOT_FOUND, SKILL_PROFILE_SUPERHERO_NOT_FOUND_FN.apply(superheroId, skillId)));
    }

    public SkillProfileDto addSuperheroSkillProfile(Long superheroId, SkillProfileDto skillProfileDto) {
        try {
            Objects.requireNonNull(skillProfileDto);

            superheroService.getSuperhero(superheroId);

            Skill skill = skillService.getSkill(skillProfileDto.skill().id());
            SkillProfile newSkillProfile = new SkillProfile(superheroId, skillProfileDto.intensity(), skill);

            SkillProfile createdSkillProfile = skillProfileRepository.save(newSkillProfile);
            return SkillProfileDto.fromDomain(createdSkillProfile);
        } catch (ApiException e) {
            throw new ApiException(e.getStatusCode(), e.getReason());
        } catch (DataIntegrityViolationException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, SKILL_PROFILE_SUPERHERO_NOT_CREATED_MSG);
        }
    }

    /*
    * TODO: use `JPA automatic dirty checking` @see SuperheroService
    */
    public SkillProfileDto changeSuperheroSkillProfile(Long superheroId, Long skillId, SkillProfileDto update) throws ApiException {
        try {
            superheroService.getSuperhero(superheroId);

            SkillProfile origin = getSuperheroSkillProfile(superheroId, skillId);

            // manual dirty checking, todo see above
            if (update.intensity() != null) {
                origin.setIntensity(update.intensity());
            }

            SkillProfile savedSkillProfile = skillProfileRepository.save(origin);
            return SkillProfileDto.fromDomain(savedSkillProfile);
        } catch (ApiException e) {
            throw new ApiException(e.getStatusCode(), e.getReason());
        } catch (DataIntegrityViolationException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, SKILL_PROFILE_SUPERHERO_NOT_UPDATED_MSG);
        }
    }

    public SkillProfileDto deleteSuperheroSkillProfile(Long superheroId, Long skillId) throws ApiException {
        SkillProfile skillProfile = getSuperheroSkillProfile(superheroId, skillId);
        skillProfileRepository.delete(skillProfile);

        return SkillProfileDto.fromDomain(skillProfile);
    }
}
