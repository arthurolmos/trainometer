package com.arthurwosniaki.trainometer.database.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.arthurwosniaki.trainometer.database.repos.TrainingRepository;
import com.arthurwosniaki.trainometer.entities.Training;
import com.arthurwosniaki.trainometer.database.DatabaseCallback;

import java.time.LocalDateTime;
import java.util.List;


public class TrainingViewModel extends AndroidViewModel {
    String TAG = TrainingViewModel.class.getSimpleName();

    private TrainingRepository repository;

    public TrainingViewModel (@NonNull Application application) {
        super(application);

        repository = new TrainingRepository(application);
    }

    public TrainingViewModel (@NonNull Application application, DatabaseCallback callback) {
        super(application);

        repository = new TrainingRepository(application, callback);
    }

    public LiveData<Training> getTrainingByName(String name){
        return repository.getTrainingByName(name);
    }

    public LiveData<Training> getTrainingById(long id){
        return repository.getTrainingById(id);
    }

    public LiveData<List<Training>> getAllTrainings() {
        return repository.getAllTrainings();
    }

    public LiveData<List<Training>> getOpenTrainings(boolean open) {
        return repository.getOpenTrainings(open);
    }

    public void insert(Training training){
        LocalDateTime now = LocalDateTime.now();
        training.setCreatedAt(now);

        repository.update(training);
    }

    public void update(Training training){
        LocalDateTime now = LocalDateTime.now();
        training.setLastModification(now);

        repository.update(training);
    }

    public void delete(Training training){
        repository.delete(training);
    }
}
