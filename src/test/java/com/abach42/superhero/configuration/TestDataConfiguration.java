package com.abach42.superhero.configuration;

import java.time.LocalDate;
import java.util.List;

import com.abach42.superhero.dto.SkillDto;
import com.abach42.superhero.dto.SkillProfileDto;
import com.abach42.superhero.dto.SkillProfileListDto;
import com.abach42.superhero.dto.SuperheroDto;
import com.abach42.superhero.dto.SuperheroUserDto;
import com.abach42.superhero.entity.Skill;
import com.abach42.superhero.entity.SkillProfile;
import com.abach42.superhero.entity.Superhero;
import com.abach42.superhero.entity.SuperheroUser;

/*
 * static to be able to run in unit on missing IoC 
 */
public class TestDataConfiguration {
    public static final Superhero getSuperheroStub() {
        return new Superhero("foo", "bar", LocalDate.of(1970, 1, 1), "Male", "foo", 
            "foo", new SuperheroUser("foo", null, "USER"));
    }

    public static final Superhero getSuperheroStubWithPassword() {
        return new Superhero("some", "bar", LocalDate.of(1970, 1, 1), "Male", "foo", 
            "foo", new SuperheroUser("unique", "foo", "USER"));
    }

    public static final SuperheroDto getSuperheroDtoStub() {
        return SuperheroDto.fromDomain(getSuperheroStub());
    }

    public static final SuperheroDto getSuperheroDtoStubEmpty() {
        return new SuperheroDto(0L, null, null, null, null, null, null, null);
    }

    public static final SuperheroDto getSuperheroDtoStubWithPassword() {
        return new SuperheroDto(1L, "new", "foo", LocalDate.of(1970,1,1), "foo",
                                    "foo", "foo", new SuperheroUserDto("new", "bar", "USER"));
    }

    public static final Skill getSkillStub() {
        return new Skill(1L, "foo");
    }

    public static final SkillDto getSkillDtoStub() {
        return SkillDto.fromDomain(getSkillStub());
    }

    public static final SkillProfile getSkillProfileStub() {
        return new SkillProfile(1L, 1, getSkillStub());
    }

    public static final SkillProfileDto getSkillProfileDtoStub() {
        return SkillProfileDto.fromDomain(getSkillProfileStub());
    }

    public static final SkillProfileListDto getSkillProfileListDtoStub() {
        return new SkillProfileListDto(List.of(getSkillProfileDtoStub()));
    }

    public static final SkillProfile getSkillProfileToCreateStub() {
        return new SkillProfile(null, 1, new Skill(3L, null));
    }

    public static final SkillProfile getSkillProfileToUpdateStub() {
        return new SkillProfile(null, 1, null);
    }
    
    public static final SkillProfileDto getSkillProfileDtoToUpdateStub() {
        return new SkillProfileDto(null, null, 1, null);
    }
}