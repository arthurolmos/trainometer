package com.arthurwosniaki.trainometer.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.arthurwosniaki.trainometer.entities.Exercise;
import com.arthurwosniaki.trainometer.entities.pojos.ExerciseWithSeries;

import java.util.List;

@Dao
public abstract class ExerciseWithSeriesDao implements BaseDao<Exercise>{

    @Transaction
    @Query( "SELECT * FROM exercises WHERE id = :idExercise")
    public abstract LiveData<ExerciseWithSeries> getExerciseAndSeriesByIdExercise(long idExercise);

    @Transaction
    @Query( "SELECT * FROM exercises WHERE id_training = :idTraining")
    public abstract LiveData<List<ExerciseWithSeries>> getExerciseAndSeriesByIdTraining(long idTraining);
}
