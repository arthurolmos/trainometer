package com.arthurwosniaki.trainometer.stopwatch.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.arthurwosniaki.trainometer.R;
import com.arthurwosniaki.trainometer.stopwatch.services.ChronometerService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.arthurwosniaki.trainometer.stopwatch.services.ServiceConstants.CHRONOMETER_FRAGMENT_BROADCAST;
import static com.arthurwosniaki.trainometer.stopwatch.services.ServiceConstants.CHRONOMETER_UPDATE;
import static com.arthurwosniaki.trainometer.stopwatch.services.ServiceConstants.RESTART_CHRONOMETER_SERVICE;
import static com.arthurwosniaki.trainometer.stopwatch.services.ServiceConstants.UPDATE_CHRONOMETER_UI;
import static com.arthurwosniaki.trainometer.stopwatch.services.SingletonServiceManager.isChronometerPaused;
import static com.arthurwosniaki.trainometer.stopwatch.services.SingletonServiceManager.isChronometerRunningBackground;
import static com.arthurwosniaki.trainometer.stopwatch.services.SingletonServiceManager.isChronometerServiceRunning;
import static java.util.Objects.requireNonNull;

public class ChronometerFragment extends Fragment {

    @OnClick(R.id.btnStartChronometer) void startChronometer() {
        if(!isChronometerServiceRunning){
            time = 0;
        }

        isChronometerServiceRunning = true;
        isChronometerPaused = false;

        startService(getActivity(), createBundle());
        Log.v(TAG, "Service is running after btnStartAction?: " + isChronometerServiceRunning);
    }

    @OnClick(R.id.btnPauseChronometer) void pauseChronometer() {
        isChronometerPaused = true;

        startService(getActivity(), createBundle());
        Log.v(TAG, "Service is running after PauseAction?: " + isChronometerServiceRunning);
    }

    @OnClick(R.id.btnStopChronometer) void stopChronometer() {
        isChronometerServiceRunning = false;
        requireNonNull(getActivity()).stopService(new Intent(getActivity(), ChronometerService.class));

        Log.v(TAG, "Service is running after StopAction?: " + isChronometerServiceRunning);

        setVisibleButtons();
    }

    @BindView(R.id.tvChronometer) TextView chronometer;
    @BindView(R.id.btnStartChronometer) ImageButton btnStart;
    @BindView(R.id.btnPauseChronometer) ImageButton btnPause;
    @BindView(R.id.btnStopChronometer) ImageButton btnStop;

    private String TAG = ChronometerFragment.class.getSimpleName();

    private ChronometerFragmentReceiver receiver;

    private long time;

    private long idTraining;
    private long idExecution;
    private long idExercise;
    private int position;


    public ChronometerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_chronometer, container, false);
        ButterKnife.bind(this, v);

        registerBroadcastReceiver();

        //Get data from Activity
        Bundle activityExtras = getArguments();
        if(activityExtras != null) {
            idTraining = activityExtras.getLong("id_training", 0);
            idExecution = activityExtras.getLong("id_execution", 0);
            idExercise = activityExtras.getLong("id_exercise", 0);
            position = activityExtras.getInt("position", 0);
        }

        /*=== SET VIEWS ACTION ===*/
        setVisibleButtons();
        setChronometer();

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        isChronometerRunningBackground = false;

        //If Service is timerRunning, stops Service. If timer is timerRunning, it will be recreated.
        if (isChronometerServiceRunning) {
            Log.v(TAG, "Service was running when OnStart was called.");

            if(getActivity() != null){
                getActivity().stopService(new Intent(getActivity(), ChronometerService.class));
                Log.v(TAG, "Service still running onStart? " + isChronometerServiceRunning);
            }
        }
    }

    private Bundle createBundle(){
        Bundle extras = new Bundle();

        extras.putLong("id_training", idTraining);
        extras.putLong("id_execution", idExecution);
        extras.putLong("id_exercise", idExercise);

        extras.putInt("fragment_position", 0);
        extras.putInt("position", position);

        extras.putLong("time", time);

        return extras;
    }

    //--- SET VIEWS ACTIONS ---//
    private void setChronometer(){
        if(!isChronometerServiceRunning)
            chronometer.setText("00:00:00");
    }

    //--- SET VIEWS VISIBILITY ---//
    //Buttons Visibility
    private void setVisibleButtons() {
        if(isChronometerServiceRunning){
            if(isChronometerPaused){
                setButtonVisibility(View.VISIBLE, View.GONE, View.VISIBLE);
            }else{
                setButtonVisibility(View.GONE, View.VISIBLE, View.VISIBLE);
            }
        }else{
            setButtonVisibility(View.VISIBLE, View.GONE, View.GONE);
        }
    }

    public void setButtonVisibility(int start, int pause, int stop){
        btnStart.setVisibility(start);
        btnPause.setVisibility(pause);
        btnStop.setVisibility(stop);
    }

    //===== FINISHING ACTIVITY =====//
    @Override
    public void onPause() {
        isChronometerRunningBackground = true;

        requireNonNull(getActivity()).stopService(new Intent(getActivity(), ChronometerService.class));
        Log.v(TAG, "Service is running after onPause?: " + isChronometerServiceRunning);

        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        LocalBroadcastManager.getInstance(requireNonNull(getActivity())).unregisterReceiver(
                receiver);
    }


    //=== SERVICES ===//
    private void startService(Context context, Bundle extras){
        if(extras!= null) {
            Intent serviceIntent = new Intent(getActivity(), ChronometerService.class);
            serviceIntent.putExtras(extras);

            requireNonNull(context).startService(serviceIntent);
        }
    }

    //=== BROADCAST RECEIVER ===/
    public class ChronometerFragmentReceiver extends BroadcastReceiver {
        public ChronometerFragmentReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v(TAG, "Message received from Chronometer Fragment Broadcast!");

            if(intent != null){
                String action = intent.getAction();
                Bundle extras = intent.getExtras();

                //Updates tvChronometer
                if(action != null){
                    switch(action){
                        case CHRONOMETER_UPDATE:
                            if (extras != null) {
                                time = extras.getLong("time", time);

                                String s = extras.getString("elapsed_time");
                                chronometer.setText(s);
                            }
                            break;

                        case RESTART_CHRONOMETER_SERVICE:
                            Log.v(TAG, "Destroying service for restart!");

                            if(isChronometerServiceRunning) {
                                startService(context, extras);

                                Log.v(TAG, "Service is running after Restart Broadcast?: " + isChronometerServiceRunning);
                            }
                            break;

                        case UPDATE_CHRONOMETER_UI:
                            Log.v(TAG, "Update UI!");

                            setVisibleButtons();
                            break;
                    }
                }
            }
        }
    }

    private void registerBroadcastReceiver() {
        receiver = new ChronometerFragmentReceiver();
        IntentFilter statusIntentFilter = new IntentFilter(CHRONOMETER_FRAGMENT_BROADCAST);
        statusIntentFilter.addAction(UPDATE_CHRONOMETER_UI);
        statusIntentFilter.addAction(CHRONOMETER_UPDATE);
        statusIntentFilter.addAction(RESTART_CHRONOMETER_SERVICE);

        // Registers the DownloadStateReceiver and its intent filters
        LocalBroadcastManager.getInstance(requireNonNull(getActivity())).registerReceiver(
                receiver, statusIntentFilter);
    }
}
