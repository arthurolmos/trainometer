package com.arthurwosniaki.trainometer.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.arthurwosniaki.trainometer.database.dao.ExecutionDao;
import com.arthurwosniaki.trainometer.database.dao.ExecutionWithTrainingDao;
import com.arthurwosniaki.trainometer.database.dao.ExerciseDao;
import com.arthurwosniaki.trainometer.database.dao.ExerciseHistoryDao;
import com.arthurwosniaki.trainometer.database.dao.ExerciseWithSeriesDao;
import com.arthurwosniaki.trainometer.database.dao.SerieDao;
import com.arthurwosniaki.trainometer.database.dao.TrainingDao;
import com.arthurwosniaki.trainometer.database.dao.TrainingWithExecutionsDao;
import com.arthurwosniaki.trainometer.database.dao.TrainingWithExercisesDao;
import com.arthurwosniaki.trainometer.entities.Execution;
import com.arthurwosniaki.trainometer.entities.Exercise;
import com.arthurwosniaki.trainometer.entities.Serie;
import com.arthurwosniaki.trainometer.entities.Training;
import com.arthurwosniaki.trainometer.entities.pojos.TrainingWithExercises;
import com.arthurwosniaki.trainometer.utils.Converters;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static com.arthurwosniaki.trainometer.database.DatabaseConstants.DATABASE_NAME;
import static com.arthurwosniaki.trainometer.database.DatabaseConstants.DATABASE_VERSION;

@Database(entities = {  Training.class,
                        Exercise.class,
                        Execution.class,
                        Serie.class},
        version = DATABASE_VERSION, exportSchema = false)
@TypeConverters(Converters.class)
public abstract class TrainometerDatabase extends RoomDatabase {

    public abstract TrainingDao trainingDao();
    public abstract ExerciseDao exerciseDao();
    public abstract ExecutionDao executionDao();
    public abstract SerieDao serieDao();

    public abstract ExerciseWithSeriesDao exerciseWithSeriesDao();
    public abstract ExerciseHistoryDao exerciseHistoryDao();
    public abstract TrainingWithExecutionsDao trainingWithExecutionsDao();
    public abstract ExecutionWithTrainingDao executionWithTrainingDao();
    public abstract TrainingWithExercisesDao trainingWithExercises();


    private static volatile TrainometerDatabase INSTANCE;

    public static TrainometerDatabase getInstance(Context context){
        if(INSTANCE == null){
            synchronized (TrainometerDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            TrainometerDatabase.class, DATABASE_NAME)
//                            .fallbackToDestructiveMigration()
//                            .addCallback(sRoomDatabaseCallback)
//                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback(){

                @Override
                public void onOpen (@NonNull SupportSQLiteDatabase db){
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };


    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final TrainingWithExercisesDao mDao;

        PopulateDbAsync(TrainometerDatabase db) {
            mDao = db.trainingWithExercises();
        }

        @Override
        protected Void doInBackground(final Void... params) {

            mDao.deleteAllTrainingWithExercises();

            TrainingWithExercises t = createTrainingA();
            mDao.insertTrainingWithExercises(t);

            t = createTrainingB();
            mDao.insertTrainingWithExercises(t);

            t = createTrainingC();
            mDao.insertTrainingWithExercises(t);

            return null;
        }
    }

    private static TrainingWithExercises createTrainingA(){
        LocalDate date = LocalDate.now();
        LocalDate conclusion = date.plusMonths(6);
        long period = 6;

        Training training = new Training("Treino A", date, period, conclusion, true);
        List<Exercise> exercises = new ArrayList<>();

        Exercise e = new Exercise("Agachamento", 5, "8~10");
        e.setSequence(1);
        exercises.add(e);

        e = new Exercise("Leg Press", 4, "8~10");
        e.setSequence(2);
        exercises.add(e);

        e = new Exercise("Extensora", 3, "8~10");
        e.setSequence(3);
        exercises.add(e);

        e = new Exercise("Stiff", 4, "8~10");
        e.setSequence(4);
        exercises.add(e);

        e = new Exercise("Mesa Flexora", 3, "8~10");
        e.setSequence(5);
        exercises.add(e);

        e = new Exercise("Extensora Lombar", 3, "8~10");
        e.setSequence(6);
        exercises.add(e);

        e = new Exercise("Abdutora", 4, "15~20");
        e.setSequence(7);
        exercises.add(e);

        e = new Exercise("Panturrilha", 4, "8~10");
        e.setSequence(8);
        exercises.add(e);

        TrainingWithExercises t = new TrainingWithExercises();
        t.setTraining(training);
        t.setExercises(exercises);

        return t;
    }

    private static TrainingWithExercises createTrainingB(){
        LocalDate date = LocalDate.now();
        LocalDate conclusion = date.plusMonths(6);
        long period = 6;


        Training training = new Training("Treino B", date, period, conclusion, true);
        List<Exercise> exercises = new ArrayList<>();

        Exercise e = new Exercise("Supino", 5, "8~10");
        e.setSequence(1);
        exercises.add(e);

        e = new Exercise("Supino inclinado halteres", 4, "8~10");
        e.setSequence(2);
        exercises.add(e);

        e = new Exercise("Peck deck fly", 3, "8~10");
        e.setSequence(3);
        exercises.add(e);

        e = new Exercise("Desenvolvimento", 4, "8~10");
        e.setSequence(4);
        exercises.add(e);

        e = new Exercise("Elevação lateral", 3, "8~10");
        e.setSequence(5);
        exercises.add(e);

        e = new Exercise("Mergulho", 4, "8~10");
        e.setSequence(6);
        exercises.add(e);

        e = new Exercise("Tríceps corda", 3, "15~20");
        e.setSequence(7);
        exercises.add(e);

        e = new Exercise("Encolhimento", 4, "15~20");
        e.setSequence(8);
        exercises.add(e);

        TrainingWithExercises t = new TrainingWithExercises();
        t.setTraining(training);
        t.setExercises(exercises);

        return t;
    }

    private static TrainingWithExercises createTrainingC(){
        LocalDate date = LocalDate.now();
        LocalDate conclusion = date.plusMonths(6);
        long period = 6;


        Training training = new Training("Treino C", date, period, conclusion, true);
        List<Exercise> exercises = new ArrayList<>();

        Exercise e = new Exercise("Barra fixa ou Graviton", 5, "8~10");
        e.setSequence(1);
        exercises.add(e);

        e = new Exercise("Remada baixa triangulo", 4, "8~10");
        e.setSequence(2);
        exercises.add(e);

        e = new Exercise("Puxada alta", 3, "8~10");
        e.setSequence(3);
        exercises.add(e);

        e = new Exercise("Rosca alternada", 4, "8~10");
        e.setSequence(4);
        exercises.add(e);

        e = new Exercise("Rosca direta no cabo", 3, "8~10");
        e.setSequence(5);
        exercises.add(e);

        e = new Exercise("Martelo", 3, "8~10");
        e.setSequence(6);
        exercises.add(e);

        e = new Exercise("Abdominal crunch maquina", 3, "15~20");
        e.setSequence(7);
        exercises.add(e);

        e = new Exercise("Abdominal oblíquo maquina", 3, "8~10");
        e.setSequence(8);
        exercises.add(e);

        TrainingWithExercises t = new TrainingWithExercises();
        t.setTraining(training);
        t.setExercises(exercises);

        return t;
    }
}
