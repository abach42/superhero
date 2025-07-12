package com.abach42.superhero.testconfiguration;

import com.abach42.superhero.skill.Skill;
import com.abach42.superhero.skill.SkillDto;
import com.abach42.superhero.skillprofile.SkillProfile;
import com.abach42.superhero.skillprofile.SkillProfileDto;
import com.abach42.superhero.skillprofile.SkillProfileListDto;
import com.abach42.superhero.superhero.Gender;
import com.abach42.superhero.superhero.Superhero;
import com.abach42.superhero.superhero.SuperheroDto;
import com.abach42.superhero.user.ApplicationUser;
import com.abach42.superhero.user.ApplicationUserDto;
import com.abach42.superhero.user.UserRole;
import java.time.LocalDate;
import java.util.List;

/*
 * static to be able to run in unit on missing IoC
 */
public class TestStubs {

    public static Superhero getSuperheroStub() {
        return new Superhero("foo", "bar", LocalDate.of(1970, 1, 1), Gender.MALE, "foo",
                "foo", new ApplicationUser("foo", null, UserRole.USER));
    }

    public static Superhero getSuperheroStubWithPassword() {
        return new Superhero("some", "bar", LocalDate.of(1970, 1, 1), Gender.MALE, "foo",
                "foo", new ApplicationUser("unique", "foo", UserRole.USER));
    }

    public static SuperheroDto getSuperheroDtoStub() {
        return SuperheroDto.fromDomain(getSuperheroStub());
    }

    public static SuperheroDto getSuperheroDtoStubEmpty() {
        return new SuperheroDto(0L, null, null, null, null, null, null, null);
    }

    public static SuperheroDto getSuperheroDtoStubWithPassword() {
        return new SuperheroDto(1L, "new", "foo", LocalDate.of(1970, 1, 1), Gender.NOT_PROVIDED,
                "foo", "foo", new ApplicationUserDto("new", "bar", UserRole.USER));
    }

    public static Skill getSkillStub() {
        return new Skill(1L, "foo");
    }

    public static SkillDto getSkillDtoStub() {
        return SkillDto.fromDomain(getSkillStub());
    }

    public static SkillProfile getSkillProfileStub() {
        return new SkillProfile(1L, 1, getSkillStub());
    }

    public static SkillProfileDto getSkillProfileDtoStub() {
        return SkillProfileDto.fromDomain(getSkillProfileStub());
    }

    public static SkillProfileListDto getSkillProfileListDtoStub() {
        return new SkillProfileListDto(List.of(getSkillProfileDtoStub()));
    }

    public static SkillProfile getSkillProfileToCreateStub() {
        return new SkillProfile(null, 1, new Skill(3L, null));
    }

    public static SkillProfile getSkillProfileToUpdateStub() {
        return new SkillProfile(null, 1, null);
    }

    public static SkillProfileDto getSkillProfileDtoToUpdateStub() {
        return new SkillProfileDto(null, null, 1, null);
    }
}