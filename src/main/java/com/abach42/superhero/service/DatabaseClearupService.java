package com.abach42.superhero.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.abach42.superhero.repository.SuperheroRepository;

@Service
@EnableScheduling
public class DatabaseClearupService {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final SuperheroRepository superheroRepository;

    public DatabaseClearupService(SuperheroRepository superheroRepository) {
        this.superheroRepository = superheroRepository;
    }


    @Scheduled(cron = "${abach42.superhero.eraseRecordsMarkedAsDeletedAt}")
    public void ereaseRecordsMarkedAsDeleted() {
        logger.info("ERASE RECORDS marked as DELETED starts as scheduled");
        Long count = superheroRepository.countByDeletedIsTrue();
        logger.info("{} records found to delete", count);
        try {
            superheroRepository.deleteByDeletedIsTrue();
            logger.info("Successfully deleted! ERASE RECORDS terminated.");
        } catch (DataAccessException e) {
            Long countAgain = superheroRepository.countByDeletedIsTrue();
            logger.error("Records not deleted. {} records marked as deleted left, cause: {}", countAgain, e.getMessage());
        }
    }
}