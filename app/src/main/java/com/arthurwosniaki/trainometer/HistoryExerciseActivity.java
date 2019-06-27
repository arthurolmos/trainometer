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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arthurwosniaki.trainometer.adapters.HistoryExerciseAdapter;
import com.arthurwosniaki.trainometer.database.viewmodels.ExerciseWithSeriesViewModel;
import com.arthurwosniaki.trainometer.database.DatabaseCallback;
import com.arthurwosniaki.trainometer.utils.SendErrorReport;
import com.arthurwosniaki.trainometer.utils.ToastMessage;

import butterknife.BindView;
import butterknife.ButterKnife;

import static java.util.Objects.requireNonNull;

public class HistoryExerciseActivity extends AppCompatActivity implements DatabaseCallback {
    private String TAG = HistoryExerciseActivity.class.getSimpleName();

    private HistoryExerciseAdapter mAdapter;

    @BindView(R.id.tvNameTraining) TextView tvNameTraining;
    @BindView(R.id.tvDateInit) TextView tvDateInit;

    @BindView(R.id.progressBar) ProgressBar progressBar;
    @BindView(R.id.rvHistoryExercises) RecyclerView rvHistoryExercises;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_exercise);

        //ActionBar
        ActionBar actionBar = getSupportActionBar();
        requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        setupRecyclerView();
        initialize();
    }


    private void initialize() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Log.d(TAG, "Getting extras...");

            long idTraining = extras.getLong("id_training");

            ExerciseWithSeriesViewModel exerciseWithSeriesViewModel =
                    ViewModelProviders.of(this).get(ExerciseWithSeriesViewModel.class);
            exerciseWithSeriesViewModel.getExerciseAndSeriesByIdTraining(idTraining).observe(this, e -> {
                if(e != null) {
                    Log.d(TAG, "Exercises found!");

                    if (e.size() > 0) {
                        Log.d(TAG, "Updating Exercises adapter...");
                        mAdapter.setExercises(e);

                    } else {
                        Log.d(TAG, "Exercises not found!");
                        ToastMessage.showMessage(this, "Sem exercícios cadastrados!");
                    }
                } else {
                    Log.d(TAG, "Exercises not found!");
                }
            });

        } else {
            Log.d(TAG, "Extras null.");
            showMessageDialog("Erro!", "Erro ao abrir o Treino!");
        }
    }

    private void setupRecyclerView() {
        //Setting adapter
        mAdapter = new HistoryExerciseAdapter(this, rvHistoryExercises, this);
        rvHistoryExercises.setAdapter(mAdapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvHistoryExercises.setLayoutManager(layoutManager);

        rvHistoryExercises.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    private void showMessageDialog(String title, String message){
        android.app.AlertDialog.Builder dialog = new android.app.AlertDialog.Builder(this);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setPositiveButton("OK", (dialogInterface, i) -> finish());
        dialog.show();
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


    //CALLBACKS
    @Override
    public void onItemDeleted(String s) {
        Log.d(TAG, "Item deleted!");
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
    }
}