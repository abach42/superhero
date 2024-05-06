package com.abach42.superhero.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.abach42.superhero.entity.SuperheroUser;

import jakarta.annotation.Nonnull;

@Repository
public interface SuperheroUserRepository extends JpaRepository<SuperheroUser, Long>{
    public Optional<SuperheroUser> findOneByEmailAndDeletedIsFalse(@Nonnull String email);
}
