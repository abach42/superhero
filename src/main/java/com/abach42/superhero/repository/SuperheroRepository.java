package com.abach42.superhero.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abach42.superhero.entity.Superhero;

public interface SuperheroRepository extends JpaRepository<Superhero, Long>{
    //List<Superhero> findAll(); //todo not necessary
    Optional<Superhero> findById(Long id); //todo not necessary
    //todo sort, pagable
    //todo not deleted
}
