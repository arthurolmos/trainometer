package com.arthurwosniaki.trainometer.database.dao;

import android.arch.persistence.room.Dao;

@Dao
public abstract class TrainingFullDao{
//
//    @Transaction
//    @Query("DELETE FROM " + TABLE_TRAININGS)
//    public abstract void deleteAllTrainingFull();
//
//    @Insert
//    abstract long insertTraining(Training training);
//
//    @Insert
//    abstract void insertExercise(List<Exercise> exercises);
//
//
//    @Transaction
//    public long insertTrainingFull(TrainingFull trainingFull){
//        long id = -1;
//
//        if(trainingFull != null) {
//            LocalDateTime now = LocalDateTime.now();
//
//            Training training = trainingFull.getTraining();
//            training.setCreatedAt(now);
//            id = insertTraining(training);
//
//            if(id != -1){
//                List<Exercise> exercises = trainingFull.getExercisesOrderedBySequence();
//
//                if(exercises.size() > 0){
//                    for(Exercise e : exercises){
//                        e.setIdTraining(id);
//                        e.setCreatedAt(now);
//                    }
//
//                    insertExercise(exercises);
//                }
//            }
//        }
//
//        return id;
//    }
}
