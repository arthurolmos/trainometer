package com.arthurwosniaki.trainometer.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.arthurwosniaki.trainometer.entities.pojos.ExecutionWithTraining;

import static com.arthurwosniaki.trainometer.database.DatabaseConstants.TABLE_EXECUTIONS;

@Dao
public abstract class ExecutionWithTrainingDao {

    @Transaction
    @Query("SELECT * FROM " + TABLE_EXECUTIONS + " WHERE id_training = :idTraining AND open = :open")
    public abstract LiveData<ExecutionWithTraining> getOpenExecutionWithTraining(long idTraining, boolean open);
}
