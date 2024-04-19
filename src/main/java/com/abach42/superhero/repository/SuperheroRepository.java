package com.abach42.superhero.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abach42.superhero.entity.Superhero;

public interface SuperheroRepository extends JpaRepository<Superhero, Long> {
    // todo sort, pagable
    // todo not deleted
}
