package com.arthurwosniaki.trainometer.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.time.LocalDateTime;

import static com.arthurwosniaki.trainometer.database.DatabaseConstants.TABLE_SERIES;


@Entity(tableName = TABLE_SERIES,
        indices = {
            @Index(value = "id_exercise"),
            @Index(value = "id_execution")
        },
        foreignKeys = {
            @ForeignKey(
                    entity = Exercise.class,
                    parentColumns = "id",
                    childColumns = "id_exercise",
                    onDelete = ForeignKey.CASCADE),

            @ForeignKey(
                    entity = Execution.class,
                    parentColumns = "id",
                    childColumns = "id_execution",
                    onDelete = ForeignKey.CASCADE)
        })
public class Serie {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "id_execution")
    private long idExecution;

    @ColumnInfo(name = "id_exercise")
    private long idExercise;

    private int reps;

    @ColumnInfo(name = "reps_total")
    private int repsTotal;

    private float weight;

    private int sequence;

    private String comment;

    @ColumnInfo(name = "created_at")
    private LocalDateTime createdAt;

    @ColumnInfo(name = "last_modification")
    private LocalDateTime lastModification;

    /* ============================== CONSTRUCTOR =================================== */
    public Serie() {
    }

    @Ignore
    public Serie(long idExecution, long idExercise, int reps, float weight, String comment) {
        this.idExecution = idExecution;
        this.idExercise = idExercise;
        this.reps = reps;
        this.weight = weight;
        this.comment = comment;
    }

    /* ============================ GETTER & SETTER ================================= */
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getIdExecution() {
        return idExecution;
    }

    public void setIdExecution(long idExecution) {
        this.idExecution = idExecution;
    }

    public long getIdExercise() {
        return idExercise;
    }

    public void setIdExercise(long idExercise) {
        this.idExercise = idExercise;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public int getRepsTotal() {
        return repsTotal;
    }

    public void setRepsTotal(int repsTotal) {
        this.repsTotal = repsTotal;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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
