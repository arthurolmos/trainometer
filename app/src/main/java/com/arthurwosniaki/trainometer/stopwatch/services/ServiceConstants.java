package com.arthurwosniaki.trainometer.stopwatch.services;


public interface ServiceConstants {
    //GENERAL
    String DATE_PATTERN =
            "(0?[1-9]|1[012]) [/.-] (0?[1-9]|[12][0-9]|3[01]) [/.-] ((19|20)\\d\\d)";
    String FRAGMENT_PREFERENCES = "fragment_preferences";

    //TIMER
    String CHANNEL_DEFAULT_TIMER_ID = "CHANNEL_DEFAULT_TIMER_ID";
    String CHANNEL_HIGH_TIMER_ID = "CHANNEL_HIGH_TIMER_ID";

    int TIMER_ID_SERVICE = 101;
    String TIMER_FRAGMENT_BROADCAST = "timer_fragment_broadcast";
    String TIMER_UPDATE = "timer_update";

    String TIMER_START = "timer_start";
    String TIMER_PAUSE = "timer_pause";
    String TIMER_STOP = "timer_stop";
    String TIMER_RESTART = "timer_restart";

    String UPDATE_TIMER_UI = "update_UI";
    String RESTART_TIMER_SERVICE = "restart_timer_service";


    //CHRONOMETER
    String CHANNEL_DEFAULT_CHRONOMETER_ID = "CHANNEL_DEFAULT_CHRONOMETER_ID";

    int CHRONOMETER_ID_SERVICE = 102;
    String CHRONOMETER_FRAGMENT_BROADCAST = "chronometer_fragment_broadcast";
    String CHRONOMETER_UPDATE = "chronometer_update";

    String CHRONOMETER_START = "chronometer_start";
    String CHRONOMETER_PAUSE = "chronometer_pause";
    String CHRONOMETER_STOP = "chronometer_stop";
    String CHRONOMETER_RESTART = "chronometer_restart";

    String UPDATE_CHRONOMETER_UI = "update_UI";
    String RESTART_CHRONOMETER_SERVICE = "restart_chronometer_service";
}
