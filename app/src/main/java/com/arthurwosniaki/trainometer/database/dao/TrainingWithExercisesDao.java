package com.arthurwosniaki.trainometer.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import com.arthurwosniaki.trainometer.entities.Exercise;
import com.arthurwosniaki.trainometer.entities.Training;
import com.arthurwosniaki.trainometer.entities.pojos.TrainingWithExercises;

import java.util.List;

import static com.arthurwosniaki.trainometer.database.DatabaseConstants.TABLE_TRAININGS;

@Dao
public abstract class TrainingWithExercisesDao {
    @Transaction
    @Query("SELECT * FROM " + TABLE_TRAININGS + " WHERE id = :id")
    public abstract LiveData<TrainingWithExercises> getTrainingWithExercises(long id);

    @Transaction
    @Query("DELETE FROM " + TABLE_TRAININGS)
    public abstract void deleteAllTrainingWithExercises();

    @Insert
    abstract long insertTraining(Training training);

    @Insert
    abstract void insertExercise(List<Exercise> exercises);

    @Update
    abstract int updateTraining(Training training);

    @Update
    abstract void updateExercises(List<Exercise> exercises);

    @Update
    abstract void updateExercise(Exercise exercise);



    @Transaction
    public long insertTrainingWithExercises(TrainingWithExercises trainingWithExercises){
        long id = -1;

        if(trainingWithExercises != null) {
            Training training = trainingWithExercises.getTraining();
            id = insertTraining(training);

            if(id != -1){
                List<Exercise> exercises = trainingWithExercises.getExercises();

                if(exercises.size() > 0){
                    for(Exercise e : exercises){
                        e.setIdTraining(id);
                    }

                    insertExercise(exercises);
                }
            }
        }

        return id;
    }

    @Transaction
    public int updateTrainingWithExercises(TrainingWithExercises trainingWithExercises){
        int ok = 0;

        if(trainingWithExercises != null) {
            Training training = trainingWithExercises.getTraining();
            ok = updateTraining(training);

            List<Exercise> exercises = trainingWithExercises.getExercises();
            updateExercises(exercises);
        }

        return ok;
    }
}
