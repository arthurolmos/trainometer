package com.arthurwosniaki.trainometer.entities.pojos;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import com.arthurwosniaki.trainometer.entities.Exercise;
import com.arthurwosniaki.trainometer.entities.Training;

import java.util.List;

public class TrainingFull {
    @Embedded
    private Training training;

    @Relation(parentColumn = "id", entityColumn = "id_training", entity = Exercise.class)
    private List<Exercise> exercises;

    public TrainingFull() {
    }

    public Training getTraining() {
        return training;
    }

    public void setTraining(Training training) {
        this.training = training;
    }

    public List<Exercise> getExercises() {
        return exercises;
    }

    public void setExercises(List<Exercise> exercises) {
        this.exercises = exercises;
    }
}
