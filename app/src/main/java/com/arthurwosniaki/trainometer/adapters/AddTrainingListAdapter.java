package com.arthurwosniaki.trainometer.adapters;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arthurwosniaki.trainometer.R;
import com.arthurwosniaki.trainometer.entities.Exercise;

import java.util.ArrayList;
import java.util.Collections;

public class AddTrainingListAdapter extends RecyclerView.Adapter<AddTrainingListAdapter.ViewHolder>{
    private String TAG = AddTrainingListAdapter.class.getSimpleName();

    private ArrayList<Exercise> exercises;
    private Activity activity;


    public AddTrainingListAdapter(Activity activity, ArrayList<Exercise> exercises) {
        this.activity = activity;
        this.exercises = exercises;
    }

    public void add(Exercise exercise) {
        exercises.add(exercise);
        notifyItemInserted(getItemCount());

        Log.v(TAG, "Added successfully");
    }

    private void remove(int position) {
        exercises.remove(position);
        notifyItemRemoved(position);

        Log.v(TAG, "Removed successfully");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new View
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.list_add_training, parent, false);

        return new ViewHolder(v);
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
        return exercises.size();
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

        Snackbar.make(activity.findViewById(R.id.addTrainingLayout),
                activity.getString(R.string.delete_item, e.getName()), 2000)
                .setAction("Excluir", view-> {
                        Log.d(TAG, "Removed position: " + position);

                        remove(position);

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

    public ArrayList<Exercise> getExercises(){
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
