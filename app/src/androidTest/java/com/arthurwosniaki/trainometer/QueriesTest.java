package com.arthurwosniaki.trainometer;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.arthurwosniaki.trainometer.database.TrainometerDatabase;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class QueriesTest {
    private static String TAG = QueriesTest.class.getSimpleName();

    private Context context;

    @Before
    public void setup() {
        context = InstrumentationRegistry.getTargetContext();
    }


    @Test
    public void queryTrainingWithExecutionSmall(){
        TrainometerDatabase db = TrainometerDatabase.getInstance(context);

//        List<TrainingWithExecutions> t =
//                db.trainingWithExecutionsDao().getOpenTrainingsWithOpenExecution(true);
//
//        if(t != null){
//            for(TrainingWithExecutions ts : t){
//                System.out.println("Training ID: " + ts.getId());
//                System.out.println("Training Name: " + ts.getName());
//                System.out.println("Training Open: " + ts.isOpen());
//
//                System.out.println("Training ID Execution: " + ts.getIdExecution());
//            }
//        }
    }
}
