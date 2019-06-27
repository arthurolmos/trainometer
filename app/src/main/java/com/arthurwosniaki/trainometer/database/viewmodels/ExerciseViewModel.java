package com.arthurwosniaki.trainometer.database.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.arthurwosniaki.trainometer.database.repos.ExerciseRepository;
import com.arthurwosniaki.trainometer.entities.Exercise;
import com.arthurwosniaki.trainometer.database.DatabaseCallback;

import java.time.LocalDateTime;
import java.util.List;


public class ExerciseViewModel extends AndroidViewModel {
    String TAG = ExerciseViewModel.class.getSimpleName();

    private ExerciseRepository repository;

    public ExerciseViewModel(@NonNull Application application) {
        super(application);

        repository = new ExerciseRepository(application);
    }

    public ExerciseViewModel(@NonNull Application application, DatabaseCallback callback) {
        super(application);

        repository = new ExerciseRepository(application, callback);
    }

    public LiveData<Exercise> getExerciseById(long id){
        return repository.getExerciseById(id);
    }

    public LiveData<List<Exercise>> getAllExercises() {
        return repository.getAllExercises();
    }

    public void insert(Exercise exercise){
        LocalDateTime now = LocalDateTime.now();
        exercise.setCreatedAt(now);

        repository.insert(exercise);
    }

    public void update(Exercise exercise){
        LocalDateTime now = LocalDateTime.now();
        exercise.setLastModification(now);

        repository.update(exercise);
    }

    public void delete(Exercise exercise){
        repository.delete(exercise);
    }
}
