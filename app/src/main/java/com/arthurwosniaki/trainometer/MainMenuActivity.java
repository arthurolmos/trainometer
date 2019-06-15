package com.arthurwosniaki.trainometer;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
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
import android.widget.ProgressBar;

import com.arthurwosniaki.trainometer.adapters.MainMenuAdapter;
import com.arthurwosniaki.trainometer.database.DatabaseCallback;
import com.arthurwosniaki.trainometer.database.viewmodels.TrainingWithExecutionsViewModel;
import com.arthurwosniaki.trainometer.utils.SendErrorReport;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

import static java.util.Objects.requireNonNull;



public class MainMenuActivity extends AppCompatActivity implements DatabaseCallback {
    private String TAG = MainMenuActivity.class.getSimpleName();

    private MainMenuAdapter mAdapter;

    @BindView(R.id.rvMainMenu) RecyclerView rvMainMenu;
    @BindView(R.id.progressBar) ProgressBar progressBar;


    @OnClick(R.id.btnAddTraining) void openAddTraining(){
        Intent intent = new Intent(MainMenuActivity.this, AddTrainingActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.btnHistTraining) void openTrainingHistory(){
        Intent intent = new Intent(MainMenuActivity.this, HistoryTrainingActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        //ActionBar
        ActionBar actionBar = getSupportActionBar();
        (requireNonNull(actionBar)).setDisplayHomeAsUpEnabled(false);

        ButterKnife.bind(this);

        setupRecyclerView();

        TrainingWithExecutionsViewModel trainingWithExecutionsViewModel =
                ViewModelProviders.of(this).get(TrainingWithExecutionsViewModel.class);
        trainingWithExecutionsViewModel.getOpenTrainingsWithExecutions(true).observe(this, trainings ->{
            if(trainings!= null){
                Log.d(TAG, "Trainings found: " + trainings.size());
                mAdapter.setTrainings(trainings);
            }
        });
    }


    private void setupRecyclerView(){
        //Setting adapter
        mAdapter = new MainMenuAdapter(this,  rvMainMenu, this);
        rvMainMenu.setAdapter(mAdapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rvMainMenu.setLayoutManager(layoutManager);

        rvMainMenu.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
            @Override
            public boolean isItemViewSwipeEnabled() {
                return super.isItemViewSwipeEnabled();
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }

            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;

                return makeMovementFlags(dragFlags, swipeFlags);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                
                if(direction == ItemTouchHelper.END)
                    mAdapter.onItemSwipedRight(position);
                else
                    mAdapter.onItemSwipedLeft(position);
            }

            public void onChildDraw (@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive){

                new RecyclerViewSwipeDecorator.Builder(MainMenuActivity.this, c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addSwipeRightBackgroundColor(ContextCompat.getColor(MainMenuActivity.this, R.color.IndianRed))
                        .addSwipeRightActionIcon(R.drawable.ic_delete_white_24dp)
                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(MainMenuActivity.this, R.color.LightGreen))
                        .addSwipeLeftActionIcon(R.drawable.ic_archive_white_24dp)
                        .create()
                        .decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(rvMainMenu);
    }

    //===================== TOOLBAR & UTILITIES===================================================//
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.report_error:
                new SendErrorReport(this).sendEmail();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    //INTERFACE CALLBACKS
    @Override
    public void onItemDeleted() {
        Log.d(TAG, "Item deleted!");
    }

    @Override
    public void onItemAdded() {
        Log.d(TAG, "Item added!");
    }

    @Override
    public void onDataNotAvailable() {
        Log.d(TAG, "Data not available!");
    }

    @Override
    public void onItemUpdated() {
        Log.d(TAG, "Item updated!");
    }
}
