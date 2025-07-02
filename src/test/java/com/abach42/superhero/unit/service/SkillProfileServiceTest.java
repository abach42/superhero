package com.abach42.superhero.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import com.abach42.superhero.configuration.TestDataConfiguration;
import com.abach42.superhero.dto.SkillProfileDto;
import com.abach42.superhero.dto.SkillProfileListDto;
import com.abach42.superhero.entity.Skill;
import com.abach42.superhero.entity.SkillProfile;
import com.abach42.superhero.exception.ApiException;
import com.abach42.superhero.repository.SkillProfileRepository;
import com.abach42.superhero.service.SkillProfileService;
import com.abach42.superhero.service.SkillService;
import com.abach42.superhero.service.SuperheroService;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@ExtendWith(MockitoExtension.class)
public class SkillProfileServiceTest {

    @MockitoBean
    private SkillProfileRepository skillProfileRepository;

    @MockitoBean
    private SuperheroService superheroService;

    @MockitoBean
    private SkillService skillService; // used by Mockito injection

    @InjectMocks
    private SkillProfileService subject;

    @Test
    @DisplayName("Get Superhero skill profile list will throw when empty")
    public void testRetrieveSuperheroSkillProfileListWillThrowWhenEmpty() {
        Long superheroId = 1L;
        List<SkillProfile> profiles = Collections.emptyList();
        given(skillProfileRepository.findBySuperheroIdOrderBySkillId(superheroId)).willReturn(
                profiles);

        ApiException exception = assertThrows(ApiException.class,
                () -> subject.retrieveSuperheroSkillProfileList(superheroId));
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("Get Superhero skill profile list will return profile list")
    public void testRetrieveSuperheroSkillProfileListWillReturnProfileList() {
        Long superheroId = 1L;
        List<SkillProfile> profiles = List.of(TestDataConfiguration.getSkillProfileStub());
        given(skillProfileRepository.findBySuperheroIdOrderBySkillId(superheroId)).willReturn(
                profiles);

        SkillProfileListDto actual = subject.retrieveSuperheroSkillProfileList(superheroId);
        assertThat(actual).usingRecursiveComparison()
                .isEqualTo(TestDataConfiguration.getSkillProfileListDtoStub());
    }

    @Test
    @DisplayName("Get Superhero skill profile can be converted")
    public void testRetrieveSuperheroSkillProfileConverted() {
        Long superheroId = 1L;
        Long skillId = 1L;
        SkillProfile skillProfile = new SkillProfile(superheroId, 50, new Skill(skillId, "foo"));
        given(skillProfileRepository.findBySuperheroIdAndSkillId(superheroId, skillId)).willReturn(
                Optional.of(skillProfile));

        SkillProfileDto dto = subject.retrieveSuperheroSkillProfile(superheroId, skillId);
        assertNotNull(dto);
        assertThat(skillProfile.getIntensity()).isEqualTo(dto.intensity());
    }

    @Test
    @DisplayName("Add Superhero skill profile throws")
    public void testAddSuperheroSkillProfileThrows() {
        given(skillProfileRepository.save(any(SkillProfile.class)))
                .willThrow(new DataIntegrityViolationException("boo"));

        ApiException exception = assertThrows(ApiException.class,
                () -> subject.addSuperheroSkillProfile(1L,
                        TestDataConfiguration.getSkillProfileDtoStub()));
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    @DisplayName("Change Superhero skill profile will update nothing on input null")
    public void testChangeSuperheroSkillProfileWillUpdateNothing() {
        given(superheroService.getSuperhero(anyLong())).willReturn(
                TestDataConfiguration.getSuperheroStub());

        SkillProfile orig = TestDataConfiguration.getSkillProfileStub();
        given(skillProfileRepository.findBySuperheroIdAndSkillId(anyLong(), anyLong()))
                .willReturn(Optional.of(orig));

        given(skillProfileRepository.save(any(SkillProfile.class))).willReturn(orig);

        SkillProfile update = TestDataConfiguration.getSkillProfileStub();
        update.setIntensity(null);
        SkillProfileDto actual = subject.changeSuperheroSkillProfile(1L, 1L,
                SkillProfileDto.fromDomain(update));
        assertThat(actual.intensity()).isEqualTo(orig.getIntensity());
    }

    @Test
    @DisplayName("Change Superhero skill profile throws")
    public void testChangeSuperheroSkillProfileThrows() {
        given(superheroService.getSuperhero(anyLong())).willReturn(
                TestDataConfiguration.getSuperheroStub());
        SkillProfile skillProfileStub = TestDataConfiguration.getSkillProfileStub();
        given(skillProfileRepository.findBySuperheroIdAndSkillId(anyLong(), anyLong())).willReturn(
                Optional.of(skillProfileStub));

        given(skillProfileRepository.save(skillProfileStub))
                .willThrow(new DataIntegrityViolationException("boo"));

        ApiException exception = assertThrows(ApiException.class,
                () -> subject.changeSuperheroSkillProfile(1L, 1L,
                        TestDataConfiguration.getSkillProfileDtoStub()));
        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }
}