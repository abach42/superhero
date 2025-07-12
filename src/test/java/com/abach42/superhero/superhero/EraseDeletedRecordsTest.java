package com.abach42.superhero.superhero;

import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tags(value = {@Tag("unit"), @Tag("superhero")})
@ExtendWith(MockitoExtension.class)
class EraseDeletedRecordsTest {

    @Mock
    private DatabaseCleanupService databaseCleanupService;

    private EraseDeletedRecords subject;

    @BeforeEach
    void setUp() {
        subject = new EraseDeletedRecords(databaseCleanupService);
    }

    @Test
    @DisplayName("Should call database cleanup service when manually erasing deleted records")
    void shouldCallDatabaseCleanupServiceWhenManuallyErasingDeletedRecords() {
        subject.manuallyEraseMarkedAsDeleted();

        verify(databaseCleanupService).eraseRecordsMarkedAsDeleted();
    }
}
