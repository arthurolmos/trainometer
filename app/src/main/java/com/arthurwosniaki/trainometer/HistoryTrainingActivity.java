package com.arthurwosniaki.trainometer;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.arthurwosniaki.trainometer.adapters.HistoryTrainingAdapter;
import com.arthurwosniaki.trainometer.database.DatabaseCallback;
import com.arthurwosniaki.trainometer.database.viewmodels.TrainingViewModel;
import com.arthurwosniaki.trainometer.utils.SendErrorReport;
import com.arthurwosniaki.trainometer.utils.ToastMessage;

import butterknife.BindView;
import butterknife.ButterKnife;

import static java.util.Objects.requireNonNull;

public class HistoryTrainingActivity extends AppCompatActivity implements DatabaseCallback {
    private String TAG = HistoryTrainingActivity.class.getSimpleName();

    @BindView(R.id.rvHistoryTraining) RecyclerView rvTrainingHistory;

    HistoryTrainingAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_training);

        //ActionBar
        ActionBar actionBar = getSupportActionBar();
        requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        setupRecyclerView();
        initializeData();
    }

    private void setupRecyclerView(){
        mAdapter = new HistoryTrainingAdapter(this, rvTrainingHistory, this);
        rvTrainingHistory.setAdapter(mAdapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvTrainingHistory.setLayoutManager(layoutManager);

        rvTrainingHistory.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private void initializeData(){
        TrainingViewModel trainingViewModel =
                ViewModelProviders.of(this).get(TrainingViewModel.class);
        trainingViewModel.getAllTrainings().observe(this, t->{
            if(t != null){
                mAdapter.setTrainings(t);
            }
        });
    }


    //===================== TOOLBAR & UTILITIES===================================================//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_default, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.report_error:
                new SendErrorReport(this).sendEmail();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    //INTERFACE CALLBACKS
    @Override
    public void onItemDeleted(String s) {
        Log.d(TAG, "Item Deleted");
    }

    @Override
    public void onItemAdded(String s) {
        Log.d(TAG, "Item Added!");
        ToastMessage.showMessage(this, getString(R.string.insert_success, s));
    }

    @Override
    public void onDataNotAvailable() {
        Log.d(TAG, "Data not Available");
        ToastMessage.showMessage(this, getString(R.string.error));
    }

    @Override
    public void onItemUpdated(String s) {
        Log.d(TAG, "Item Updated");
    }
}
