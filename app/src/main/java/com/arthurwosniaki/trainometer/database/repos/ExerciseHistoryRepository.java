package com.arthurwosniaki.trainometer.database.repos;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.arthurwosniaki.trainometer.database.TrainometerDatabase;
import com.arthurwosniaki.trainometer.database.dao.ExerciseHistoryDao;
import com.arthurwosniaki.trainometer.entities.pojos.ExerciseHistory;
import com.arthurwosniaki.trainometer.database.DatabaseCallback;

public class ExerciseHistoryRepository {

    private ExerciseHistoryDao mDao;
    private DatabaseCallback mCallback;

     public ExerciseHistoryRepository(Application application) {
        TrainometerDatabase db = TrainometerDatabase.getInstance(application);

        mDao = db.exerciseHistoryDao();
    }

    public ExerciseHistoryRepository(Application application, DatabaseCallback callback) {
        TrainometerDatabase db = TrainometerDatabase.getInstance(application);

        mDao = db.exerciseHistoryDao();
        mCallback = callback;
    }

    public LiveData<ExerciseHistory> getExerciseHistoryByIdExercise(long idExercise){
         return mDao.getExerciseHistoryByIdExercise(idExercise);
    }


}
