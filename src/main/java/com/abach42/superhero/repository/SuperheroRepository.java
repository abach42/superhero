package com.abach42.superhero.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abach42.superhero.entity.Superhero;

public interface SuperheroRepository extends JpaRepository<Superhero, Long>{
    List<Superhero> findAll();
    Optional<Superhero> findById(Long id);
    //sort, pagable
}
