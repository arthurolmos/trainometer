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

import com.arthurwosniaki.trainometer.adapters.HistorySerieAdapter;
import com.arthurwosniaki.trainometer.database.DatabaseCallback;
import com.arthurwosniaki.trainometer.database.viewmodels.ExerciseHistoryViewModel;
import com.arthurwosniaki.trainometer.entities.Execution;
import com.arthurwosniaki.trainometer.entities.Serie;
import com.arthurwosniaki.trainometer.entities.pojos.ExerciseHistory;
import com.arthurwosniaki.trainometer.utils.Converters;
import com.arthurwosniaki.trainometer.utils.SendErrorReport;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import butterknife.BindView;
import butterknife.ButterKnife;

import static java.util.Objects.requireNonNull;

public class HistorySerieActivity extends AppCompatActivity  implements DatabaseCallback {
    private String TAG = HistorySerieActivity.class.getSimpleName();

    private HistorySerieAdapter mAdapter;

    @BindView(R.id.rvHistory) RecyclerView rvHistory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_serie);

        //ActionBar
        ActionBar actionBar = getSupportActionBar();
        requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        setupRecyclerView();

        long idExercise = getIntent().getLongExtra("id_exercise", -1);
        if(idExercise != -1) {
            initializeData(idExercise);
        }
    }

    private void initializeData(long idExercise){
        ExerciseHistoryViewModel exerciseHistoryViewModel =
                ViewModelProviders.of(this).get(ExerciseHistoryViewModel.class);
        exerciseHistoryViewModel.getExerciseHistoryByIdExercise(idExercise).observe(this, e -> {
            if (e != null) {//
                List<ExerciseHistory> histories = createHistory(e);
                mAdapter.setHistories(histories);
            }
        });
    }

    private void setupRecyclerView() {
        mAdapter = new HistorySerieAdapter(this, this);
        rvHistory.setAdapter(mAdapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvHistory.setLayoutManager(layoutManager);

        rvHistory.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    public List<ExerciseHistory> createHistory(ExerciseHistory exerciseHistory) {

        List<Serie> series = exerciseHistory.getSeries();
        List<Execution> executions = exerciseHistory.getExecutions();

        List<ExerciseHistory> histories = new ArrayList<>();

        for(Execution e : executions) {
            ExerciseHistory h = new ExerciseHistory();

            //Filter Series by idExecution (already filtered by IdExercise)
            List<Serie> seriesExecution = series.stream()
                    .filter(s -> s.getIdExecution() == e.getId())
                    .collect(Collectors.toList());

            if(!seriesExecution.isEmpty()){
                int serieTotal = 0;
                for (Serie s : seriesExecution) {
                    h.getReps().add(Integer.toString(s.getReps()));
                    h.getWeight().add(Float.toString(s.getWeight()));

                    if(!s.getComment().equals("")) {
                        h.getComments().add(s.getComment());
                    }

                    serieTotal++;
                }

                h.setSerieTotal(serieTotal);
                h.setExercise(exerciseHistory.getExercise());
                h.setExecution(e);

                LocalDate date = e.getDateStart();
                String d = Converters.localDateToString(date);
                h.setDate(d);

                histories.add(h);
            }

        }
        return histories;
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
