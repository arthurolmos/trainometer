package com.arthurwosniaki.trainometer;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
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
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import com.arthurwosniaki.trainometer.adapters.ExecuteExerciseAdapter;
import com.arthurwosniaki.trainometer.database.DatabaseCallback;
import com.arthurwosniaki.trainometer.database.viewmodels.ExerciseViewModel;
import com.arthurwosniaki.trainometer.database.viewmodels.SerieViewModel;
import com.arthurwosniaki.trainometer.entities.Serie;
import com.arthurwosniaki.trainometer.stopwatch.fragments.FragmentAdapter;
import com.arthurwosniaki.trainometer.utils.AlertDialogUtil;
import com.arthurwosniaki.trainometer.utils.Converters;
import com.arthurwosniaki.trainometer.utils.SendErrorReport;
import com.arthurwosniaki.trainometer.utils.ToastMessage;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import static com.arthurwosniaki.trainometer.stopwatch.services.ServiceConstants.FRAGMENT_PREFERENCES;
import static java.util.Objects.requireNonNull;

public class ExecuteExerciseActivity extends AppCompatActivity implements DatabaseCallback {
    String TAG = ExecuteExerciseActivity.class.getSimpleName();

    private ExecuteExerciseAdapter mAdapter;

    private long mLastClickTime = 0;

    private int position;
    private long idExecution ;
    private long idExercise;
    private long idTraining;

    boolean startedFromNotification;
    private int fragmentPosition;

    private int serieTotal;

    private List<Serie> series;


    @BindView(R.id.tvExerciseName) TextView tvExerciseName;
    @BindView(R.id.tvCounter) TextView tvCounter;

    @BindView(R.id.etReps) EditText etReps;
    @BindView(R.id.etWeight) EditText etWeight;
    @BindView(R.id.rvExecuteExercise) RecyclerView rvExecuteExercise;
    @BindView(R.id.etComment) EditText etComment;

    @BindView(R.id.viewPager) ViewPager viewPager;
    @BindView(R.id.tabLayout) TabLayout tabLayout;


    /*=== Action Buttons === */
    @OnClick(R.id.btnClear) void clearComments(){
        etComment.setText("");
    }

    @OnClick(R.id.btnAddSerie) void addSerie(){
        if(series != null){
            if(series.size() < serieTotal){
                if(validate()){
                    // mis-clicking prevention, using threshold of 1000 ms
                    if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                        return;
                    }
                    mLastClickTime = SystemClock.elapsedRealtime();

                    int reps = Integer.parseInt(etReps.getText().toString());
                    float weight = Float.parseFloat(etWeight.getText().toString());
                    weight = Converters.roundDecimals(weight);
                    String comment = etComment.getText().toString();

                    Serie serie = new Serie(idExecution, idExercise, reps, weight, comment);

                    new SerieViewModel(getApplication(), this).insert(serie);
                }
            }else{
                ToastMessage.showMessage(this, "Total de séries preenchidos!");
            }
        }
    }

    /*=== Modifiers Buttons === */
    @OnClick(R.id.btnPlusReps) void plusReps(){
        int reps =0;

        if(!etReps.getText().toString().equals("")) {
            reps = Integer.parseInt(etReps.getText().toString());
        }

        etReps.setText(String.valueOf(++reps));
    }

    @OnClick(R.id.btnSubReps) void subReps(){
        int reps =0;

        if(!etReps.getText().toString().equals("")){
            reps = Integer.parseInt(etReps.getText().toString());
        }

        if(reps>0)
            etReps.setText(String.valueOf(--reps));
    }

    @OnClick(R.id.btnPlusWeight) void plusWeight(){
        double weight = 0;

        if(!etWeight.getText().toString().equals("")) {
            weight = Double.parseDouble(etWeight.getText().toString());
        }

        weight = weight + 0.5;
        String w = Double.toString(weight);

        etWeight.setText(w);
    }

    @OnClick(R.id.btnSubWeight) void subWeight(){
        double weight = 0;

        if(!etWeight.getText().toString().equals("")) {
            weight = Double.parseDouble(etWeight.getText().toString());
        }

        if (weight > 0) {
            weight = weight - 0.5;
            String w = Double.toString(weight);

            etWeight.setText(w);
        }
    }


    //=== ACTIVITY ====//
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_execute_exercise);

        //ActionBar
        ActionBar actionBar = getSupportActionBar();
        requireNonNull(actionBar).setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        Bundle extras = getIntent().getExtras();
        //If started from an Activity, will get Intent Extras
        if(extras != null) {
            getValuesFromExtras(extras);

            //If started from Notification, get Fragment Position from notification. Else, from preferences.
            if(startedFromNotification){
                fragmentPosition = extras.getInt("fragment_position", 0);
            }else{
                SharedPreferences preferences = this.getSharedPreferences(FRAGMENT_PREFERENCES, Context.MODE_PRIVATE);
                fragmentPosition = preferences.getInt("current_fragment_item", 0);
            }

            setupRecyclerView();
            setupFragment(extras);
            initialize();
        }
    }

    private void getValuesFromExtras(Bundle extras){
        //Getting Extras
        String name = extras.getString("name_exercise");
        tvExerciseName.setText(name);

        idExecution = extras.getLong("id_execution");
        idExercise = extras.getLong("id_exercise");
        idTraining = extras.getLong( "id_training");

        position = extras.getInt("position");

        startedFromNotification = extras.getBoolean("started_notification", false);
    }

    private void setupRecyclerView(){
        mAdapter = new ExecuteExerciseAdapter(this, rvExecuteExercise, this);
        rvExecuteExercise.setAdapter(mAdapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvExecuteExercise.setLayoutManager(layoutManager);

        rvExecuteExercise.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));


        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,
                ItemTouchHelper.RIGHT) {

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

                if(direction == ItemTouchHelper.END)
                    mAdapter.onItemSwipedRight(position);
            }

            public void onChildDraw (@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive){

                new RecyclerViewSwipeDecorator.Builder(ExecuteExerciseActivity.this, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(ContextCompat.getColor(ExecuteExerciseActivity.this, R.color.IndianRed))
                        .addSwipeRightActionIcon(R.drawable.ic_delete_white_24dp)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(rvExecuteExercise);
    }

    private void setupFragment(Bundle bundle) {
        FragmentAdapter fAdapter = new FragmentAdapter(this, getSupportFragmentManager(), bundle);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        viewPager.setAdapter(fAdapter);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.setCurrentItem(fragmentPosition);
    }

    private void initialize(){
        //Exercise Observer
        ExerciseViewModel exerciseViewModel =
                ViewModelProviders.of(this).get(ExerciseViewModel.class);
        exerciseViewModel.getExerciseById(idExercise).observe(this, e->{
            if(e != null){
                Log.d(TAG,"Exercise found!");
                Log.d(TAG,"Updating UI...");

                tvExerciseName.setText(e.getName());
                serieTotal = e.getSerieTotal();
            }
        });
        exerciseViewModel.getExerciseById(idExercise).removeObservers(this);


        //Series Observer
        SerieViewModel seriesViewModel =
                ViewModelProviders.of(this).get(SerieViewModel.class);
        seriesViewModel.getSeriesByExecutionIdAndExerciseId(idExecution, idExercise).observe(this, s->{
            if(s != null){
                Log.d(TAG, "Series found!");
                series = s;

                //If fields are empty, fill them with last input value
                if( etWeight.getText().toString().equals("") &&
                    etReps.getText().toString().equals("") &&
                    etComment.getText().toString().equals("")){
                    if(series.size() > 0){
                        Log.d(TAG, "Series size > 0!");

                        float weight = series.get(series.size()-1).getWeight();
                        weight = Converters.roundDecimals(weight);
                        String w = Float.toString(weight);
                        etWeight.setText(w);

                        int reps = series.get(series.size()-1).getReps();
                        etReps.setText(Integer.toString(reps));

                        String comment = series.get(series.size()-1).getComment();
                        if(comment != null){
                            etComment.setText(comment);
                        }
                    }
                }

                Log.d(TAG, "Setting series adapter!");
                mAdapter.setSeries(series);

                String counter = "SERIES: " + series.size() + "/" + serieTotal;
                tvCounter.setText(counter);
            }
        });
    }

    private boolean validate() {
        boolean valid = true;

        if(etReps.getText().toString().equals("")){
            valid = false;
            etReps.setError("Preencha o campo corretamente!");
        }else{
            etReps.setError(null);
        }

        if(etWeight.getText().toString().equals("")){
            valid = false;
            etWeight.setError("Preencha o campo corretamente!");
        }else{
            etWeight.setError(null);
        }

        return valid;
    }

    private void save() {
        onBackPressed();
        ToastMessage.showMessage(this, "Salvo com sucesso!");
    }

    private void showHistory(){
        Intent intent = new Intent(this, HistorySerieActivity.class);
        intent.putExtra("id_execution", idExecution);
        intent.putExtra("id_exercise", idExercise);

        startActivity(intent);
    }

    private void showMessageDialog(){
        AlertDialog.Builder dialog = new AlertDialogUtil(this)
                .createAlertDialog("Erro", "Exercício não encontrado!");
        dialog.setPositiveButton("OK", (dialogInterface, i) -> onBackPressed());
        dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //SET FRAGMENT'S LAST SELECTED ITEM
        SharedPreferences preferences = this.getSharedPreferences(FRAGMENT_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("current_fragment_item", viewPager.getCurrentItem());
        editor.apply();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if(startedFromNotification){
            Intent intent = new Intent(this, ExecuteTrainingActivity.class);

            intent.putExtra("id_execution", idExecution);
            intent.putExtra("id_exercise", idExercise);
            intent.putExtra("id_training", idTraining);

            intent.putExtra("position", position);

            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent);
        }
    }


    //=== TOOLBAR & UTILITIES ===//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_execute_exercise, menu);
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
            case R.id.save: save();
                return true;

            case R.id.history: showHistory();
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

    @Override
    public void onItemDeleted() {
        Log.d(TAG, "Item deleted!");
    }

    @Override
    public void onItemAdded() {
        Log.d(TAG, "Item added!");

        ToastMessage.showMessage(this, "Série adicionada!");
    }

    @Override
    public void onDataNotAvailable() {

    }

    @Override
    public void onItemUpdated() {

    }
}
