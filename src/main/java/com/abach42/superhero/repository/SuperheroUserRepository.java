package com.abach42.superhero.repository;

import com.abach42.superhero.entity.SuperheroUser;
import jakarta.annotation.Nonnull;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SuperheroUserRepository extends JpaRepository<SuperheroUser, Long> {

    Optional<SuperheroUser> findOneByEmailAndDeletedIsFalse(@Nonnull String email);
}
