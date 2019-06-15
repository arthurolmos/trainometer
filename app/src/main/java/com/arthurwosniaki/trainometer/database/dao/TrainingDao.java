package com.arthurwosniaki.trainometer.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import com.arthurwosniaki.trainometer.entities.Training;

import java.util.List;

import static com.arthurwosniaki.trainometer.database.DatabaseConstants.TABLE_TRAININGS;

@Dao
public abstract class TrainingDao implements BaseDao<Training>{

    @Query("SELECT * FROM " + TABLE_TRAININGS + " WHERE id =:trainingId")
    public abstract LiveData<Training> getTrainingById(long trainingId);

    @Query("SELECT * FROM " + TABLE_TRAININGS + " WHERE name =:name")
    public abstract LiveData<Training> getTrainingByName(String name);

    @Query("SELECT * FROM " + TABLE_TRAININGS + " WHERE open =:open")
    public abstract LiveData<List<Training>> getOpenTrainings(boolean open);

    @Query("SELECT * FROM " + TABLE_TRAININGS)
    public abstract LiveData<List<Training>> getAllTrainings();

    @Query("DELETE FROM " + TABLE_TRAININGS)
    public abstract void deleteAll();
}
