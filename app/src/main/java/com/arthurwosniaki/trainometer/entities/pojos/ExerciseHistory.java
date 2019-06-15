package com.arthurwosniaki.trainometer.entities.pojos;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Relation;

import com.arthurwosniaki.trainometer.entities.Execution;
import com.arthurwosniaki.trainometer.entities.Exercise;
import com.arthurwosniaki.trainometer.entities.Serie;

import java.util.ArrayList;
import java.util.List;

public class ExerciseHistory {
    @Embedded
    private Exercise exercise;

    @Relation(parentColumn = "id", entityColumn = "id_exercise", entity = Serie.class)
    private List<Serie> series;

    @Relation(parentColumn = "id_training", entityColumn = "id_training", entity = Execution.class)
    private List<Execution> executions;

    @Ignore
    private Execution execution;

    @Ignore
    private String date;

    @Ignore
    private List<String> reps;

    @Ignore
    private List<String> weight;

    @Ignore
    private List<String> comments;

    public ExerciseHistory() {
        reps = new ArrayList<>();
        weight = new ArrayList<>();
        comments = new ArrayList<>();
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

    public List<Execution> getExecutions() {
        return executions;
    }

    public void setExecutions(List<Execution> executions) {
        this.executions = executions;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getReps() {
        return reps;
    }

    public void setReps(List<String> reps) {
        this.reps = reps;
    }

    public List<String> getWeight() {
        return weight;
    }

    public void setWeight(List<String> weight) {
        this.weight = weight;
    }

    public Execution getExecution() {
        return execution;
    }

    public void setExecution(Execution execution) {
        this.execution = execution;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }
}
