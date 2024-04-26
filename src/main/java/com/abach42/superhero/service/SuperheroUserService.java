package com.abach42.superhero.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.abach42.superhero.entity.SuperheroUser;
import com.abach42.superhero.entity.dto.UserDto;
import com.abach42.superhero.exception.UserNotFoundException;
import com.abach42.superhero.repository.SuperheroUserRepository;

@Service
public class SuperheroUserService {
    SuperheroUserRepository superheroUserRepository;

    public SuperheroUserService(SuperheroUserRepository superheroUserRepository) {
        this.superheroUserRepository = superheroUserRepository;
    }

    public UserDto getSuperheroConverted(String email) {
        Optional<SuperheroUser> user = superheroUserRepository.findOneByEmailAndDeletedIsFalse(email);
        return UserDto.fromDomain(user.orElseThrow(() -> new UserNotFoundException(email)));
    }
}
