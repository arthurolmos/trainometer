package com.arthurwosniaki.trainometer.database.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.arthurwosniaki.trainometer.database.DatabaseCallback;
import com.arthurwosniaki.trainometer.database.repos.TrainingFullRepository;
import com.arthurwosniaki.trainometer.entities.pojos.TrainingFull;

import java.time.LocalDateTime;


public class TrainingFullViewModel extends AndroidViewModel{
    String TAG = TrainingFullViewModel.class.getSimpleName();

    private TrainingFullRepository repository;

    public TrainingFullViewModel(@NonNull Application application) {
        super(application);

        repository = new TrainingFullRepository(application);
    }

    public TrainingFullViewModel(@NonNull Application application, DatabaseCallback callback) {
        super(application);

        repository = new TrainingFullRepository(application, callback);
    }

    public void insert (TrainingFull trainingFull){
        LocalDateTime now = LocalDateTime.now();
        trainingFull.getTraining().setCreatedAt(now);

        repository.insert(trainingFull);
    }
}
