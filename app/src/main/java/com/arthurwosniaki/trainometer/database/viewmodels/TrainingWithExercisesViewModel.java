package com.arthurwosniaki.trainometer.database.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.arthurwosniaki.trainometer.database.DatabaseCallback;
import com.arthurwosniaki.trainometer.database.repos.TrainingWithExercisesRepository;
import com.arthurwosniaki.trainometer.entities.Exercise;
import com.arthurwosniaki.trainometer.entities.pojos.TrainingWithExercises;

import java.time.LocalDateTime;


public class TrainingWithExercisesViewModel extends AndroidViewModel{
    String TAG = TrainingWithExercisesViewModel.class.getSimpleName();

    private TrainingWithExercisesRepository repository;

    public TrainingWithExercisesViewModel(@NonNull Application application) {
        super(application);

        repository = new TrainingWithExercisesRepository(application);
    }

    public TrainingWithExercisesViewModel(@NonNull Application application, DatabaseCallback callback) {
        super(application);

        repository = new TrainingWithExercisesRepository(application, callback);
    }

    public LiveData<TrainingWithExercises> getTrainingWithExercises(long id){
        return repository.getTrainingWithExercises(id);
    }


    public void insert (TrainingWithExercises trainingWithExercises){
        LocalDateTime now = LocalDateTime.now();
        trainingWithExercises.getTraining().setCreatedAt(now);

        if(trainingWithExercises.getExercises().size() > 0){
            for(Exercise e : trainingWithExercises.getExercises()){
                e.setCreatedAt(now);
            }
        }

        repository.insert(trainingWithExercises);
    }

    public void update (TrainingWithExercises trainingWithExercises){
        LocalDateTime now = LocalDateTime.now();
        trainingWithExercises.getTraining().setLastModification(now);

        if(trainingWithExercises.getExercises().size() > 0){
            for(Exercise e : trainingWithExercises.getExercises()){
                e.setLastModification(now);
            }
        }

        repository.update(trainingWithExercises);
    }
}
