package com.abach42.superhero.superhero;

import static org.assertj.core.api.Assertions.assertThat;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.abach42.superhero.testconfiguration.TestContainerConfiguration;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.testcontainers.junit.jupiter.Testcontainers;

@Tags(value = {@Tag("integration"), @Tag("superhero"), @Tag("cleanup")})
@SpringBootTest(classes = {TestContainerConfiguration.class}, properties = {
        "com.abach42.superhero.database-cleanup-service.enabled=true"})
@Testcontainers
@Import({DatabaseCleanupService.class})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
class DatabaseCleanupServiceLogTest {

    @Autowired
    private DatabaseCleanupService databaseCleanupService;

    private ListAppender<ch.qos.logback.classic.spi.ILoggingEvent> logAppender;

    @BeforeEach
    void setupLogCapture() {
        Logger logger = (Logger) LoggerFactory.getLogger(DatabaseCleanupService.class);
        logAppender = new ListAppender<>();
        logAppender.start();
        logger.addAppender(logAppender);
    }

    @Test
    void shouldLogInfoMessagesOnSuccessfulCleanup() {
        databaseCleanupService.eraseRecordsMarkedAsDeleted();

        List<String> logs = logAppender.list.stream()
                .map(ILoggingEvent::getFormattedMessage)
                .toList();

        assertThat(logs).anyMatch(
                msg -> msg.contains("ERASE RECORDS marked as DELETED starts as scheduled"));
        assertThat(logs).anyMatch(msg -> msg.contains("records found to delete"));
        assertThat(logs).anyMatch(
                msg -> msg.contains("Successfully deleted! ERASE RECORDS terminated."));
    }
}
