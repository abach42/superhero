package com.abach42.superhero.unit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.abach42.superhero.dto.SuperheroUserDto;
import com.abach42.superhero.entity.SuperheroUser;
import com.abach42.superhero.exception.UserNotFoundException;
import com.abach42.superhero.repository.SuperheroUserRepository;
import com.abach42.superhero.service.SuperheroUserService;

@ExtendWith(MockitoExtension.class)
public class SuperheroUserServiceTest {

    @Mock
    private SuperheroUserRepository superheroUserRepository;

    @InjectMocks
    private SuperheroUserService subject;

    @Test
    @DisplayName("Get superhero can be converted")
    public void testRetrieveSuperheroUser() {
        String email = "test@example.com";
        SuperheroUser superheroUser = new SuperheroUser(email, "foo", "user");
        superheroUser.setEmail(email);

        given(superheroUserRepository.findOneByEmailAndDeletedIsFalse(email)).willReturn(Optional.of(superheroUser));

        SuperheroUserDto dto = subject.retrieveSuperheroUser(email);
        assertNotNull(dto);
        assertThat(email).isEqualTo(dto.email());
    }

    @Test
    @DisplayName("Get superhero throws when empty")
    public void testRetrieveSuperheroUserFailsNotFound() {
        String email = "test@example.com";
        given(superheroUserRepository.findOneByEmailAndDeletedIsFalse(email)).willReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> subject.retrieveSuperheroUser(email));
    }
}
