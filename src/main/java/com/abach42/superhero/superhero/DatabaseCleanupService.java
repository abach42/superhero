package com.abach42.superhero.superhero;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@ConditionalOnProperty(name = "com.abach42.superhero.database-cleanup-service.enabled", havingValue = "true")
@Service
@EnableScheduling
public class DatabaseCleanupService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final SuperheroRepository superheroRepository;

    public DatabaseCleanupService(SuperheroRepository superheroRepository) {
        this.superheroRepository = superheroRepository;
    }

    @Scheduled(cron = "${com.abach42.superhero.database-cleanup-service.erase-soft-deleted-at}")
    public void eraseRecordsMarkedAsDeleted() {
        logger.info("ERASE RECORDS marked as DELETED starts as scheduled");
        Long count = superheroRepository.countByDeletedIsTrue();
        logger.info("{} records found to delete", count);

        try {
            superheroRepository.deleteByDeletedIsTrue();
            logger.info("Successfully deleted! ERASE RECORDS terminated.");
        } catch (DataAccessException e) {
            Long countAgain = superheroRepository.countByDeletedIsTrue();
            logger.error("Records not deleted. {} records marked as deleted left, cause: {}",
                    countAgain,
                    e.getMessage());
        }
    }
}