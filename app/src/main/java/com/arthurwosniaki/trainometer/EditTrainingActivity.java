package com.arthurwosniaki.trainometer;

import android.app.DatePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
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

import com.arthurwosniaki.trainometer.adapters.EditTrainingListAdapter;
import com.arthurwosniaki.trainometer.database.DatabaseCallback;
import com.arthurwosniaki.trainometer.database.viewmodels.ExerciseViewModel;
import com.arthurwosniaki.trainometer.database.viewmodels.TrainingViewModel;
import com.arthurwosniaki.trainometer.database.viewmodels.TrainingWithExercisesViewModel;
import com.arthurwosniaki.trainometer.entities.Exercise;
import com.arthurwosniaki.trainometer.entities.Training;
import com.arthurwosniaki.trainometer.entities.pojos.TrainingWithExercises;
import com.arthurwosniaki.trainometer.utils.Converters;
import com.arthurwosniaki.trainometer.utils.SendErrorReport;
import com.arthurwosniaki.trainometer.utils.ToastMessage;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import static java.util.Objects.requireNonNull;

public class EditTrainingActivity extends AppCompatActivity implements DatabaseCallback {
    private String TAG = EditTrainingActivity.class.getSimpleName();

    private long idTraining;

    private Training training;
    private List<Training> openTrainings;
    private List<Exercise> exercises;

    private EditTrainingListAdapter mAdapter;

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

        setupCalendar();
        setupRecyclerView();

        idTraining = getIntent().getLongExtra("id_training", 0);
        initializeData(idTraining);
    }

    private void setupCalendar(){
        Calendar c = Calendar.getInstance();
        String s = Converters.calendarToString(c);
        etDateStart.setText(s);
    }

    private void setupRecyclerView(){
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvExercises.setLayoutManager(layoutManager);

        mAdapter = new EditTrainingListAdapter(this, rvExercises, this);
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

                new RecyclerViewSwipeDecorator.Builder(EditTrainingActivity.this, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(EditTrainingActivity.this, R.color.IndianRed))
                        .addSwipeRightActionIcon(R.drawable.ic_delete_white_24dp)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(rvExercises);
    }

    private void initializeData(long idTraining){
        //Training Observer
        TrainingWithExercisesViewModel trainingWithExercisesViewModel =
                ViewModelProviders.of(this).get(TrainingWithExercisesViewModel.class);
        trainingWithExercisesViewModel.getTrainingWithExercises(idTraining).observe(this, t -> {
            if(t != null){
                training = t.getTraining();

                etNameTraining.setText(training.getName());

                String dateStart = Converters.localDateToString(training.getDateStart());
                etDateStart.setText(dateStart);

                String period = Converters.longToString(training.getPeriod());
                etPeriodTraining.setText(period);

                if(t.getExercises() != null){
                    exercises = t.getExercisesOrderedBySequence();

                    mAdapter.setExercises(exercises);
                }
            }
        });

        //Open Trainings Observer for validate equal name on update
        TrainingViewModel trainingViewModel =
                ViewModelProviders.of(this).get(TrainingViewModel.class);
        trainingViewModel.getOpenTrainings(true).observe(this, t->{
            if(t != null){
                openTrainings = t;
            }
        });
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

        new DatePickerDialog(EditTrainingActivity.this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void addExercise(){
        if(validateExercise()){
            String name = etNameExercise.getText().toString();
            int serie = Integer.parseInt(etSerie.getText().toString());
            String repetition = etRepetition.getText().toString();

            Exercise exercise = new Exercise(name, serie, repetition, idTraining);
            new ExerciseViewModel(getApplication(), this).insert(exercise);

            clearFields();
        }
    }

    private void clearFields(){
        etNameExercise.setText("");
        etSerie.setText("");
        etRepetition.setText("");
        etNameExercise.requestFocus();
    }

    private void update(){
        Log.d(TAG, "Updating Training!");
        if(validateTraining()) {

            String name = etNameTraining.getText().toString();
            if(validTrainingName(name, idTraining)){
                Log.d(TAG, "Updating...");

                String date = etDateStart.getText().toString();
                LocalDate dateStart = Converters.stringToLocalDate(date);

                long period = Long.parseLong(etPeriodTraining.getText().toString());
                LocalDate dateConclusion = dateStart.plusMonths(period);

                //Creates new Training
                Training t = new Training(name, dateStart, period, dateConclusion, true, idTraining);

                //Get Exercises and set Sequence
                List<Exercise> exercises = mAdapter.getExercises();
                if(exercises.size() > 0){
                    for(int i=0; i<exercises.size(); i++){
                        exercises.get(i).setSequence(i+1);
                    }
                }

                //Creates Training with Exercises and Update
                TrainingWithExercises trainingWithExercises = new TrainingWithExercises();
                trainingWithExercises.setTraining(t);
                trainingWithExercises.setExercises(exercises);

                new TrainingWithExercisesViewModel(getApplication(), this)
                        .update(trainingWithExercises);

                finish();

            }else{
                Log.d(TAG, "Training with same name found!");
                ToastMessage.showMessage(this, getString(R.string.training_same_name_open));
            }
        }
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

    private boolean validTrainingName(String name, long idTraining){
        boolean resp = true;

        if(openTrainings != null){
            for(Training t : openTrainings){
                if(t.getName().equals(name) && t.getId() != idTraining){
                    resp = false;
                }
            }
        }

        return resp;
    }

    //===================== TOOLBAR & UTILITIES===================================================//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_edit_training, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.update: update();
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
        ToastMessage.showMessage(this, getString(R.string.update_success, s));
    }
}
