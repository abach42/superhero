package com.abach42.superhero.shell;

import org.springframework.context.annotation.Profile;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import com.abach42.superhero.service.DatabaseClearupService;

@Profile("!test")
@ShellComponent
public class EraseDeletedRecords {
    DatabaseClearupService databaseClearupService; 

    public EraseDeletedRecords(DatabaseClearupService databaseClearupService) {
        this.databaseClearupService = databaseClearupService;
    }

    @ShellMethod("Manually start to erase records which are marked as deleted.")
    public void manuallyEraseMarkedAsDeleted() {
        databaseClearupService.ereaseRecordsMarkedAsDeleted();
    }
}
