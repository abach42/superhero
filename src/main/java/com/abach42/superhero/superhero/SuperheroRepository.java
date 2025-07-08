package com.abach42.superhero.superhero;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface SuperheroRepository extends JpaRepository<Superhero, Long> {

    @Override
    default @NonNull Page<Superhero> findAll(@NonNull Pageable pageable) {
        return findAllByDeletedIsFalse(pageable);
    }

    @Override
    default @NonNull List<Superhero> findAll() {
        return findAllByDeletedIsFalse();
    }

    Page<Superhero> findAllByDeletedIsFalse(Pageable pageable);

    List<Superhero> findAllByDeletedIsFalse();

    @Override
    default @NonNull Optional<Superhero> findById(@NonNull Long id) {
        return findOneByIdAndDeletedIsFalse(id);
    }

    Optional<Superhero> findOneByIdAndDeletedIsFalse(Long id);

    @Override
    default long count() {
        return countByDeletedIsFalse();
    }

    long countByDeletedIsFalse();

    long countByDeletedIsTrue();

    @Transactional
    void deleteByDeletedIsTrue();
}
