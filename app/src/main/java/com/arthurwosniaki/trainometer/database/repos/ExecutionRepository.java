package com.arthurwosniaki.trainometer.database.repos;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;

import com.arthurwosniaki.trainometer.database.TrainometerDatabase;
import com.arthurwosniaki.trainometer.database.dao.ExecutionDao;
import com.arthurwosniaki.trainometer.entities.Execution;
import com.arthurwosniaki.trainometer.database.DatabaseCallback;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class ExecutionRepository {

    private ExecutionDao mDao;
    private DatabaseCallback mCallback;

     public ExecutionRepository(Application application) {
        TrainometerDatabase db = TrainometerDatabase.getInstance(application);

        mDao = db.executionDao();
    }

    public ExecutionRepository(Application application, DatabaseCallback callback) {
        TrainometerDatabase db = TrainometerDatabase.getInstance(application);

        mDao = db.executionDao();
        mCallback = callback;
    }

    public LiveData<Execution> getOpenExecutionByTrainingId(long idTraining, boolean open) {
         return mDao.getOpenExecutionByTrainingId(idTraining, open);
    }

    public LiveData<Execution> getExecutionById(long id){
        return mDao.getExecutionById(id);
    }

    public LiveData<List<Execution>> getAllExecutions() {
        return mDao.getAllExecutions();
    }

    public void updateDateEnd(Execution execution){
        new UpdateDateTask(mDao, mCallback).execute(execution);
    }

    public void updateOpen(Execution execution){
        new UpdateOpenTask(mDao, mCallback).execute(execution);
    }

    public void updateFinishExecution(Execution execution){
        new UpdateFinishTask(mDao, mCallback).execute(execution);
    }

    public void insert(Execution execution) {
        new InsertTask(mDao, mCallback).execute(execution);
    }

    public void update(Execution execution){
        new UpdateTask(mDao, mCallback).execute(execution);
    }

    public void delete(Execution execution){
        new DeleteTask(mDao, mCallback).execute(execution);
    }


    //Tasks
    private static class InsertTask extends AsyncTask<Execution, Void, Long>{

        private ExecutionDao mDao;
        private DatabaseCallback mCallBack;

        InsertTask (ExecutionDao executionDao, DatabaseCallback callback){
            mDao = executionDao;
            mCallBack = callback;
        }

        @Override
        protected Long doInBackground(Execution... executions) {
            return mDao.insert(executions[0]);
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

    private static class UpdateTask extends AsyncTask<Execution, Void, Integer>{

        private ExecutionDao mDao;
        private DatabaseCallback mCallBack;

        UpdateTask (ExecutionDao executionDao, DatabaseCallback callback){
            mDao = executionDao;
            mCallBack = callback;
        }

        @Override
        protected Integer doInBackground(Execution... executions) {
            return mDao.update(executions[0]);
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

    private static class DeleteTask extends AsyncTask<Execution, Void, Integer>{

        private ExecutionDao mDao;
        private DatabaseCallback mCallBack;

        DeleteTask (ExecutionDao executionDao, DatabaseCallback callback){
            mDao = executionDao;
            mCallBack = callback;
        }

        @Override
        protected Integer doInBackground(Execution... executions) {
            return mDao.delete(executions[0]);
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

    private static class UpdateDateTask extends AsyncTask<Execution, Void, Integer>{

        private ExecutionDao mDao;
        private DatabaseCallback mCallBack;

        UpdateDateTask (ExecutionDao executionDao, DatabaseCallback callback){
            mDao = executionDao;
            mCallBack = callback;
        }

        @Override
        protected Integer doInBackground(Execution... executions) {
            long id = executions[0].getId();
            LocalDate date = executions[0].getDateEnd();

            return mDao.updateDateEnd(date, id);
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

    private static class UpdateOpenTask extends AsyncTask<Execution, Void, Integer>{

        private ExecutionDao mDao;
        private DatabaseCallback mCallBack;

        UpdateOpenTask (ExecutionDao executionDao, DatabaseCallback callback){
            mDao = executionDao;
            mCallBack = callback;
        }

        @Override
        protected Integer doInBackground(Execution... executions) {
            long id = executions[0].getId();
            boolean open = executions[0].isOpen();

            return mDao.updateOpen(open, id);
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

    private static class UpdateFinishTask extends AsyncTask<Execution, Void, Integer>{

        private ExecutionDao mDao;
        private DatabaseCallback mCallBack;

        UpdateFinishTask (ExecutionDao executionDao, DatabaseCallback callback){
            mDao = executionDao;
            mCallBack = callback;
        }

        @Override
        protected Integer doInBackground(Execution... executions) {
            long id = executions[0].getId();
            boolean open = executions[0].isOpen();
            LocalDate end = executions[0].getDateEnd();
            LocalDateTime modified = executions[0].getLastModification();

            return mDao.updateFinishExecution(end, open, modified, id);
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

}
