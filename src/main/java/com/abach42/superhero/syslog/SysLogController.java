package com.abach42.superhero.syslog;

import com.abach42.superhero.login.methodsecurity.IsAdmin;
import com.abach42.superhero.shared.api.ApiException;
import com.abach42.superhero.shared.api.PathConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "SysLog")
@RestController
@RequestMapping(path = PathConfig.SYSLOG)
@IsAdmin
public class SysLogController {

    private final SysLogService sysLogService;

    public SysLogController(SysLogService sysLogService) {
        this.sysLogService = sysLogService;
    }

    @Operation(summary = "Get complete sys log")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Logs found")
    })
    @GetMapping
    public Page<SysLog> getAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "createDateTime") String sortBy,
        @RequestParam(defaultValue = "desc") String sortDir) {

        Sort sort = sysLogService.retrieveSort(sortBy, sortDir);

        Pageable pageable = PageRequest.of(page, size, sort);
        return sysLogService.retrieveAll(pageable);
    }

    @Operation(summary = "Get unread sys log")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Unread logs found")
    })
    @GetMapping("/unread")
    public Page<SysLog> getUnread(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "createDateTime") String sortBy,
        @RequestParam(defaultValue = "desc") String sortDir) {
        Sort sort = sysLogService.retrieveSort(sortBy, sortDir);
        Pageable pageable = PageRequest.of(page, size, sort);
        return sysLogService.retrieveUnread(pageable);
    }

    @Operation(summary = "Get count of sys log")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Number of sys log entries")
    })
    @GetMapping("/count")
    public long count() {
        return sysLogService.countAll();
    }

    @Operation(summary = "Search sys log")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Sys log search result"),
    })
    @GetMapping("/search")
    public SysLogListDto search(
        @RequestParam String query,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) throws ApiException {
        return sysLogService.search(query, page, size);
    }

    @Operation(summary = "Get sys log by id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Sys log found")
    })
    @GetMapping("/{id}")
    public SysLog getById(@PathVariable Long id) throws ApiException {
        return sysLogService.retrieveById(id);
    }

    @Operation(summary = "Set sys log status to read")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Sys log status set to read"),
    })
    @PutMapping("/{id}/read")
    public ResponseEntity<SysLog> setRead(@PathVariable Long id) {
        SysLog log = sysLogService.setRead(id);
        return ResponseEntity.ok(log);
    }

    @Operation(summary = "Export all sys logs as CSV")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "CSV file with all sys logs",
            content = @Content(mediaType = "text/csv")
        )
    })
    @GetMapping("/export")
    public ResponseEntity<String> exportCsv(
        @RequestParam(defaultValue = "createDateTime") String sortBy,
        @RequestParam(defaultValue = "desc") String sortDir) {

        String csvContent = sysLogService.exportAllToCsv(sortBy, sortDir);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        headers.setContentDispositionFormData("attachment", SysLogService.exportFilename());

        return ResponseEntity.ok()
            .headers(headers)
            .body(csvContent);
    }

    @Operation(summary = "Delete all sys log entries.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Number of all sys log entries deleted."),
    })
    @DeleteMapping("/delete-all")
    public ResponseEntity<Long> deleteAll() {
        Long count = sysLogService.countAll();
        sysLogService.deleteAll();
        return ResponseEntity.ok(count);
    }
    
    @Operation(summary = "Delete sys log entries older than given days.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Number of sys log entries deleted."),
    })
    @DeleteMapping("/delete")
    public ResponseEntity<Long> deleteOlderThanDays(@RequestParam int days) {
        LocalDateTime dateTime = SysLogService.getDateTimeDaysAgo(days);
        Long count = sysLogService.countAllOlderThan(dateTime);
        sysLogService.deleteAllOlderThan(dateTime);
        return ResponseEntity.ok(count);
    }

    @Operation(summary = "Delete sys log by id.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Sys log deleted."),
    })
    @DeleteMapping("/{id}")
    public  ResponseEntity<SysLog> deleteById(@PathVariable Long id) {
        SysLog deletedLog = sysLogService.deleteById(id);
        return ResponseEntity.ok(deletedLog);
    }
}