package com.arthurwosniaki.trainometer.database.repos;

import android.app.Application;
import android.os.AsyncTask;

import com.arthurwosniaki.trainometer.database.DatabaseCallback;
import com.arthurwosniaki.trainometer.database.TrainometerDatabase;
import com.arthurwosniaki.trainometer.database.dao.TrainingFullDao;
import com.arthurwosniaki.trainometer.entities.pojos.TrainingFull;

public class TrainingFullRepository {

    private DatabaseCallback callback;
    private TrainingFullDao mDao;

    public TrainingFullRepository(Application application) {
        TrainometerDatabase db = TrainometerDatabase.getInstance(application);

        mDao = db.trainingFullDao();
    }

    public TrainingFullRepository(Application application, DatabaseCallback callback) {
        TrainometerDatabase db = TrainometerDatabase.getInstance(application);

        mDao = db.trainingFullDao();
        this.callback = callback;
    }


    public void insert(TrainingFull trainingFull){
        new InsertTask(mDao, callback).execute(trainingFull);
    }


    private static class InsertTask extends AsyncTask<TrainingFull, Void, Long>{
        private TrainingFullDao mDao;
        private DatabaseCallback callback;

        InsertTask (TrainingFullDao trainingFullDao, DatabaseCallback callback){
            mDao = trainingFullDao;

            this.callback = callback;
        }

        @Override
        protected Long doInBackground(TrainingFull... trainingFulls) {
            return mDao.insertTrainingFull(trainingFulls[0]);
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);

            if(aLong >= 0)
                callback.onItemAdded();
            else
                callback.onDataNotAvailable();
        }
    }
}
