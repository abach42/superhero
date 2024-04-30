package com.abach42.superhero.unit.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.abach42.superhero.entity.SuperheroUser;

import jakarta.validation.Validator;

public class SuperheroUserTest {

    @Mock
    private Validator validator;

    @InjectMocks
    private SuperheroUser superheroUser;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGettersAndSetters() {
        SuperheroUser superheroUser = new SuperheroUser("foo", "bar", "baz");
        assertEquals("foo", superheroUser.getEmail());
        assertEquals("bar", superheroUser.getPassword());
        assertEquals("baz", superheroUser.getRole());
        assertEquals(false, superheroUser.getDeleted());

        superheroUser.setEmail("test@example.com");
        superheroUser.setPassword("password");
        superheroUser.setRole("USER");
        superheroUser.setDeleted(true);

        assertEquals("test@example.com", superheroUser.getEmail());
        assertEquals("password", superheroUser.getPassword());
        assertEquals("USER", superheroUser.getRole());
        assertEquals(true, superheroUser.getDeleted());
    }
}
