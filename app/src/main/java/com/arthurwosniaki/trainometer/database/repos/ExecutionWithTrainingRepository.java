package com.arthurwosniaki.trainometer.database.repos;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.arthurwosniaki.trainometer.database.TrainometerDatabase;
import com.arthurwosniaki.trainometer.database.dao.ExecutionWithTrainingDao;
import com.arthurwosniaki.trainometer.entities.pojos.ExecutionWithTraining;
import com.arthurwosniaki.trainometer.database.DatabaseCallback;

public class ExecutionWithTrainingRepository {

    private DatabaseCallback callback;
    private ExecutionWithTrainingDao mDao;

    public ExecutionWithTrainingRepository(Application application) {
        TrainometerDatabase db = TrainometerDatabase.getInstance(application);

        mDao = db.executionWithTrainingDao();
    }

    public ExecutionWithTrainingRepository(Application application, DatabaseCallback callback) {
        TrainometerDatabase db = TrainometerDatabase.getInstance(application);

        mDao = db.executionWithTrainingDao();
        this.callback = callback;
    }


    public LiveData<ExecutionWithTraining> getOpenExecutionWithTraining(long idTraining, boolean open){
        return mDao.getOpenExecutionWithTraining(idTraining, open);
    }


}
