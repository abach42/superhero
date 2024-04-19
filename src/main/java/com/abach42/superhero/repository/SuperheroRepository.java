package com.abach42.superhero.repository;

import java.util.Optional;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

import com.abach42.superhero.entity.Superhero;

public interface SuperheroRepository extends JpaRepository<Superhero, Long> {
    @Override
    default @NonNull Page<Superhero> findAll(@NonNull Pageable pageable) {
        return findAllByDeletedIsFalse(pageable);
    }

    public Page<Superhero> findAllByDeletedIsFalse(Pageable pageable);

    Optional<Superhero> findOneByIdAndDeletedIsFalse(Long id);

    @Override
    public default @NonNull Optional<Superhero> findById(@NonNull Long id) {
        return findOneByIdAndDeletedIsFalse(id);
    }

    @Override
    default long count() {
        return countByDeletedIsFalse();
    }

    public long countByDeletedIsFalse();

    public long countByDeletedIsTrue();

    @Transactional
    public void deleteByDeletedIsTrue();
}
