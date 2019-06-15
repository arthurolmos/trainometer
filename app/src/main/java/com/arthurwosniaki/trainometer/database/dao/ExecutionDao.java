package com.arthurwosniaki.trainometer.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.arthurwosniaki.trainometer.entities.Execution;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.arthurwosniaki.trainometer.database.DatabaseConstants.TABLE_EXECUTIONS;


@Dao
public abstract class ExecutionDao implements BaseDao<Execution>{

    @Query("SELECT * FROM " + TABLE_EXECUTIONS + " WHERE id =:id")
    public abstract LiveData<Execution> getExecutionById(long id);

    @Query("SELECT * FROM " + TABLE_EXECUTIONS + " WHERE id_training =:idTraining")
    public abstract LiveData<List<Execution>> getExecutionsByTrainingId(long idTraining);

    @Query("SELECT * FROM " + TABLE_EXECUTIONS + " WHERE id_training =:idTraining AND open =:open")
    public abstract LiveData<Execution> getOpenExecutionByTrainingId(long idTraining, boolean open);

    @Query("SELECT * FROM " + TABLE_EXECUTIONS)
    public abstract LiveData<List<Execution>> getAllExecutions();

    @Query("UPDATE " + TABLE_EXECUTIONS + " SET date_end =:dateEnd WHERE id = :idExecution")
    public abstract int updateDateEnd(LocalDate dateEnd, long idExecution);

    @Query("UPDATE " + TABLE_EXECUTIONS + " SET open =:open WHERE id = :idExecution")
    public abstract int updateOpen(boolean open, long idExecution);

    @Query("UPDATE " + TABLE_EXECUTIONS + " SET date_end =:dateEnd, open = :open, " +
            "last_modification = :modified WHERE id = :idExecution")
    public abstract int updateFinishExecution(LocalDate dateEnd, boolean open, LocalDateTime modified, long idExecution);
}
