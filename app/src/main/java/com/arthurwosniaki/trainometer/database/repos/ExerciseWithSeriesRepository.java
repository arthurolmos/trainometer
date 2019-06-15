package com.arthurwosniaki.trainometer.database.repos;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.arthurwosniaki.trainometer.database.TrainometerDatabase;
import com.arthurwosniaki.trainometer.database.dao.ExerciseWithSeriesDao;
import com.arthurwosniaki.trainometer.entities.pojos.ExerciseWithSeries;
import com.arthurwosniaki.trainometer.database.DatabaseCallback;

import java.util.List;

public class ExerciseWithSeriesRepository {

    private ExerciseWithSeriesDao mDao;
    private DatabaseCallback mCallback;

    public ExerciseWithSeriesRepository(Application application) {
        TrainometerDatabase db = TrainometerDatabase.getInstance(application);

        mDao = db.exerciseWithSeriesDao();
    }

    public ExerciseWithSeriesRepository(Application application, DatabaseCallback callback) {
        TrainometerDatabase db = TrainometerDatabase.getInstance(application);

        mDao = db.exerciseWithSeriesDao();
        mCallback = callback;
    }

    public LiveData<ExerciseWithSeries> getExerciseAndSeriesByIdExercise(long idExercise){
        return mDao.getExerciseAndSeriesByIdExercise(idExercise);
    }

    public LiveData<List<ExerciseWithSeries>> getExerciseAndSeriesByIdTraining(long idTraining){
        return mDao.getExerciseAndSeriesByIdTraining(idTraining);
    }
}
