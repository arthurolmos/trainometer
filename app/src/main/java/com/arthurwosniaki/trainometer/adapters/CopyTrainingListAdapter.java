package com.arthurwosniaki.trainometer.adapters;

import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.arthurwosniaki.trainometer.R;
import com.arthurwosniaki.trainometer.database.DatabaseCallback;
import com.arthurwosniaki.trainometer.database.viewmodels.ExerciseViewModel;
import com.arthurwosniaki.trainometer.entities.Exercise;
import com.arthurwosniaki.trainometer.utils.AlertDialogUtil;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class CopyTrainingListAdapter extends RecyclerView.Adapter<CopyTrainingListAdapter.ViewHolder>{
    private String TAG = CopyTrainingListAdapter.class.getSimpleName();

    private long mLastClickTime = 0;

    private List<Exercise> exercises;

    private RecyclerView rvEditExercises;

    private Activity activity;
    private Context context;
    private DatabaseCallback callback;

    public CopyTrainingListAdapter(Activity activity, RecyclerView rvEditExercises, DatabaseCallback callback) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.callback = callback;

        this.rvEditExercises = rvEditExercises;
    }

    public void setExercises(List<Exercise> e){
        exercises = e;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new View
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.list_copy_training, parent, false);

        v.setOnClickListener(view -> {
            //Mis-clicking prevention, using threshold of 1000 ms
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            final int position = rvEditExercises.getChildAdapterPosition(view);
            Exercise exercise = exercises.get(position);
            openEditDialog(exercise);
        });

        return new ViewHolder(v);
    }

    private void openEditDialog(Exercise exercise){
        String name = exercise.getName();
        int serieTotal = exercise.getSerieTotal();
        String repsTotal = exercise.getRepsTotal();


        AtomicBoolean modified = new AtomicBoolean();
        modified.set(false);

        AlertDialog.Builder builder = new AlertDialogUtil(activity)
                .createEditDialog("Editar Exercício");

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_exercise, null);


        EditText etName = view.findViewById(R.id.etName);
        etName.setText(exercise.getName());
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                modified.set(true);

                exercise.setName(etName.getText().toString());
            }
        });



        EditText etSerie = view.findViewById(R.id.etSerie);
        String serie = Integer.toString(exercise.getSerieTotal());
        etSerie.setText(serie);
        etSerie.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                modified.set(true);

                if(!etSerie.getText().toString().equals("")){
                    String serie = etSerie.getText().toString();
                    int serieTotal = Integer.parseInt(serie);

                    exercise.setSerieTotal(serieTotal);
                }
            }
        });


        EditText etReps = view.findViewById(R.id.etReps);
        String reps = exercise.getRepsTotal();
        etReps.setText(reps);
        etReps.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                modified.set(true);

                if(!etReps.getText().toString().equals("")){
                    exercise.setRepsTotal(etReps.getText().toString());
                }
            }
        });

        builder.setView(view)
                .setPositiveButton("OK", (positive, which) -> {
                    if(modified.get()){
                        new ExerciseViewModel(activity.getApplication(), callback)
                                .update(exercise);
                    }
                })
                .setNegativeButton("Cancelar", (negative, which) -> {
                    if(modified.get()){
                        exercise.setName(name);
                        exercise.setSerieTotal(serieTotal);
                        exercise.setRepsTotal(repsTotal);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        Exercise exercise = exercises.get(i);
        String name = exercise.getName();
        String serie = "SERIE: " + exercise.getSerieTotal();
        String repetition = "REPS: " + exercise.getRepsTotal();

        viewHolder.txtHeader.setText(name);
        viewHolder.txtFooterLeft.setText(serie);
        viewHolder.txtFooterRight.setText(repetition);
    }

    @Override
    public int getItemCount() {
        if(exercises != null)
            return exercises.size();
        else return 0;
    }

    public void onItemMove(int initialPosition, int finalPosition) {
        if(initialPosition < getItemCount() && finalPosition < getItemCount()){
            if(initialPosition < finalPosition){
                for(int i = initialPosition; i < finalPosition; i++){
                    Collections.swap(exercises, i, i+1);
                }
            }else{
                for(int i = initialPosition; i > finalPosition; i--){
                    Collections.swap(exercises, i, i-1);
                }
            }
            notifyItemMoved(initialPosition, finalPosition);
        }
    }

    public void onItemSwiped(final int position) {
        final Exercise e = exercises.get(position);

        Snackbar.make(activity.findViewById(R.id.copyTrainingLayout),
                activity.getString(R.string.delete_item, e.getName()), 2000)
                .setAction("Excluir", view -> {
                    Log.d(TAG, "Removed position: " + position);

                    new ExerciseViewModel(activity.getApplication())
                            .delete(e);

            }).addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                Log.d(TAG, "Returned position: " + position);
                notifyItemChanged(position);
            }
        }).show();
    }

    
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public List<Exercise> getExercises(){
        return exercises;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final TextView txtHeader;
        final TextView txtFooterLeft;
        final TextView txtFooterRight;
        public View layout;

        ViewHolder(View view) {
            super(view);
            layout = view;
            txtHeader = view.findViewById(R.id.tvTitle);
            txtFooterLeft = view.findViewById(R.id.tvDescriptionLeft);
            txtFooterRight = view.findViewById(R.id.tvDescriptionRight);
        }
    }
}
