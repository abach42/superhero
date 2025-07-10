package com.abach42.superhero.skillprofile;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.abach42.superhero.config.api.ApiException;
import com.abach42.superhero.skill.Skill;
import com.abach42.superhero.skill.SkillService;
import com.abach42.superhero.superhero.SuperheroService;
import com.abach42.superhero.testconfiguration.TestStubs;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;

@Tags(value = {@Tag("unit"), @Tag("skillprofile")})
@ExtendWith(MockitoExtension.class)
public class SkillProfileServiceTest {

    @Mock
    private SkillProfileRepository skillProfileRepository;

    @Mock
    private SuperheroService superheroService;

    @Mock
    private SkillService skillService;

    @InjectMocks
    private SkillProfileService subject;

    @Test
    @DisplayName("Should throw ApiException when skill profile list is empty")
    public void testRetrieveSuperheroSkillProfileListWillThrowWhenEmpty() {
        Long superheroId = 1L;
        List<SkillProfile> emptyProfiles = Collections.emptyList();
        when(skillProfileRepository.findBySuperheroIdOrderBySkillId(superheroId))
                .thenReturn(emptyProfiles);

        ApiException exception = assertThrows(ApiException.class,
                () -> subject.retrieveSuperheroSkillProfileList(superheroId));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getReason()).contains(
                "Skill profiles not found for superhero by id " + superheroId);
        verify(skillProfileRepository).findBySuperheroIdOrderBySkillId(superheroId);
    }

    @Test
    @DisplayName("Should return skill profile list when profiles exist")
    public void testRetrieveSuperheroSkillProfileListWillReturnProfileList() {
        Long superheroId = 1L;
        List<SkillProfile> profiles = List.of(TestStubs.getSkillProfileStub());
        when(skillProfileRepository.findBySuperheroIdOrderBySkillId(superheroId))
                .thenReturn(profiles);

        SkillProfileListDto actual = subject.retrieveSuperheroSkillProfileList(superheroId);

        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(TestStubs.getSkillProfileListDtoStub());
        verify(skillProfileRepository).findBySuperheroIdOrderBySkillId(superheroId);
    }

    @Test
    @DisplayName("Should convert skill profile to DTO correctly")
    public void testRetrieveSuperheroSkillProfileConverted() {
        Long superheroId = 1L;
        Long skillId = 1L;
        SkillProfile skillProfile = new SkillProfile(superheroId, 50, new Skill(skillId, "foo"));
        when(skillProfileRepository.findBySuperheroIdAndSkillId(superheroId, skillId))
                .thenReturn(Optional.of(skillProfile));

        SkillProfileDto dto = subject.retrieveSuperheroSkillProfile(superheroId, skillId);

        assertThat(dto).isNotNull();
        assertThat(dto.intensity()).isEqualTo(skillProfile.getIntensity());
        assertThat(dto.skill().id()).isEqualTo(skillProfile.getSkill().getId());
        verify(skillProfileRepository).findBySuperheroIdAndSkillId(superheroId, skillId);
    }

    @Test
    @DisplayName("Should throw ApiException when skill profile not found")
    public void testRetrieveSuperheroSkillProfileThrowsWhenNotFound() {
        Long superheroId = 1L;
        Long skillId = 1L;
        when(skillProfileRepository.findBySuperheroIdAndSkillId(superheroId, skillId))
                .thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class,
                () -> subject.retrieveSuperheroSkillProfile(superheroId, skillId));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getReason()).contains(
                "Skill profile not found for superhero by superhero id 1 and skill id 1");
    }

    @Test
    @DisplayName("Should create skill profile successfully")
    public void testAddSuperheroSkillProfileSuccess() {
        Long superheroId = 1L;
        SkillProfileDto skillProfileDto = TestStubs.getSkillProfileDtoStub();
        Skill skill = new Skill(1L, "TestSkill");
        SkillProfile savedSkillProfile = new SkillProfile(superheroId, skillProfileDto.intensity(),
                skill);

        when(superheroService.getSuperhero(superheroId))
                .thenReturn(TestStubs.getSuperheroStub());
        when(skillService.getSkill(skillProfileDto.skill().id()))
                .thenReturn(skill);
        when(skillProfileRepository.save(any(SkillProfile.class)))
                .thenReturn(savedSkillProfile);

        SkillProfileDto result = subject.addSuperheroSkillProfile(superheroId, skillProfileDto);

        assertThat(result).isNotNull();
        assertThat(result.intensity()).isEqualTo(skillProfileDto.intensity());
        verify(superheroService).getSuperhero(superheroId);
        verify(skillService).getSkill(skillProfileDto.skill().id());
        verify(skillProfileRepository).save(any(SkillProfile.class));
    }

    @Test
    @DisplayName("Should throw ApiException when null DTO provided")
    public void testAddSuperheroSkillProfileThrowsWhenNullDto() {
        Long superheroId = 1L;

        ApiException exception = assertThrows(ApiException.class,
                () -> subject.addSuperheroSkillProfile(superheroId, null));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getReason()).isEqualTo(
                SkillProfileService.SKILL_PROFILE_SUPERHERO_NOT_CREATED_MSG);
    }

    @Test
    @DisplayName("Should propagate ApiException when superhero not found")
    public void testAddSuperheroSkillProfileThrowsWhenSuperheroNotFound() {
        Long superheroId = 1L;
        SkillProfileDto skillProfileDto = TestStubs.getSkillProfileDtoStub();

        when(superheroService.getSuperhero(superheroId))
                .thenThrow(new ApiException(HttpStatus.NOT_FOUND, "Superhero not found"));

        ApiException exception = assertThrows(ApiException.class,
                () -> subject.addSuperheroSkillProfile(superheroId, skillProfileDto));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getReason()).isEqualTo("Superhero not found");
    }

    @Test
    @DisplayName("Should throw ApiException when adding skill profile fails due to data integrity violation")
    public void testAddSuperheroSkillProfileThrows() {
        Long superheroId = 1L;
        SkillProfileDto skillProfileDto = TestStubs.getSkillProfileDtoStub();

        when(superheroService.getSuperhero(superheroId))
                .thenReturn(TestStubs.getSuperheroStub());
        when(skillService.getSkill(skillProfileDto.skill().id()))
                .thenReturn(new Skill(1L, "TestSkill"));
        when(skillProfileRepository.save(any(SkillProfile.class)))
                .thenThrow(new DataIntegrityViolationException("Data integrity violation"));

        ApiException exception = assertThrows(ApiException.class,
                () -> subject.addSuperheroSkillProfile(superheroId, skillProfileDto));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getReason()).isEqualTo(
                SkillProfileService.SKILL_PROFILE_SUPERHERO_NOT_CREATED_MSG);
    }

    @Test
    @DisplayName("Should update skill profile intensity when value is provided")
    public void testChangeSuperheroSkillProfileUpdatesIntensity() {
        Long superheroId = 1L;
        Long skillId = 1L;
        Integer newIntensity = 80;

        SkillProfile originalProfile = TestStubs.getSkillProfileStub();
        SkillProfile updatedProfile = TestStubs.getSkillProfileStub();
        updatedProfile.setIntensity(newIntensity);

        SkillProfileDto updateDto = new SkillProfileDto(
                null,
                superheroId,
                newIntensity,
                TestStubs.getSkillDtoStub()
        );

        when(superheroService.getSuperhero(superheroId))
                .thenReturn(TestStubs.getSuperheroStub());
        when(skillProfileRepository.findBySuperheroIdAndSkillId(superheroId, skillId))
                .thenReturn(Optional.of(originalProfile));
        when(skillProfileRepository.save(originalProfile))
                .thenReturn(updatedProfile);

        SkillProfileDto result = subject.changeSuperheroSkillProfile(superheroId, skillId,
                updateDto);

        assertThat(result.intensity()).isEqualTo(newIntensity);
        verify(superheroService).getSuperhero(superheroId);
        verify(skillProfileRepository).findBySuperheroIdAndSkillId(superheroId, skillId);
        verify(skillProfileRepository).save(originalProfile);
    }

    @Test
    @DisplayName("Should not update skill profile when intensity is null")
    public void testChangeSuperheroSkillProfileWillUpdateNothing() {
        Long superheroId = 1L;
        Long skillId = 1L;

        SkillProfile originalProfile = TestStubs.getSkillProfileStub();
        Integer originalIntensity = originalProfile.getIntensity();

        SkillProfileDto updateDto = new SkillProfileDto(
                null,
                null,
                null,
                TestStubs.getSkillDtoStub()
        );

        when(superheroService.getSuperhero(superheroId))
                .thenReturn(TestStubs.getSuperheroStub());
        when(skillProfileRepository.findBySuperheroIdAndSkillId(superheroId, skillId))
                .thenReturn(Optional.of(originalProfile));
        when(skillProfileRepository.save(originalProfile))
                .thenReturn(originalProfile);

        SkillProfileDto result = subject.changeSuperheroSkillProfile(superheroId, skillId,
                updateDto);

        assertThat(result.intensity()).isEqualTo(originalIntensity);
        verify(superheroService).getSuperhero(superheroId);
        verify(skillProfileRepository).findBySuperheroIdAndSkillId(superheroId, skillId);
        verify(skillProfileRepository).save(originalProfile);
    }

    @Test
    @DisplayName("Should propagate ApiException when superhero not found during update")
    public void testChangeSuperheroSkillProfileThrowsWhenSuperheroNotFound() {
        Long superheroId = 1L;
        Long skillId = 1L;
        SkillProfileDto updateDto = TestStubs.getSkillProfileDtoStub();

        when(superheroService.getSuperhero(superheroId))
                .thenThrow(new ApiException(HttpStatus.NOT_FOUND, "Superhero not found"));

        ApiException exception = assertThrows(ApiException.class,
                () -> subject.changeSuperheroSkillProfile(superheroId, skillId, updateDto));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getReason()).isEqualTo("Superhero not found");
    }

    @Test
    @DisplayName("Should throw ApiException when updating skill profile fails due to data integrity violation")
    public void testChangeSuperheroSkillProfileThrows() {
        Long superheroId = 1L;
        Long skillId = 1L;

        SkillProfile skillProfile = TestStubs.getSkillProfileStub();
        SkillProfileDto updateDto = TestStubs.getSkillProfileDtoStub();

        when(superheroService.getSuperhero(superheroId))
                .thenReturn(TestStubs.getSuperheroStub());
        when(skillProfileRepository.findBySuperheroIdAndSkillId(superheroId, skillId))
                .thenReturn(Optional.of(skillProfile));
        when(skillProfileRepository.save(skillProfile))
                .thenThrow(new DataIntegrityViolationException("Data integrity violation"));

        ApiException exception = assertThrows(ApiException.class,
                () -> subject.changeSuperheroSkillProfile(superheroId, skillId, updateDto));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(exception.getReason()).isEqualTo(
                SkillProfileService.SKILL_PROFILE_SUPERHERO_NOT_UPDATED_MSG);
    }

    @Test
    @DisplayName("Should delete skill profile successfully")
    public void testDeleteSuperheroSkillProfile() {
        Long superheroId = 1L;
        Long skillId = 1L;
        SkillProfile skillProfile = TestStubs.getSkillProfileStub();

        when(skillProfileRepository.findBySuperheroIdAndSkillId(superheroId, skillId))
                .thenReturn(Optional.of(skillProfile));

        SkillProfileDto result = subject.deleteSuperheroSkillProfile(superheroId, skillId);

        assertThat(result).isNotNull();
        assertThat(result.intensity()).isEqualTo(skillProfile.getIntensity());
        verify(skillProfileRepository).findBySuperheroIdAndSkillId(superheroId, skillId);
        verify(skillProfileRepository).delete(skillProfile);
    }

    @Test
    @DisplayName("Should throw ApiException when trying to delete non-existent skill profile")
    public void testDeleteSuperheroSkillProfileThrowsWhenNotFound() {
        Long superheroId = 1L;
        Long skillId = 1L;

        when(skillProfileRepository.findBySuperheroIdAndSkillId(superheroId, skillId))
                .thenReturn(Optional.empty());

        ApiException exception = assertThrows(ApiException.class,
                () -> subject.deleteSuperheroSkillProfile(superheroId, skillId));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(exception.getReason()).contains(
                "Skill profile not found for superhero by superhero id 1 and skill id 1");
    }
}