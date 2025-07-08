package com.abach42.superhero.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationUserRepository extends JpaRepository<ApplicationUser, Long> {

    Optional<ApplicationUser> findOneByEmailAndDeletedIsFalse(String email);
}
