package com.arthurwosniaki.trainometer.database.repos;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.arthurwosniaki.trainometer.database.TrainometerDatabase;
import com.arthurwosniaki.trainometer.database.dao.SerieDao;
import com.arthurwosniaki.trainometer.entities.Exercise;
import com.arthurwosniaki.trainometer.entities.Serie;
import com.arthurwosniaki.trainometer.database.DatabaseCallback;

import java.util.List;

public class SerieRepository {

    private SerieDao mDao;
    private DatabaseCallback mCallback;

     public SerieRepository(Application application) {
        TrainometerDatabase db = TrainometerDatabase.getInstance(application);

        mDao = db.serieDao();
    }

    public SerieRepository(Application application, DatabaseCallback callback) {
        TrainometerDatabase db = TrainometerDatabase.getInstance(application);

        mDao = db.serieDao();
        mCallback = callback;
    }


    public LiveData<Serie> getSerieById(long id){
         return mDao.getSerieById(id);
    }

    public LiveData<List<Serie>> getSeriesByExerciseId(long idExercise){
         return mDao.getSeriesByExerciseId(idExercise);
    }

    public LiveData<List<Serie>> getSeriesByExecutionId(long idExecution){
         return mDao.getSeriesByExecutionId(idExecution);
    }

    public LiveData<List<Serie>> getSeriesByExecutionIdAndExerciseId(long idExecution, long idExercise){
         return mDao.getSeriesByExecutionIdAndExerciseId(idExecution, idExercise);
    }

    public LiveData<List<Serie>> getAllSeries(){
         return mDao.getAllSeries();
    }

    public void getSerieCount(long idExecution, Exercise e){
         new QuerySerieCountTaks(mDao, mCallback, idExecution).execute(e);
    }


    public void insert(Serie serie){
         new InsertTask(mDao, mCallback).execute(serie);
    }

    public void update(Serie serie){
        new UpdateTask(mDao, mCallback).execute(serie);
    }

    public void delete(Serie serie){
        new DeleteTask(mDao, mCallback).execute(serie);
    }


    //Tasks
    private static class InsertTask extends AsyncTask<Serie, Void, Long>{

        private SerieDao mDao;
        private DatabaseCallback mCallBack;

        InsertTask (SerieDao serieDao, DatabaseCallback callback){
            mDao = serieDao;
            mCallBack = callback;
        }

        @Override
        protected Long doInBackground(Serie... series) {
            return mDao.insert(series[0]);
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);

            if(aLong >= 0)
                mCallBack.onItemAdded();
            else
                mCallBack.onDataNotAvailable();
        }
    }

    private static class UpdateTask extends AsyncTask<Serie, Void, Integer>{

        private SerieDao mDao;
        private DatabaseCallback mCallBack;

        UpdateTask (SerieDao serieDao, DatabaseCallback callback){
            mDao = serieDao;
            mCallBack = callback;
        }

        @Override
        protected Integer doInBackground(Serie... series) {
            return mDao.update(series[0]);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            if(integer >= 1)
                mCallBack.onItemUpdated();
            else
                mCallBack.onDataNotAvailable();

        }
    }

    private static class DeleteTask extends AsyncTask<Serie, Void, Integer>{

        private SerieDao mDao;
        private DatabaseCallback mCallBack;

        DeleteTask (SerieDao serieDao, DatabaseCallback callback){
            mDao = serieDao;
            mCallBack = callback;
        }

        @Override
        protected Integer doInBackground(Serie... series) {
            return mDao.delete(series[0]);
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

    private static class QuerySerieCountTaks extends AsyncTask<Exercise, Void, Void>{
        private SerieDao mDao;
        private DatabaseCallback mCallBack;

        private long idExecution;
        private long idExercise;

        QuerySerieCountTaks (SerieDao serieDao, DatabaseCallback callback, long idExecution){
            mDao = serieDao;
            mCallBack = callback;

            this.idExecution = idExecution;
        }

        @Override
        protected Void doInBackground(Exercise... exercises) {
            exercises[0].setSerieCount(mDao.getSerieCount(idExecution, exercises[0].getId()));

            return null;
        }
    }
}
