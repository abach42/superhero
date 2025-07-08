package com.abach42.superhero.shell;

import org.springframework.context.annotation.Profile;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@Profile("!test")
@ShellComponent
public class EraseDeletedRecords {

    DatabaseCleanupService databaseCleanupService;

    public EraseDeletedRecords(DatabaseCleanupService databaseCleanupService) {
        this.databaseCleanupService = databaseCleanupService;
    }

    @ShellMethod("Manually start to erase records which are marked as deleted.")
    public void manuallyEraseMarkedAsDeleted() {
        databaseCleanupService.eraseRecordsMarkedAsDeleted();
    }
}
