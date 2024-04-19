package com.abach42.superhero.service;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

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
