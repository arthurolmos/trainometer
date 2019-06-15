package com.arthurwosniaki.trainometer.database.repos;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.arthurwosniaki.trainometer.database.TrainometerDatabase;
import com.arthurwosniaki.trainometer.database.dao.ExerciseDao;
import com.arthurwosniaki.trainometer.entities.Exercise;
import com.arthurwosniaki.trainometer.database.DatabaseCallback;

import java.util.List;

public class ExerciseRepository {

    private ExerciseDao mDao;
    private DatabaseCallback mCallback;

     public ExerciseRepository(Application application) {
        TrainometerDatabase db = TrainometerDatabase.getInstance(application);

        mDao = db.exerciseDao();
    }

    public ExerciseRepository(Application application, DatabaseCallback callback) {
        TrainometerDatabase db = TrainometerDatabase.getInstance(application);

        mDao = db.exerciseDao();
        mCallback = callback;
    }

    public LiveData<Exercise> getExerciseById(long id){
        return mDao.getExerciseById(id);
    }

    public LiveData<List<Exercise>> getAllExercises() {
        return mDao.getAllExercises();
    }

    public void insert(Exercise exercise) {
        new InsertTask(mDao, mCallback).execute(exercise);
    }

    public void update(Exercise exercise){
        new UpdateTask(mDao, mCallback).execute(exercise);
    }



    //Tasks
    private static class InsertTask extends AsyncTask<Exercise, Void, Long>{

        private ExerciseDao mDao;
        private DatabaseCallback mCallBack;

        InsertTask (ExerciseDao exerciseDao, DatabaseCallback callback){
            mDao = exerciseDao;
            mCallBack = callback;
        }

        @Override
        protected Long doInBackground(Exercise... exercises) {
            return mDao.insert(exercises[0]);
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


    private static class UpdateTask extends AsyncTask<Exercise, Void, Integer>{

        private ExerciseDao mDao;
        private DatabaseCallback mCallBack;

        UpdateTask (ExerciseDao exerciseDao, DatabaseCallback callback){
            mDao = exerciseDao;
            mCallBack = callback;
        }

        @Override
        protected Integer doInBackground(Exercise... exercises) {
            return mDao.update(exercises[0]);
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
}
