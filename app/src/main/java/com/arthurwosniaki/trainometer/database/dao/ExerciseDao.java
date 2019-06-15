package com.arthurwosniaki.trainometer.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.arthurwosniaki.trainometer.entities.Exercise;

import java.util.List;

import static com.arthurwosniaki.trainometer.database.DatabaseConstants.TABLE_EXERCISES;

@Dao
public abstract class ExerciseDao implements BaseDao<Exercise>{

    @Query("SELECT * FROM " + TABLE_EXERCISES + " WHERE id =:id")
    public abstract LiveData<Exercise> getExerciseById(long id);

    @Query("SELECT * FROM " + TABLE_EXERCISES + " WHERE id_training =:idTraining")
    public abstract LiveData<List<Exercise>> getExercisesByTrainingId(long idTraining);

    @Query("SELECT * FROM " + TABLE_EXERCISES)
    public abstract LiveData<List<Exercise>> getAllExercises();
}
