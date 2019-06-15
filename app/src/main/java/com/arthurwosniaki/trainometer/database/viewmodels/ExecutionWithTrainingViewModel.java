package com.arthurwosniaki.trainometer.database.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.arthurwosniaki.trainometer.database.repos.ExecutionWithTrainingRepository;
import com.arthurwosniaki.trainometer.entities.pojos.ExecutionWithTraining;
import com.arthurwosniaki.trainometer.database.DatabaseCallback;


public class ExecutionWithTrainingViewModel extends AndroidViewModel{
    String TAG = ExecutionWithTrainingViewModel.class.getSimpleName();

    private ExecutionWithTrainingRepository repository;



    public ExecutionWithTrainingViewModel(@NonNull Application application) {
        super(application);

        repository = new ExecutionWithTrainingRepository(application);
    }

    public ExecutionWithTrainingViewModel(@NonNull Application application, DatabaseCallback callback) {
        super(application);

        repository = new ExecutionWithTrainingRepository(application, callback);
    }

    public LiveData<ExecutionWithTraining> getOpenExecutionWithTraining(long idTraining, boolean open){
        return repository.getOpenExecutionWithTraining(idTraining, open);
    }

}
