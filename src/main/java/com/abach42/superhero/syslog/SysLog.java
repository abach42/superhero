package com.abach42.superhero.syslog;

import com.abach42.superhero.shared.convertion.EncryptedStringListConverter;
import com.abach42.superhero.shared.convertion.EncryptionConverter;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "actor", discriminatorType = DiscriminatorType.INTEGER)
public abstract class SysLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @NotNull
    @Enumerated(EnumType.ORDINAL)

    protected Level level = Level.STANDARD;

    @Size(max = 250)
    @Column(length = 250)
    @Convert(converter = EncryptionConverter.class)
    protected String title;

    @Convert(converter = EncryptedStringListConverter.class)
    protected List<String> message;

    protected boolean read;

    @CreationTimestamp
    @Column(updatable = false)
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createDateTime;

    protected SysLog() {
    }

    public Long getId() {
        return id;
    }

    public Level getLevel() {
        return level;
    }

    public void setLevel(Level level) {
        this.level = level;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getMessage() {
        return message;
    }

    public void setMessage(List<String> message) {
        this.message = message;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public LocalDateTime getCreateDateTime() {
        return createDateTime;
    }

    public abstract String getActor();
}
