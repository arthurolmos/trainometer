package com.arthurwosniaki.trainometer.stopwatch.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
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
import com.arthurwosniaki.trainometer.stopwatch.services.TimerService;
import com.arthurwosniaki.trainometer.utils.Converters;
import com.ikovac.timepickerwithseconds.MyTimePickerDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.arthurwosniaki.trainometer.stopwatch.services.ServiceConstants.RESTART_TIMER_SERVICE;
import static com.arthurwosniaki.trainometer.stopwatch.services.ServiceConstants.TIMER_FRAGMENT_BROADCAST;
import static com.arthurwosniaki.trainometer.stopwatch.services.ServiceConstants.FRAGMENT_PREFERENCES;
import static com.arthurwosniaki.trainometer.stopwatch.services.ServiceConstants.TIMER_UPDATE;
import static com.arthurwosniaki.trainometer.stopwatch.services.ServiceConstants.UPDATE_TIMER_UI;
import static com.arthurwosniaki.trainometer.stopwatch.services.SingletonServiceManager.isTimerPaused;
import static com.arthurwosniaki.trainometer.stopwatch.services.SingletonServiceManager.isTimerRunningBackground;
import static com.arthurwosniaki.trainometer.stopwatch.services.SingletonServiceManager.isTimerServiceRunning;
import static java.util.Objects.requireNonNull;

public class TimerFragment extends Fragment {

    @OnClick(R.id.btnStartTimer) void startTimer(){
        if(!isTimerServiceRunning){
            timeLeft = initTime;
        }

        isTimerServiceRunning = true;
        isTimerPaused = false;

        startService(getActivity(), createBundle());
        Log.v(TAG, "Service is running after btnStartAction?: " + isTimerServiceRunning);
    }

    @OnClick(R.id.btnPauseTimer) void pauseTimer(){
        isTimerPaused = true;

        startService(getActivity(), createBundle());
        Log.v(TAG, "Service is running after PauseAction?: " + isTimerServiceRunning);
    }

    @OnClick(R.id.btnStopTimer) void stopTimer(){
        isTimerServiceRunning = false;
        requireNonNull(getActivity()).stopService(new Intent(getActivity(), TimerService.class));

        Log.v(TAG, "Service is running after StopAction?: " + isTimerServiceRunning);

        updateUI();
    }


    @BindView(R.id.tvTimer) TextView tvTimer;
    @BindView(R.id.btnStartTimer) ImageButton btnStart;
    @BindView(R.id.btnPauseTimer) ImageButton btnPause;
    @BindView(R.id.btnStopTimer) ImageButton btnStop;


    private String TAG = TimerFragment.class.getSimpleName();

    private TimerFragmentReceiver receiver;

    private long initTime;
    private long timeLeft;

    private long idTraining;
    private long idExecution;
    private long idExercise;
    private int position;


    public TimerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_timer, container, false);
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

        //Get Initial Time from SharedPrefeences
        SharedPreferences preferences = requireNonNull(getActivity()).getSharedPreferences(FRAGMENT_PREFERENCES, Context.MODE_PRIVATE);
        initTime = preferences.getLong("init_time", 0);

        /*=== SET VIEWS ACTION ===*/
        setTvTimerAction();

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        isTimerRunningBackground = false;

        //If Service is timerRunning, stops Service. If timer is timerRunning, it will be recreated.
        if (isTimerServiceRunning) {
            Log.v(TAG, "Service was running when OnStart was called.");

            if(getActivity() != null){
                getActivity().stopService(new Intent(getActivity(), TimerService.class));
                Log.v(TAG, "Service still running onStart? " + isTimerServiceRunning);
            }
        }

        updateUI();
    }

    private Bundle createBundle(){
        Bundle extras = new Bundle();

        extras.putLong("id_training", idTraining);
        extras.putLong("id_execution", idExecution);
        extras.putLong("id_exercise", idExercise);

        extras.putInt("fragment_position", 1);
        extras.putInt("position", position);

        extras.putLong("time", timeLeft);
        extras.putLong("init_time", initTime);

        return extras;
    }

    //--- SET VIEWS ACTIONS ---//
    private void setTvTimerAction() {
        //Set Action
        tvTimer.setOnClickListener(view -> {

                String[] time = tvTimer.getText().toString().split(":");
                String h = time[0];
                String m = time[1];
                String s = time[2];

                int hour = Integer.parseInt(h);
                int minute = Integer.parseInt(m);
                int second = Integer.parseInt(s);

                MyTimePickerDialog mTimePicker = new MyTimePickerDialog(requireNonNull(getActivity()),
                        (v, hourPicker, minutePicker, secondsPicker) -> {
                            String time1 = String.format("%02d",hourPicker) + ":"
                                    + String.format("%02d", minutePicker) + ":"
                                    + String.format("%02d", secondsPicker);
                            tvTimer.setText(time1);

                            long t = Converters.stringTimeToLong(time1);
                            SharedPreferences preferences = requireNonNull(getActivity()).getSharedPreferences(FRAGMENT_PREFERENCES, Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putLong("init_time", t);
                            editor.apply();

                            initTime = t;

                        }, hour, minute, second, true);
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
        });
    }

    //--- SET VIEWS VISIBILITY ---//
    private void updateUI(){
        setTvTimerVisibility();
        setVisibleButtons();
    }

    //Buttons Visibility
    private void setTvTimerVisibility(){
        String text;

        if(isTimerServiceRunning){
            //If timer is running, text will be timeleft
            text = Converters.longTimeToString(timeLeft);
            tvTimer.setEnabled(false);
        }else{
            //If timer is not running, then
            if(initTime != 0) {
                text = Converters.longTimeToString(initTime);
            }else {
                text = getResources().getString(R.string.timer_default_time);
            }
            tvTimer.setEnabled(true);
        }

        tvTimer.setText(text);
    }

    private void setVisibleButtons() {
        if(isTimerServiceRunning){
            if(isTimerPaused){
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
        isTimerRunningBackground = true;

        requireNonNull(getActivity()).stopService(new Intent(getActivity(), TimerService.class));
        Log.v(TAG, "Service is running after onPause?: " + isTimerServiceRunning);

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
            Intent serviceIntent = new Intent(getActivity(), TimerService.class);
            serviceIntent.putExtras(extras);

            requireNonNull(context).startService(serviceIntent);
        }
    }


    //=== BROADCAST RECEIVER ===/
    public class TimerFragmentReceiver extends BroadcastReceiver{
        public TimerFragmentReceiver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v(TAG, "Message received from Timer Fragment Broadcast!");

            if(intent != null){
                String action = intent.getAction();
                Bundle extras = intent.getExtras();

                //Updates tvTimer
                if(action != null){
                    switch(action){
                        case TIMER_UPDATE:
                            if (extras != null) {
                                timeLeft = extras.getLong("time", initTime);

                                String s = extras.getString("elapsed_time");
                                tvTimer.setText(s);
                            }
                            break;

                        case RESTART_TIMER_SERVICE:
                            Log.v(TAG, "Destroying service for restart!");

                            if(isTimerServiceRunning) {
                                    startService(context, extras);

                                    Log.v(TAG, "Service is running after Restart Broadcast?: " + isTimerServiceRunning);
                            }
                            break;

                        case UPDATE_TIMER_UI:
                            Log.v(TAG, "Update UI!");
                            if(extras != null){
                                timeLeft = extras.getLong("time", initTime);
                            }

                            updateUI();
                            break;
                    }
                }
            }
        }
    }

    private void registerBroadcastReceiver() {
        receiver = new TimerFragmentReceiver();
        IntentFilter statusIntentFilter = new IntentFilter(TIMER_FRAGMENT_BROADCAST);
        statusIntentFilter.addAction(UPDATE_TIMER_UI);
        statusIntentFilter.addAction(TIMER_UPDATE);
        statusIntentFilter.addAction(RESTART_TIMER_SERVICE);

        // Registers the DownloadStateReceiver and its intent filters
        LocalBroadcastManager.getInstance(requireNonNull(getActivity())).registerReceiver(
                receiver, statusIntentFilter);
    }
}
