package com.arthurwosniaki.trainometer.entities;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

import java.time.LocalDateTime;

import static com.arthurwosniaki.trainometer.database.DatabaseConstants.TABLE_EXERCISES;

@Entity(tableName = TABLE_EXERCISES,
        indices = @Index(value = "id_training"),
        foreignKeys = @ForeignKey(
                entity = Training.class,
                parentColumns = "id",
                childColumns = "id_training",
                onDelete = ForeignKey.CASCADE))
public class Exercise {
    @PrimaryKey(autoGenerate = true)
    private long id;

    @ColumnInfo(name = "id_training")
    private long idTraining;

    private String name;

    private int sequence;

    private String weight;

    @ColumnInfo(name = "serie_total")
    private int serieTotal;

    @ColumnInfo(name = "serie_count")
    private int serieCount;

    @ColumnInfo(name = "reps_total")
    private String repsTotal;

    private String interval;

    @ColumnInfo(name = "created_at")
    private LocalDateTime createdAt;

    @ColumnInfo(name = "last_modification")
    private LocalDateTime lastModification;


    /* ============================== CONSTRUCTOR =================================== */
    public Exercise() {
    }

    @Ignore
    public Exercise(String name, int serieTotal, String repsTotal) {
        this.name = name;
        this.serieTotal = serieTotal;
        this.repsTotal = repsTotal;
    }

    /* ============================ GETTER & SETTER ================================= */

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

    public long getIdTraining() {
        return idTraining;
    }

    public void setIdTraining(long idTraining) {
        this.idTraining = idTraining;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getRepsTotal() {
        return repsTotal;
    }

    public void setRepsTotal(String repsTotal) {
        this.repsTotal = repsTotal;
    }

    public String getInterval() {
        return interval;
    }

    public void setInterval(String interval) {
        this.interval = interval;
    }

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int order) {
        this.sequence = order;
    }

    public int getSerieTotal() {
        return serieTotal;
    }

    public void setSerieTotal(int serieTotal) {
        this.serieTotal = serieTotal;
    }

    public int getSerieCount() {
        return serieCount;
    }

    public void setSerieCount(int serieCount) {
        this.serieCount = serieCount;
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
