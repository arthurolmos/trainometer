package com.arthurwosniaki.trainometer.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.arthurwosniaki.trainometer.entities.pojos.TrainingWithExecutions;

import java.util.List;

import static com.arthurwosniaki.trainometer.database.DatabaseConstants.TABLE_TRAININGS;

@Dao
public abstract class TrainingWithExecutionsDao{

//    @Transaction
//    @Query( "SELECT " +
//            "   T.id, T.name, T.open, " +
//            "   E.id AS id_execution, E.date_start"+
//            " FROM " + TABLE_TRAININGS + " AS T " +
//            " LEFT JOIN " + TABLE_EXECUTIONS + " AS E " +
//            " ON T.id = E.id_training " +
//            " WHERE T.id = :idTraining AND (E.open = :open OR E.id ISNULL)")
//    public abstract LiveData<TrainingWithExecutions> getTrainingAndExecutionByIdTraining(long idTraining, boolean open);

    @Transaction
    @Query("SELECT * FROM " + TABLE_TRAININGS + " WHERE open = :open")
    public abstract LiveData<List<TrainingWithExecutions>> getOpenTrainingsWithExecutions(boolean open);
}
