package com.arthurwosniaki.trainometer.database;

public interface DatabaseCallback {

    void onItemDeleted();

    void onItemAdded();

    void onDataNotAvailable();

    void onItemUpdated();
}
