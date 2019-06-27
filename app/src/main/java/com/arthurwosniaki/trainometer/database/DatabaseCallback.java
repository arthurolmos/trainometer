package com.arthurwosniaki.trainometer.database;

public interface DatabaseCallback {

    void onItemAdded(String s);

    void onItemUpdated(String s);

    void onItemDeleted(String s);

    void onDataNotAvailable();

}
