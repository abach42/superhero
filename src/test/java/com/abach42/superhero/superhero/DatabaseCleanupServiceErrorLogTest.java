package com.abach42.superhero.superhero;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;

@Tags(value = {@Tag("integration"), @Tag("superhero"), @Tag("cleanup")})
@ExtendWith(MockitoExtension.class)
class DatabaseCleanupServiceErrorLogTest {

    private DatabaseCleanupService databaseCleanupService;

    private ListAppender<ILoggingEvent> logAppender;

    @BeforeEach
    void setup() {
        Logger logger = (Logger) LoggerFactory.getLogger(DatabaseCleanupService.class);
        logAppender = new ListAppender<>();
        logAppender.start();
        logger.addAppender(logAppender);

        SuperheroRepository superheroRepositoryMock = mock(SuperheroRepository.class);
        when(superheroRepositoryMock.countByDeletedIsTrue()).thenReturn(1L);
        doThrow(new DataAccessException("simulated db error") {})
                .when(superheroRepositoryMock).deleteByDeletedIsTrue();

        databaseCleanupService = new DatabaseCleanupService(superheroRepositoryMock);

        when(superheroRepositoryMock.countByDeletedIsTrue()).thenReturn(1L);
    }

    @Test
    void shouldLogErrorIfDeletionFails() {
        databaseCleanupService.eraseRecordsMarkedAsDeleted();

        List<String> logs = logAppender.list.stream()
                .map(ILoggingEvent::getFormattedMessage)
                .toList();

        assertThat(logs).anyMatch(
                msg -> msg.contains("Records not deleted. 1 records marked as deleted left"));
        assertThat(logs).anyMatch(msg -> msg.contains("simulated db error"));
    }
}
