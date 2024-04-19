package com.abach42.superhero.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import com.abach42.superhero.entity.Superhero;

public interface SuperheroRepository extends JpaRepository<Superhero, Long> {
    // TODO sort, pagable
    Optional<Superhero> findOneByIdAndDeleted(Long id, boolean deleted);

    @Override
    public default Optional<Superhero> findById(Long id) {
        return findOneByIdAndDeleted(id, false);
    }

    public long countByDeletedIsTrue();

    @Transactional
    public void deleteByDeletedIsTrue();
}
