package com.arthurwosniaki.trainometer.database.repos;

import android.app.Application;
import android.arch.lifecycle.LiveData;

import com.arthurwosniaki.trainometer.database.TrainometerDatabase;
import com.arthurwosniaki.trainometer.database.dao.TrainingWithExecutionsDao;
import com.arthurwosniaki.trainometer.entities.pojos.TrainingWithExecutions;
import com.arthurwosniaki.trainometer.database.DatabaseCallback;

import java.util.List;

public class TrainingWithExecutionsRepository {

    private DatabaseCallback callback;
    private TrainingWithExecutionsDao mDao;

    public TrainingWithExecutionsRepository(Application application) {
        TrainometerDatabase db = TrainometerDatabase.getInstance(application);

        mDao = db.trainingWithExecutionsDao();
    }

    public TrainingWithExecutionsRepository(Application application, DatabaseCallback callback) {
        TrainometerDatabase db = TrainometerDatabase.getInstance(application);

        mDao = db.trainingWithExecutionsDao();
        this.callback = callback;
    }


    public LiveData<List<TrainingWithExecutions>> getOpenTrainingsWithExecutions(boolean open){
        return mDao.getOpenTrainingsWithExecutions(open);
    }

//    public LiveData<List<TrainingWithExecutions>> getOpenTrainingsWithOpenExecution(boolean open){
//        return mDao.getOpenTrainingsWithOpenExecution(open);
//    }

    public LiveData<TrainingWithExecutions> getTrainingAndExecutionByIdTraining(long idTraining, boolean open){
        return mDao.getTrainingAndExecutionByIdTraining(idTraining, open);
    }

}
