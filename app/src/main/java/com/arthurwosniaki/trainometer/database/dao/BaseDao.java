package com.arthurwosniaki.trainometer.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Update;

@Dao
interface BaseDao <T>{

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(T t);

    @Update
    int update(T t);

    @Delete
    int delete(T t);
}
