package com.arthurwosniaki.trainometer.entities.pojos;

import android.arch.persistence.room.ColumnInfo;

import java.time.LocalDate;

public class TrainingWithExecutionsBkp {
    private long id;
    private String name;
    private boolean open;

    @ColumnInfo(name = "id_execution")
    private long idExecution;

    @ColumnInfo(name = "date_start")
    private LocalDate dateStart;

//    @ColumnInfo(name = "open_execution")
//    private boolean openExecution;

    public TrainingWithExecutionsBkp(){
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public long getIdExecution() {
        return idExecution;
    }

    public void setIdExecution(long idExecution) {
        this.idExecution = idExecution;
    }

    public LocalDate getDateStart() {
        return dateStart;
    }

    public void setDateStart(LocalDate dateStart) {
        this.dateStart = dateStart;
    }

//    public boolean isOpenExecution() {
//        return openExecution;
//    }
//
//    public void setOpenExecution(boolean openExecution) {
//        this.openExecution = openExecution;
//    }
}
