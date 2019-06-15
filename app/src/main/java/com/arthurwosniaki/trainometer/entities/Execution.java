package com.arthurwosniaki.trainometer.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.arthurwosniaki.trainometer.database.DatabaseConstants.TABLE_EXECUTIONS;

@Entity(tableName = TABLE_EXECUTIONS,
        indices = @Index(value = "id_training"),
        foreignKeys = @ForeignKey(
                entity = Training.class,
                parentColumns = "id",
                childColumns = "id_training",
                onDelete = ForeignKey.CASCADE))
public class Execution {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "id_training")
    private long idTraining;

    @ColumnInfo(name = "date_start")
    private LocalDate dateStart;

    @ColumnInfo(name = "date_end")
    private LocalDate dateEnd;

    private boolean open;

    @ColumnInfo(name = "created_at")
    private LocalDateTime createdAt;

    @ColumnInfo(name = "last_modification")
    private LocalDateTime lastModification;

    /* ================== CONSTRUCTOR ========================== */
    public Execution() {
    }

    @Ignore
    public Execution(long idTraining, LocalDate dateStart, boolean open) {
        this.idTraining = idTraining;
        this.dateStart = dateStart;
        this.open = open;
    }

    /* ================== GETTERS & SETTERS ======================= */
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIdTraining() {
        return idTraining;
    }

    public void setIdTraining(long idTraining) {
        this.idTraining = idTraining;
    }

    public LocalDate getDateStart() {
        return dateStart;
    }

    public void setDateStart(LocalDate dateStart) {
        this.dateStart = dateStart;
    }

    public LocalDate getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(LocalDate dateEnd) {
        this.dateEnd = dateEnd;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastModification() {
        return lastModification;
    }

    public void setLastModification(LocalDateTime lastModification) {
        this.lastModification = lastModification;
    }
}
