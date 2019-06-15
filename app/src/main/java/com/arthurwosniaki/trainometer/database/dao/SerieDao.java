package com.arthurwosniaki.trainometer.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.arthurwosniaki.trainometer.entities.Serie;

import java.util.List;

import static com.arthurwosniaki.trainometer.database.DatabaseConstants.TABLE_SERIES;


@Dao
public abstract class SerieDao implements BaseDao<Serie>{

    @Query("SELECT * FROM " + TABLE_SERIES + " WHERE id =:id")
    public abstract LiveData<Serie> getSerieById(long id);

    @Query("SELECT * FROM " + TABLE_SERIES + " WHERE id_exercise =:idExercise")
    public abstract LiveData<List<Serie>> getSeriesByExerciseId(long idExercise);

    @Query("SELECT * FROM " + TABLE_SERIES + " WHERE id_execution =:idExecution")
    public abstract LiveData<List<Serie>> getSeriesByExecutionId(long idExecution);

    @Query("SELECT * FROM " + TABLE_SERIES + " " +
            "WHERE id_execution =:idExecution " +
            "AND id_exercise = :idExercise")
    public abstract LiveData<List<Serie>> getSeriesByExecutionIdAndExerciseId(long idExecution, long idExercise);

    @Query("SELECT * FROM " + TABLE_SERIES)
    public abstract LiveData<List<Serie>> getAllSeries();

    @Query("SELECT COUNT(*) FROM series WHERE  id_execution = :idExecution AND id_exercise = :idExercise")
    public abstract int getSerieCount(long idExecution, long idExercise);
}
