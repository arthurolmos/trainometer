package com.arthurwosniaki.trainometer.entities.pojos;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import com.arthurwosniaki.trainometer.entities.Execution;
import com.arthurwosniaki.trainometer.entities.Training;

import java.util.List;

public class ExecutionWithTraining {
    @Embedded
    private Execution execution;

    @Relation(parentColumn = "id_training", entityColumn = "id", entity = Training.class)
    private List<Training> training;

    public ExecutionWithTraining() {
    }

    public Execution getExecution() {
        return execution;
    }

    public void setExecution(Execution execution) {
        this.execution = execution;
    }

    public List<Training> getTraining() {
        return training;
    }

    public void setTraining(List<Training> training) {
        this.training = training;
    }
}
