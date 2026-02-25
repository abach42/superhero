package com.abach42.superhero.syslog;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SysLogCsvService {
    public static final String CSV_HEADER = buildCsvHeader();

    // CSV Header Column Constants
    public static final String HEADER_ID = "ID";
    public static final String HEADER_CREATE_DATE_TIME = "Create Date Time";
    public static final String HEADER_TYPE = "Type";
    public static final String HEADER_LEVEL = "Level";
    public static final String HEADER_TITLE = "Title";
    public static final String HEADER_MESSAGES = "Messages";
    public static final String HEADER_READ_STATUS = "Read Status";

    // CSV Format Constants
    public static final String CSV_SEPARATOR = ";";
    public static final String CSV_LINE_ENDING = "\n";

    // Status Constants
    public static final String READ_STATUS = "Read";
    public static final String UNREAD_STATUS = "Unread";

    private static String buildCsvHeader() {
        return String.join(CSV_SEPARATOR,
            HEADER_ID,
            HEADER_CREATE_DATE_TIME,
            HEADER_TYPE,
            HEADER_LEVEL,
            HEADER_TITLE,
            HEADER_MESSAGES,
            HEADER_READ_STATUS
        ) + CSV_LINE_ENDING;
    }

    public String buildCsvContent(List<SysLog> logs) {
        StringBuilder csv = new StringBuilder();
        appendCsvHeader(csv);

        appendCsvRows(logs, csv);

        return csv.toString();
    }

    private void appendCsvRows(List<SysLog> logs, StringBuilder csv) {
        if(logs.isEmpty()) {
            return;
        }

        for (SysLog log : logs) {
            appendCsvRow(csv, log);
        }
    }

    private void appendCsvHeader(StringBuilder csv) {
        csv.append(CSV_HEADER);
    }

    private void appendCsvRow(StringBuilder csv, SysLog log) {
        csv.append(escapeCsvField(String.valueOf(log.getId()))).append(CSV_SEPARATOR);
        csv.append(escapeCsvField(formatDateTime(log.getCreateDateTime()))).append(CSV_SEPARATOR);
        csv.append(escapeCsvField(formatType(log))).append(CSV_SEPARATOR);
        csv.append(escapeCsvField(formatLevel(log.getLevel()))).append(CSV_SEPARATOR);
        csv.append(escapeCsvField(log.getTitle())).append(CSV_SEPARATOR);
        csv.append(escapeCsvField(formatMessages(log.getMessage()))).append(CSV_SEPARATOR);
        csv.append(escapeCsvField(log.isRead() ? READ_STATUS : UNREAD_STATUS));
        csv.append("\n");
    }

    private String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }

        return dateTime.toString();
    }

    private String formatType(SysLog log) {
        String actor = log.getActor();

        if (actor == null) {
            return "";
        }

        return actor;
    }

    private String formatMessages(List<String> messages) {
        if (messages == null) {
            return "";
        }

        return String.join(" | ", messages);
    }

    private String formatLevel(Level level) {
        if (level == null) {
            return "";
        }

        return level.toString();
    }

    private String escapeCsvField(String field) {
        if (field == null) {
            return "";
        }

        if (containsCsvSpecialCharacters(field)) {
            return "\"" + field.replace("\"", "\"\"") + "\"";
        }

        return field;
    }

    private boolean containsCsvSpecialCharacters(String field) {
        return field.contains(CSV_SEPARATOR) || field.contains("\"") ||
            field.contains(CSV_LINE_ENDING) || field.contains("\r");
    }
}
