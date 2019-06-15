package com.arthurwosniaki.trainometer.database.repos;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.arthurwosniaki.trainometer.database.TrainometerDatabase;
import com.arthurwosniaki.trainometer.database.dao.TrainingDao;
import com.arthurwosniaki.trainometer.entities.Training;
import com.arthurwosniaki.trainometer.database.DatabaseCallback;

import java.util.List;

public class TrainingRepository {

    private TrainingDao mDao;
    private DatabaseCallback mCallback;

     public TrainingRepository(Application application) {
        TrainometerDatabase db = TrainometerDatabase.getInstance(application);

        mDao = db.trainingDao();
    }

    public TrainingRepository(Application application, DatabaseCallback callback) {
        TrainometerDatabase db = TrainometerDatabase.getInstance(application);

        mDao = db.trainingDao();
        mCallback = callback;
    }


    public LiveData<Training> getTrainingById(long id){
        return mDao.getTrainingById(id);
    }

    public LiveData<List<Training>> getAllTrainings() {
        return mDao.getAllTrainings();
    }

    public LiveData<List<Training>> getOpenTrainings(boolean open) {
        return mDao.getOpenTrainings(open);
    }

    public LiveData<Training> getTrainingByName(String name){
        return mDao.getTrainingByName(name);
    }

    public long insert(Training training) {
        return mDao.insert(training);
    }

    public void update(Training training){
        new UpdateTask(mDao, mCallback).execute(training);
    }

    public void delete(Training training) { new DeleteTask(mDao, mCallback).execute(training);}


    //Tasks
    private static class UpdateTask extends AsyncTask<Training, Void, Integer>{

        private TrainingDao mDao;
        private DatabaseCallback mCallBack;

        UpdateTask (TrainingDao trainingDao, DatabaseCallback callback){
            mDao = trainingDao;
            mCallBack = callback;
        }

        @Override
        protected Integer doInBackground(Training... trainings) {
            return mDao.update(trainings[0]);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            if(integer >= 1)
                mCallBack.onItemUpdated();
            else
                mCallBack.onItemDeleted();

        }
    }

    private static class DeleteTask extends AsyncTask<Training, Void, Integer>{

        private TrainingDao mDao;
        private DatabaseCallback mCallBack;

        DeleteTask (TrainingDao trainingDao, DatabaseCallback callback){
            mDao = trainingDao;
            mCallBack = callback;
        }

        @Override
        protected Integer doInBackground(Training... trainings) {
            return mDao.delete(trainings[0]);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            if(integer >= 1)
                mCallBack.onItemDeleted();
            else
                mCallBack.onDataNotAvailable();
        }
    }
}
