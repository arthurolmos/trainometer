package com.arthurwosniaki.trainometer.database.repos;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.arthurwosniaki.trainometer.database.DatabaseCallback;
import com.arthurwosniaki.trainometer.database.TrainometerDatabase;
import com.arthurwosniaki.trainometer.database.dao.TrainingWithExercisesDao;
import com.arthurwosniaki.trainometer.entities.pojos.TrainingWithExercises;

public class TrainingWithExercisesRepository {

    private DatabaseCallback callback;
    private TrainingWithExercisesDao mDao;

    public TrainingWithExercisesRepository(Application application) {
        TrainometerDatabase db = TrainometerDatabase.getInstance(application);

        mDao = db.trainingWithExercises();
    }

    public TrainingWithExercisesRepository(Application application, DatabaseCallback callback) {
        TrainometerDatabase db = TrainometerDatabase.getInstance(application);

        mDao = db.trainingWithExercises();
        this.callback = callback;
    }

    public LiveData<TrainingWithExercises> getTrainingWithExercises(long id){
        return mDao.getTrainingWithExercises(id);
    }


    public void insert(TrainingWithExercises trainingWithExercises){
        new InsertTask(mDao, callback).execute(trainingWithExercises);
    }

    public void update(TrainingWithExercises trainingWithExercises){
        new UpdateTask(mDao, callback).execute(trainingWithExercises);
    }


    private static class InsertTask extends AsyncTask<TrainingWithExercises, Void, Long>{
        private TrainingWithExercisesDao mDao;
        private DatabaseCallback callback;

        InsertTask (TrainingWithExercisesDao trainingWithExercisesDao, DatabaseCallback callback){
            mDao = trainingWithExercisesDao;

            this.callback = callback;
        }

        @Override
        protected Long doInBackground(TrainingWithExercises... trainingWithExercises) {
            return mDao.insertTrainingWithExercises(trainingWithExercises[0]);
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);

            if(aLong >= 0)
                callback.onItemAdded("Treino");
            else
                callback.onDataNotAvailable();
        }
    }

    private static class UpdateTask extends AsyncTask<TrainingWithExercises, Void, Integer>{
        private TrainingWithExercisesDao mDao;
        private DatabaseCallback callback;

        UpdateTask (TrainingWithExercisesDao trainingWithExercisesDao, DatabaseCallback callback){
            mDao = trainingWithExercisesDao;

            this.callback = callback;
        }

        @Override
        protected Integer doInBackground(TrainingWithExercises... trainingWithExercises) {
            return mDao.updateTrainingWithExercises(trainingWithExercises[0]);
        }

        @Override
        protected void onPostExecute(Integer aInt) {
            super.onPostExecute(aInt);

            if(aInt >= 0)
                callback.onItemUpdated("Treino");
            else
                callback.onDataNotAvailable();
        }
    }
}
