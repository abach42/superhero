package com.abach42.superhero.skillprofile;

import com.abach42.superhero.ai.RemoveSuperheroVectorEvent;
import com.abach42.superhero.ai.UpdateSuperheroVectorEvent;
import com.abach42.superhero.config.api.ApiException;
import com.abach42.superhero.skill.Skill;
import com.abach42.superhero.skill.SkillService;
import com.abach42.superhero.superhero.Superhero;
import com.abach42.superhero.superhero.SuperheroService;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
public class SkillProfileService {

    public static final String SKILL_PROFILES_SUPERHERO_NOT_FOUND_MSG =
            "Skill profiles not found for superhero by id ";
    public static final String SKILL_PROFILE_SUPERHERO_NOT_FOUND_MSG =
            "Skill profile not found for superhero";
    public static final BiFunction<Long, Long, String> SKILL_PROFILE_SUPERHERO_NOT_FOUND_FN =
            (superheroId, skillId) -> String.format(
            SKILL_PROFILE_SUPERHERO_NOT_FOUND_MSG + " by superhero id %d and skill id %d",
            superheroId, skillId);
    public static final String SKILL_PROFILE_SUPERHERO_NOT_CREATED_MSG =
            "Skill profile for superhero could not be written.";
    public static final String SKILL_PROFILE_SUPERHERO_NOT_UPDATED_MSG =
            "Skill profile for superhero could not be updated.";

    private final SkillProfileRepository skillProfileRepository;
    private final SuperheroService superheroService;
    private final SkillService skillService;
    private final ApplicationEventPublisher eventPublisher;

    public SkillProfileService(SkillProfileRepository skillProfileRepository,
            @Lazy SuperheroService superheroService, @Lazy SkillService skillService,
            ApplicationEventPublisher eventPublisher) {
        this.skillProfileRepository = skillProfileRepository;
        this.superheroService = superheroService;
        this.skillService = skillService;
        this.eventPublisher = eventPublisher;
    }

    public SkillProfileListDto retrieveSuperheroSkillProfileList(Long superheroId)
            throws ApiException {
        List<SkillProfileDto> profileList = skillProfileRepository.findBySuperheroIdOrderBySkillId(
                superheroId).stream().map(SkillProfileDto::fromDomain).toList();
        if (profileList.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND,
                    SKILL_PROFILES_SUPERHERO_NOT_FOUND_MSG + superheroId);
        }

        triggerVectorUpdateEvent(superheroId);

        return new SkillProfileListDto(profileList);
    }

    private void triggerVectorUpdateEvent(Long superheroId) {
        Superhero superhero = superheroService.getSuperhero(superheroId);
        eventPublisher.publishEvent(new UpdateSuperheroVectorEvent(superhero));
    }

    public SkillProfileDto retrieveSuperheroSkillProfile(Long superheroId, Long skillId) {
        SkillProfile skillProfile = getSuperheroSkillProfile(superheroId, skillId);
        return SkillProfileDto.fromDomain(skillProfile);
    }

    private SkillProfile getSuperheroSkillProfile(Long superheroId, Long skillId) {
        return skillProfileRepository.findBySuperheroIdAndSkillId(superheroId, skillId).orElseThrow(
                () -> new ApiException(HttpStatus.NOT_FOUND,
                        SKILL_PROFILE_SUPERHERO_NOT_FOUND_FN.apply(superheroId, skillId)));
    }

    public SkillProfileDto addSuperheroSkillProfile(Long superheroId,
            SkillProfileDto skillProfileDto) {
        try {
            Objects.requireNonNull(skillProfileDto);

            superheroService.getSuperhero(superheroId);

            Skill skill = skillService.getSkill(skillProfileDto.skill().id());
            SkillProfile newSkillProfile = new SkillProfile(superheroId,
                    skillProfileDto.intensity(), skill);

            SkillProfile createdSkillProfile = skillProfileRepository.save(newSkillProfile);

            triggerVectorUpdateEvent(superheroId);

            return SkillProfileDto.fromDomain(createdSkillProfile);
        } catch (ApiException e) {
            throw new ApiException(e.getStatusCode(), e.getReason());
        } catch (DataIntegrityViolationException | NullPointerException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, SKILL_PROFILE_SUPERHERO_NOT_CREATED_MSG);
        }
    }

    public SkillProfileDto changeSuperheroSkillProfile(Long superheroId, Long skillId,
            SkillProfileDto update) throws ApiException {
        try {
            superheroService.getSuperhero(superheroId);

            SkillProfile origin = getSuperheroSkillProfile(superheroId, skillId);

            // manual dirty checking, todo see above
            if (update.intensity() != null) {
                origin.setIntensity(update.intensity());
            }

            SkillProfile savedSkillProfile = skillProfileRepository.save(origin);

            triggerVectorUpdateEvent(superheroId);

            return SkillProfileDto.fromDomain(savedSkillProfile);
        } catch (ApiException e) {
            throw new ApiException(e.getStatusCode(), e.getReason());
        } catch (DataIntegrityViolationException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, SKILL_PROFILE_SUPERHERO_NOT_UPDATED_MSG);
        }
    }

    public SkillProfileDto deleteSuperheroSkillProfile(Long superheroId, Long skillId)
            throws ApiException {
        SkillProfile skillProfile = getSuperheroSkillProfile(superheroId, skillId);
        skillProfileRepository.delete(skillProfile);

        triggerVectorRemoveEvent(superheroId);

        return SkillProfileDto.fromDomain(skillProfile);
    }

    private void triggerVectorRemoveEvent(Long superheroId) {
        Superhero superhero = superheroService.getSuperhero(superheroId);
        eventPublisher.publishEvent(new RemoveSuperheroVectorEvent(superhero));
    }
}
