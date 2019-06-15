package com.arthurwosniaki.trainometer.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.arthurwosniaki.trainometer.entities.Exercise;
import com.arthurwosniaki.trainometer.entities.pojos.ExerciseHistory;

import static com.arthurwosniaki.trainometer.database.DatabaseConstants.TABLE_EXERCISES;

@Dao
public abstract class ExerciseHistoryDao implements BaseDao<Exercise>{

    @Transaction
    @Query("SELECT * FROM " + TABLE_EXERCISES + " WHERE id =:idExercise")
    public abstract LiveData<ExerciseHistory> getExerciseHistoryByIdExercise(long idExercise);
}
