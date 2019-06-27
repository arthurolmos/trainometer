package com.arthurwosniaki.trainometer.database.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.arthurwosniaki.trainometer.database.repos.TrainingWithExecutionsRepository;
import com.arthurwosniaki.trainometer.entities.pojos.TrainingWithExecutions;
import com.arthurwosniaki.trainometer.database.DatabaseCallback;

import java.util.List;


public class TrainingWithExecutionsViewModel extends AndroidViewModel{
    String TAG = TrainingWithExecutionsViewModel.class.getSimpleName();

    private TrainingWithExecutionsRepository repository;



    public TrainingWithExecutionsViewModel(@NonNull Application application) {
        super(application);

        repository = new TrainingWithExecutionsRepository(application);
    }

    public TrainingWithExecutionsViewModel(@NonNull Application application, DatabaseCallback callback) {
        super(application);

        repository = new TrainingWithExecutionsRepository(application, callback);
    }

    public LiveData<List<TrainingWithExecutions>> getOpenTrainingsWithExecutions(boolean open) {
        return repository.getOpenTrainingsWithExecutions(open);
    }
}
