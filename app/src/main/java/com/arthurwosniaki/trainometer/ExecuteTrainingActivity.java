package com.arthurwosniaki.trainometer;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arthurwosniaki.trainometer.adapters.ExecuteTrainingAdapter;
import com.arthurwosniaki.trainometer.database.DatabaseCallback;
import com.arthurwosniaki.trainometer.database.viewmodels.ExecutionViewModel;
import com.arthurwosniaki.trainometer.database.viewmodels.ExecutionWithTrainingViewModel;
import com.arthurwosniaki.trainometer.database.viewmodels.ExerciseWithSeriesViewModel;
import com.arthurwosniaki.trainometer.entities.Execution;
import com.arthurwosniaki.trainometer.entities.Training;
import com.arthurwosniaki.trainometer.utils.AlertDialogUtil;
import com.arthurwosniaki.trainometer.utils.Converters;
import com.arthurwosniaki.trainometer.utils.SendErrorReport;
import com.arthurwosniaki.trainometer.utils.ToastMessage;

import java.time.LocalDate;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;

import static java.util.Objects.requireNonNull;

public class ExecuteTrainingActivity extends AppCompatActivity implements DatabaseCallback {
    private String TAG = ExecuteTrainingActivity.class.getSimpleName();

    private ExecuteTrainingAdapter mAdapter;
    private long mLastClickTime;

    Parcelable listState;

    Execution execution;

    @BindView(R.id.tvNameTraining) TextView tvNameTraining;
    @BindView(R.id.tvDateInit) TextView tvDateInit;

    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.rvExercises) RecyclerView rvExercises;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_execute_training);

        ButterKnife.bind(this);

        //ActionBar
        ActionBar actionBar = getSupportActionBar();
        requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        setupRecyclerView();

        long idTraining = getIntent().getLongExtra("id_training", -1);
        if(idTraining != -1){
            initializeData(idTraining);
        }
    }


    private void initializeData(long idTraining) {
        //Execution Observer
        ExecutionWithTrainingViewModel executionWithTraining = ViewModelProviders.of(this)
                .get(ExecutionWithTrainingViewModel.class);
        executionWithTraining.getOpenExecutionWithTraining(idTraining, true).observe(this, e->{
            if(e != null){
                Log.d(TAG, "Open Execution found! ID: " + e.getExecution().getId());
                Log.d(TAG, "Updating UI...");

                if(e.getTraining().size() > 0) {
                    Training t = e.getTraining().get(0);
                    Log.d(TAG, "Training ID: " + t.getId());

                    tvNameTraining.setText(t.getName());

                    String dateStart = Converters.localDateToString(e.getExecution().getDateStart());
                    tvDateInit.setText(dateStart);

                    execution = e.getExecution();
                    mAdapter.setIdExecution(execution.getId());
                }
            }else{
                Log.d(TAG, "Training not found!");
            }
        });


        //Exercise Observer
        ExerciseWithSeriesViewModel exerciseWithSeriesViewModel =
                ViewModelProviders.of(this).get(ExerciseWithSeriesViewModel.class);
        exerciseWithSeriesViewModel.getExerciseAndSeriesByIdTraining(idTraining).observe(this, e -> {
            if(e != null) {
                Log.d(TAG, "Exercises found!");

                if (e.size() > 0) {
                    Log.d(TAG, "Updating Exercises adapter...");
                    Collections.sort(e);
                    mAdapter.setExercisesWithSeries(e);

                } else {
                    Log.d(TAG, "Exercises not found!");
                    ToastMessage.showMessage(this, "Sem exercícios cadastrados!");
                }
            } else {
                Log.d(TAG, "Exercises not found!");
            }
        });
    }

    private void setupRecyclerView() {
        //Setting adapter
        mAdapter = new ExecuteTrainingAdapter(this, rvExercises, this);
        rvExercises.setAdapter(mAdapter);


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvExercises.setLayoutManager(layoutManager);

        rvExercises.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private void finishExecution() {
        AlertDialog.Builder dialog = new AlertDialogUtil(this)
                .createAlertDialog("Atenção!",getString(R.string.finish_execution)
                        );
        dialog.setPositiveButton("OK",(dialogInterface, i) -> {
            LocalDate now = LocalDate.now();
            execution.setDateEnd(now);
            execution.setOpen(false);

            new ExecutionViewModel(getApplication(), this).update(execution);
            finish();

        }).setNegativeButton("Cancelar", (dialogInterface, i) ->{

        });
        dialog.show();
    }

    private void discardExecution() {
        AlertDialog.Builder dialog = new AlertDialogUtil(this)
                .createAlertDialog("Atenção!",
                        getString(R.string.discard_execution));
        dialog.setPositiveButton("OK",(dialogInterface, i) -> {

            new ExecutionViewModel(getApplication(), this).delete(execution);
            finish();

        }).setNegativeButton("Cancelar", (dialogInterface, i) ->{

        });
        dialog.show();
    }

    private void showMessageDialog(String title, String message){
        AlertDialog.Builder dialog = new AlertDialogUtil(this)
                .createAlertDialog(title, message);
        dialog.setPositiveButton("OK", (dialogInterface, i) -> {
            finish();
        });
        dialog.show();
    }


    //=== SAVE AND RESTORE DATA ===//
    protected void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);

        // Save list state
        listState = requireNonNull(rvExercises.getLayoutManager()).onSaveInstanceState();
        state.putParcelable("listState", listState);
    }

    protected void onRestoreInstanceState(Bundle state) {
        super.onRestoreInstanceState(state);

        // Retrieve list state and list/item positions
        if(state != null)
            listState = state.getParcelable("listState");
    }


    //===================== TOOLBAR & UTILITIES===================================================//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_execute_training, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Mis-clicking prevention, using threshold of 1000 ms
        if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
            return false;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        // Handle item selection
        switch (item.getItemId()) {
            case R.id.finish: finishExecution();
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.discard: discardExecution();
                return true;

            case R.id.report_error:
                new SendErrorReport(this).sendEmail();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //CALLBACKS
    @Override
    public void onItemDeleted(String s) {
        Log.d(TAG, "Item deleted!");
        ToastMessage.showMessage(this, getString(R.string.delete_success, s));
    }

    @Override
    public void onItemAdded(String s) {
        Log.d(TAG, "Item added!");
    }

    @Override
    public void onDataNotAvailable() {
        Log.d(TAG, "Data not available!");
    }

    @Override
    public void onItemUpdated(String s) {
        Log.d(TAG, "Item updated!");
        ToastMessage.showMessage(this, getString(R.string.update_success, s));
    }
}