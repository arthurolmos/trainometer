package com.arthurwosniaki.trainometer.entities.pojos;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Relation;

import com.arthurwosniaki.trainometer.entities.Exercise;
import com.arthurwosniaki.trainometer.entities.Serie;

import java.util.List;

public class ExerciseWithSeries implements Comparable<ExerciseWithSeries>{
    @Embedded
    private Exercise exercise;

    @Relation(parentColumn = "id", entityColumn = "id_exercise", entity = Serie.class)
    private List<Serie> series;

    @Ignore
    private long executionId;


    public ExerciseWithSeries() {
    }



    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public List<Serie> getSeries() {
        return series;
    }

    public void setSeries(List<Serie> series) {
        this.series = series;
    }

    public long getExecutionId() {
        return executionId;
    }

    public void setExecutionId(long executionId) {
        this.executionId = executionId;
    }

    @Override
    public int compareTo(ExerciseWithSeries otherExerciseWithSeries) {
        return (this.getExercise().getSequence() - otherExerciseWithSeries.getExercise().getSequence());
    }
}
