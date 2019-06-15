package com.arthurwosniaki.trainometer.database.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.arthurwosniaki.trainometer.database.repos.ExerciseHistoryRepository;
import com.arthurwosniaki.trainometer.entities.pojos.ExerciseHistory;
import com.arthurwosniaki.trainometer.database.DatabaseCallback;
public class ExerciseHistoryViewModel extends AndroidViewModel {

    private ExerciseHistoryRepository mRepository;
    private DatabaseCallback mCallback;

    public ExerciseHistoryViewModel(@NonNull Application application) {
        super(application);

        mRepository = new ExerciseHistoryRepository(application);
    }

    public ExerciseHistoryViewModel(@NonNull Application application, DatabaseCallback callback) {
        super(application);

        mRepository = new ExerciseHistoryRepository(application);
        mCallback = callback;
    }


    public LiveData<ExerciseHistory> getExerciseHistoryByIdExercise(long idExercise){
         return mRepository.getExerciseHistoryByIdExercise(idExercise);
    }


}
