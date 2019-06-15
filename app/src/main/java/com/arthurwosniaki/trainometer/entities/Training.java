package com.arthurwosniaki.trainometer.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.arthurwosniaki.trainometer.database.DatabaseConstants.TABLE_TRAININGS;


@Entity(tableName = TABLE_TRAININGS)
public class Training {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String name;

    private String instructor;

    @ColumnInfo(name = "date_start")
    private LocalDate dateStart;

    private long period;

    @ColumnInfo(name = "date_conclusion")
    private LocalDate dateConclusion;

    private boolean open;

    @ColumnInfo(name = "created_at")
    private LocalDateTime createdAt;

    @ColumnInfo(name = "last_modification")
    private LocalDateTime lastModification;


    /* ======================== CONSTRUCTORS ============================= */
    public Training() {
    }

    @Ignore
    public Training(String name, LocalDate dateStart, Long period, LocalDate dateConclusion, boolean open) {
        this.name = name;
        this.dateStart = dateStart;
        this.period = period;
        this.dateConclusion = dateConclusion;
        this.open = open;
    }

    /* ======================== GETTERS & SETTERS ========================== */

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getDateStart() {
        return dateStart;
    }

    public void setDateStart(LocalDate dateStart) {
        this.dateStart = dateStart;
    }

    public LocalDate getDateConclusion() {
        return dateConclusion;
    }

    public void setDateConclusion(LocalDate dateConclusion) {
        this.dateConclusion = dateConclusion;
    }

    public long getPeriod() {
        return period;
    }

    public void setPeriod(long period) {
        this.period = period;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
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
