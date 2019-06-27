package com.arthurwosniaki.trainometer.database.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.arthurwosniaki.trainometer.database.repos.ExerciseWithSeriesRepository;
import com.arthurwosniaki.trainometer.entities.pojos.ExerciseWithSeries;
import com.arthurwosniaki.trainometer.database.DatabaseCallback;

import java.util.List;

public class ExerciseWithSeriesViewModel extends AndroidViewModel {

    private ExerciseWithSeriesRepository repository;

    public ExerciseWithSeriesViewModel(@NonNull Application application) {
        super(application);

        repository = new ExerciseWithSeriesRepository(application);
    }

    public ExerciseWithSeriesViewModel(@NonNull Application application, DatabaseCallback callback) {
        super(application);

        repository = new ExerciseWithSeriesRepository(application, callback);
    }

    public LiveData<ExerciseWithSeries> getExerciseAndSeriesByIdExercise(long idExercise){
        return repository.getExerciseAndSeriesByIdExercise(idExercise);
    }

    public LiveData<List<ExerciseWithSeries>> getExerciseAndSeriesByIdTraining(long idTraining){
        return repository.getExerciseAndSeriesByIdTraining(idTraining);
    }
}
