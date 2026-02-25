package com.abach42.superhero.syslog;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SysLogCronjob {

    private final SysLogService sysLogService;

    public SysLogCronjob(SysLogService sysLogService) {
        this.sysLogService = sysLogService;
    }

    @Scheduled(cron = "0 */10 * * * *")
    public void notifyManySysLogs() {
        long count = sysLogService.countAll();

        if(count < 100_000) {
            return;
        }

        sysLogService.sendCountNotification(count);
    }

    @Scheduled(cron = "0 0 8 * * *")
    public void notifySysLogs() {
        long count = sysLogService.countUnread();

        if (count == 0) {
            return;
        }

        sysLogService.sendLogFileNotification(count);
    }
}
