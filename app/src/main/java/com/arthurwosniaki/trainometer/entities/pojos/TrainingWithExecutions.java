package com.arthurwosniaki.trainometer.entities.pojos;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Relation;

import com.arthurwosniaki.trainometer.entities.Execution;
import com.arthurwosniaki.trainometer.entities.Training;

import java.util.List;

public class TrainingWithExecutions {
    @Embedded
    private Training training;

    @Relation(parentColumn = "id", entityColumn = "id_training", entity = Execution.class)
    private List<Execution> executions;

    @Ignore
    private Execution openExecution;

    public TrainingWithExecutions(){
    }

    public Training getTraining() {
        return training;
    }

    public void setTraining(Training training) {
        this.training = training;
    }

    public List<Execution> getExecutions() {
        return executions;
    }

    public void setExecutions(List<Execution> executions) {
        this.executions = executions;
    }

    public Execution getOpenExecution() {
        return openExecution;
    }

    public void setOpenExecution(Execution openExecution) {
        this.openExecution = openExecution;
    }
}
