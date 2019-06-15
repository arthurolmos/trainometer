package com.arthurwosniaki.trainometer.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import com.arthurwosniaki.trainometer.entities.Exercise;
import com.arthurwosniaki.trainometer.entities.Training;
import com.arthurwosniaki.trainometer.entities.pojos.TrainingFull;

import java.util.List;

import static com.arthurwosniaki.trainometer.database.DatabaseConstants.TABLE_TRAININGS;

@Dao
public abstract class TrainingFullDao{

    @Transaction
    @Query("DELETE FROM " + TABLE_TRAININGS)
    public abstract void deleteAllTrainingFull();


    @Insert
    abstract long insertTraining(Training training);

    @Insert
    abstract void insertExercise(List<Exercise> exercises);


    @Transaction
    public long insertTrainingFull(TrainingFull trainingFull){
        long id = -1;

        if(trainingFull != null) {
            Training training = trainingFull.getTraining();
            id = insertTraining(training);

            if(id != -1){
                List<Exercise> exercises = trainingFull.getExercises();

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
}
