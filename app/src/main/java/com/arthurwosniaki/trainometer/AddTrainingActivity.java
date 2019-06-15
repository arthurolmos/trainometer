package com.arthurwosniaki.trainometer;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageButton;

import com.arthurwosniaki.trainometer.adapters.AddTrainingListAdapter;
import com.arthurwosniaki.trainometer.database.viewmodels.TrainingFullViewModel;
import com.arthurwosniaki.trainometer.database.viewmodels.TrainingViewModel;
import com.arthurwosniaki.trainometer.entities.Exercise;
import com.arthurwosniaki.trainometer.entities.Training;
import com.arthurwosniaki.trainometer.entities.pojos.TrainingFull;
import com.arthurwosniaki.trainometer.database.DatabaseCallback;
import com.arthurwosniaki.trainometer.utils.Converters;
import com.arthurwosniaki.trainometer.utils.SendErrorReport;
import com.arthurwosniaki.trainometer.utils.ToastMessage;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import static java.util.Objects.requireNonNull;

public class AddTrainingActivity extends AppCompatActivity implements DatabaseCallback {
    private String TAG = AddTrainingActivity.class.getSimpleName();

    private List<Training> openTrainings;
    private AddTrainingListAdapter mAdapter;

    @BindView(R.id.etNameTraining) EditText etNameTraining;
    @BindView(R.id.etDateStart) EditText etDateStart;
    @BindView(R.id.etPeriodTraining) EditText etPeriodTraining;
    @BindView(R.id.btnCalendar) ImageButton btnCalendar;

    @BindView(R.id.etNameExercise) EditText etNameExercise;
    @BindView(R.id.etSerie) EditText etSerie;
    @BindView(R.id.etRepetition) EditText etRepetition;

    @BindView(R.id.rvExercises) RecyclerView rvExercises;

    @OnClick(R.id.btnAddExercise) void doAddExercise(){
        addExercise();
    }

    @OnClick(R.id.btnCalendar) void doOpenDatePicker(){
        openDatePicker();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_training);

        //ActionBar
        ActionBar actionBar = getSupportActionBar();
        requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        Calendar c = Calendar.getInstance();
        String s = Converters.calendarToString(c);
        etDateStart.setText(s);

        TrainingViewModel trainingViewModel = ViewModelProviders.of(this).get(TrainingViewModel.class);
        trainingViewModel.getOpenTrainings(true).observe(this,
                t -> openTrainings = t);
        trainingViewModel.getOpenTrainings(true).removeObservers(this);

        setupRecyclerView();
    }

    private void openDatePicker() {
        final Calendar myCalendar = Calendar.getInstance();

        DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            String s = Converters.calendarToString(myCalendar);
            etDateStart.setText(s);
        };

        new DatePickerDialog(AddTrainingActivity.this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void addExercise(){
        if(validateExercise()){
            String name = etNameExercise.getText().toString();
            int serie = Integer.parseInt(etSerie.getText().toString());
            String repetition = etRepetition.getText().toString();

            Exercise exercise = new Exercise(name, serie, repetition);

            LocalDateTime now = LocalDateTime.now();
            exercise.setCreatedAt(now);

            mAdapter.add(exercise);

            etNameExercise.setText("");
            etSerie.setText("");
            etRepetition.setText("");
            etNameExercise.requestFocus();
        }
    }

    private void setupRecyclerView(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvExercises.setLayoutManager(layoutManager);

        mAdapter = new AddTrainingListAdapter(this, new ArrayList<>());
        rvExercises.setAdapter(mAdapter);

        rvExercises.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean isLongPressDragEnabled() {
                return super.isLongPressDragEnabled();
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return super.isItemViewSwipeEnabled();
            }

            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                int swipeFlags = ItemTouchHelper.END;

                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                final int fromPos = viewHolder.getAdapterPosition();
                final int toPos = target.getAdapterPosition();
                // move item in `fromPos` to `toPos` in adapter.

                mAdapter.onItemMove(fromPos, toPos);
                return true;// true if moved, false otherwise
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                mAdapter.onItemSwiped(position);
            }

            public void onChildDraw (@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive){

                new RecyclerViewSwipeDecorator.Builder(AddTrainingActivity.this, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(AddTrainingActivity.this, R.color.IndianRed))
                        .addSwipeRightActionIcon(R.drawable.ic_delete_white_24dp)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(rvExercises);
    }

    private void save(){
        Log.d(TAG, "Insert new Training!");
        if(validateTraining()) {

            String name = etNameTraining.getText().toString();
            if(validTrainingName(name)){
                Log.d(TAG, "Inserting...");

                String s = etDateStart.getText().toString();
                LocalDate dateStart = Converters.stringToLocalDate(s);

                long period = Long.parseLong(etPeriodTraining.getText().toString());
                LocalDate dateConclusion = dateStart.plusMonths(period);

                TrainingFull trainingFull = new TrainingFull();
                Training t = new Training(name, dateStart, period, dateConclusion, true);

                LocalDateTime now = LocalDateTime.now();
                t.setCreatedAt(now);

                trainingFull.setTraining(t);

                List<Exercise> exercises = mAdapter.getExercises();
                if(exercises != null){
                    trainingFull.setExercises(exercises);
                }

                new TrainingFullViewModel(getApplication(), this).insert(trainingFull);
            }else{
                Log.d(TAG, "Training with same name found!");
                ToastMessage.showMessage(this, getString(R.string.training_same_name_open));
            }
        }
    }

    private void showMessage(String title, String message) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(title);
        dialog.setMessage(message);
        dialog.setCancelable(false);
        dialog.setPositiveButton("OK", (dialogInterface, i) -> finish());

        dialog.show();
    }

    private boolean validateExercise(){
        boolean valid = true;

        if(etNameExercise.getText().toString().equals("")){
            valid = false;
            etNameExercise.setError(getString(R.string.error_fill_field));
        }else{
            etNameExercise.setError(null);
        }

        if(etSerie.getText().toString().equals("")){
            valid = false;
            etSerie.setError(getString(R.string.error_fill_field));
        }else{
            etSerie.setError(null);
        }

        if(etRepetition.getText().toString().equals("")){
            valid = false;
            etRepetition.setError(getString(R.string.error_fill_field));
        }else{
            etRepetition.setError(null);
        }

        return valid;
    }

    private boolean validateTraining(){
        boolean valid = true;

        if(etNameTraining.getText().toString().equals("")){
            valid = false;
            etNameTraining.setError(getString(R.string.error_fill_field));
        }else{
            etNameTraining.setError(null);
        }

        if(etDateStart.getText().toString().equals("")){
            valid = false;
            etDateStart.setError(getString(R.string.error_fill_field));
        }else{
            etDateStart.setError(null);
        }

        if(etPeriodTraining.getText().toString().equals("")){
            valid = false;
            etPeriodTraining.setError(getString(R.string.error_fill_field));
        }else{
            etPeriodTraining.setError(null);
        }

        return valid;
    }

    private boolean validTrainingName(String name){
        boolean resp = true;

        for(Training t : openTrainings){
            if(t.getName().equals(name)){
                resp = false;
            }
        }

        return resp;
    }

    //===================== TOOLBAR & UTILITIES===================================================//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_add_training, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.save: save();
                return true;

            case android.R.id.home: onBackPressed();
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
    public void onItemDeleted() {
        Log.d(TAG, "Item deleted");
    }

    @Override
    public void onItemAdded() {
        Log.d(TAG, "Item Added!");
        showMessage(getString(R.string.success), getString(R.string.insert_success, "Treino"));
    }

    @Override
    public void onDataNotAvailable() {
        Log.d(TAG, "Data not Available");
        ToastMessage.showMessage(this, getString(R.string.error));
    }

    @Override
    public void onItemUpdated() {
        Log.d(TAG, "Item updated");
    }
}
