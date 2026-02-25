package com.abach42.superhero.syslog;

import com.abach42.superhero.shared.api.ApiException;
import com.abach42.superhero.shared.mail.EmailService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SysLogService {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final SysLogRepository sysLogRepository;

    private final EmailService emailService;

    private final SysLogCsvService sysLogCsvService;

    public SysLogService( SysLogRepository sysLogRepository,
        EmailService emailService, SysLogCsvService sysLogCsvService) {
        this.sysLogRepository = sysLogRepository;
        this.emailService = emailService;
        this.sysLogCsvService = sysLogCsvService;
    }

    public void persist(SysLog issueSysLog) {
        sysLogRepository.save(issueSysLog);
    }

    public Sort retrieveSort(String sortBy, String sortDir) {
        return sortDir.equalsIgnoreCase("desc")
            ? Sort.by(sortBy).descending()
            : Sort.by(sortBy).ascending();
    }

    public Page<SysLog> retrieveAll(Pageable pageable) {
        return sysLogRepository.findAll(pageable);
    }

    public long countAll() {
        return sysLogRepository.count();
    }

    public long countUnread() {
        return sysLogRepository.countByReadFalse();
    }

    public SysLog retrieveById(Long id) {
        Optional<SysLog> maybeSyslog = sysLogRepository.findById(id);

        if(maybeSyslog.isEmpty()) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Syslog not found" + id);
        }

        return maybeSyslog.get();
    }

    public SysLog deleteById(Long id) {
        SysLog syslog = retrieveById(id);

        sysLogRepository.deleteById(id);

        return syslog;
    }

    public void deleteAll() {
        sysLogRepository.deleteAll();
    }

    public SysLogListDto search(String query, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<SysLog> resultPage = sysLogRepository.findByMessageContaining(query, pageable);

        return new SysLogListDto(
                resultPage.getContent(),
                pageable.getPageNumber(),
                resultPage.getTotalElements(),
                resultPage.getTotalPages()
        );
    }

    public SysLog setRead(Long id) {
        SysLog syslog = retrieveById(id);
        syslog.setRead(true);
        return sysLogRepository.save(syslog);
    }

    public void setAllRead() {
        List<SysLog> allLogs = sysLogRepository.findAll();

        for (SysLog sysLog : allLogs) {
            sysLog.setRead(true);
        }

        sysLogRepository.saveAll(allLogs);
    }

    public String exportAllUnreadToCsv() {
        List<SysLog> allUnreadLogs = sysLogRepository.findAllByReadFalseOrderByCreateDateTimeDesc();

        return sysLogCsvService.buildCsvContent(allUnreadLogs);
    }

    public String exportAllToCsv(String sortBy, String sortDir) {
        Sort sort = retrieveSort(sortBy, sortDir);
        List<SysLog> allLogs = sysLogRepository.findAll(sort);

        return sysLogCsvService.buildCsvContent(allLogs);
    }

    public static String exportFilename() {
        return "syslog_export_" +
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) + ".csv";
    }

    public Long countAllOlderThan(LocalDateTime dateTime) {
        return sysLogRepository.countAllByCreateDateTimeBefore(dateTime);
    }

    @Transactional
    public void deleteAllOlderThan(LocalDateTime dateTime) {
        sysLogRepository.deleteAllByCreateDateTimeBefore(dateTime);
    }

    public Page<SysLog> retrieveUnread(Pageable pageable) {
        return sysLogRepository.findAllByReadFalse(pageable);
    }

    public void sendCountNotification(Long count) {
        new CountNotifier(count).send();
    }

    public void sendLogFileNotification(Long count) {
        new LogFileNotifier(count).send();
        setAllRead();
    }

    private abstract static class AbstractNotifier {
        protected Long count;

        AbstractNotifier(Long count) {
            this.count = count;
        }

        abstract void send(); //should be

        protected String buildSubject() {
            return "Superhero System Notification ";
        }

        protected String buildBody() {
            return "Superhero System Notification  more than " + count + " System Notification(s) found.\n\n";
        }
    }

    class CountNotifier extends AbstractNotifier {
        CountNotifier(Long count) {
            super(count);
        }

        @Override
        void send() {
            String subject = buildSubject();
            String body = buildBody();

            emailService.sendEmail("foof@bar", subject, body);
        }
    }

    class LogFileNotifier extends AbstractNotifier {
        LogFileNotifier(Long count) {
            super(count);
        }

        @Override
        void send() {
            String subject = buildSubject();
            String body = buildBody();

            String csvContent = exportAllUnreadToCsv();
            String filename = exportFilename();

            emailService.sendEmailWithAttachment(
                "foof@bar",
                subject,
                body,
                csvContent,
                filename
            );
        }
    }

    public static LocalDateTime getDateTimeDaysAgo(int days) {
        return LocalDateTime.now().minusDays(days);
    }
}
