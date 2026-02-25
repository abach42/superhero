package com.abach42.superhero.syslog;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SysLogRepository extends JpaRepository<SysLog, Long> {

    List<SysLog> findAllByReadFalseOrderByCreateDateTimeDesc();

    long countByReadFalse();

    void deleteAllByCreateDateTimeBefore(LocalDateTime dateTime);

    Long countAllByCreateDateTimeBefore(LocalDateTime dateTime);

    Page<SysLog> findAllByReadFalse(Pageable pageable);

    Page<SysLog> findByMessageContaining(String query, Pageable pageable);
}
