package com.abach42.superhero.user;

import org.springframework.stereotype.Service;

@Service
public class ApplicationUserService {

    ApplicationUserRepository applicationUserRepository;

    public ApplicationUserService(ApplicationUserRepository applicationUserRepository) {
        this.applicationUserRepository = applicationUserRepository;
    }

    public ApplicationUser retrieveUserByEmail(String email) {
        return applicationUserRepository.findOneByEmailAndDeletedIsFalse(email)
                .orElseThrow(() -> new UserNotFoundException(email));
    }
}
