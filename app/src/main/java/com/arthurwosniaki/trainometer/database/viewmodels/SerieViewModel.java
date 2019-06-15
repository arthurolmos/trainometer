package com.arthurwosniaki.trainometer.database.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.arthurwosniaki.trainometer.database.repos.SerieRepository;
import com.arthurwosniaki.trainometer.entities.Exercise;
import com.arthurwosniaki.trainometer.entities.Serie;
import com.arthurwosniaki.trainometer.database.DatabaseCallback;

import java.time.LocalDateTime;
import java.util.List;

public class SerieViewModel extends AndroidViewModel{

    private SerieRepository repository;

    public SerieViewModel(@NonNull Application application) {
        super(application);

        repository = new SerieRepository(application);
    }

    public SerieViewModel(@NonNull Application application, DatabaseCallback callback) {
        super(application);

        repository = new SerieRepository(application, callback);
    }


    public LiveData<Serie> getSerieById(long id){
         return repository.getSerieById(id);
    }

    public LiveData<List<Serie>> getSeriesByExerciseId(long idExercise){
         return repository.getSeriesByExerciseId(idExercise);
    }

    public LiveData<List<Serie>> getSeriesByExecutionId(long idExecution){
         return repository.getSeriesByExecutionId(idExecution);
    }

    public LiveData<List<Serie>> getSeriesByExecutionIdAndExerciseId(long idExecution, long idExercise){
         return repository.getSeriesByExecutionIdAndExerciseId(idExecution, idExercise);
    }

    public LiveData<List<Serie>> getAllSeries(){
         return repository.getAllSeries();
    }

    public void getSerieCount(long idExecution, Exercise e){
        repository.getSerieCount(idExecution, e);
    }

    public void insert(Serie serie){
        LocalDateTime now = LocalDateTime.now();
        serie.setCreatedAt(now);

         repository.insert(serie);
    }

    public void update(Serie serie){
        LocalDateTime now = LocalDateTime.now();
        serie.setLastModification(now);

        repository.update(serie);
    }

    public void delete(Serie serie){
        repository.delete(serie);
    }

}
