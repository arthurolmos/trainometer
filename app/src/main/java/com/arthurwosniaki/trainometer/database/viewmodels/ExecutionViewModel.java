package com.arthurwosniaki.trainometer.database.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.arthurwosniaki.trainometer.database.repos.ExecutionRepository;
import com.arthurwosniaki.trainometer.entities.Execution;
import com.arthurwosniaki.trainometer.database.DatabaseCallback;

import java.time.LocalDateTime;
import java.util.List;


public class ExecutionViewModel extends AndroidViewModel {
    String TAG = ExecutionViewModel.class.getSimpleName();

    private ExecutionRepository repository;

    public ExecutionViewModel(@NonNull Application application) {
        super(application);

        repository = new ExecutionRepository(application);
    }

    public ExecutionViewModel(@NonNull Application application, DatabaseCallback callback) {
        super(application);

        repository = new ExecutionRepository(application, callback);
    }

    public LiveData<Execution> getOpenExecutionByTrainingId(long idTraining, boolean open) {
        return repository.getOpenExecutionByTrainingId(idTraining, open);
    }

    public LiveData<Execution> getExecutionById(long id){
        return repository.getExecutionById(id);
    }

    public LiveData<List<Execution>> getAllExecutions() {
        return repository.getAllExecutions();
    }

    public void updateDateEnd(Execution execution){
       repository.updateDateEnd(execution);
    }

    public void updateOpen(Execution execution){
        repository.updateOpen(execution);
    }

    public void updateFinishExecution(Execution execution){
        repository.updateFinishExecution(execution);
    }

    public void insert(Execution execution){
        LocalDateTime now = LocalDateTime.now();
        execution.setCreatedAt(now);

        repository.insert(execution);
    }

    public void update(Execution execution){
        LocalDateTime now = LocalDateTime.now();
        execution.setLastModification(now);

        repository.update(execution);
    }

    public void delete(Execution execution){
        repository.delete(execution);
    }
}
