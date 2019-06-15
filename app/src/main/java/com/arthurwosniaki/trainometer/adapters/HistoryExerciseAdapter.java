package com.arthurwosniaki.trainometer.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arthurwosniaki.trainometer.HistorySerieActivity;
import com.arthurwosniaki.trainometer.R;
import com.arthurwosniaki.trainometer.entities.Exercise;
import com.arthurwosniaki.trainometer.entities.pojos.ExerciseWithSeries;
import com.arthurwosniaki.trainometer.database.DatabaseCallback;

import java.util.List;

public class HistoryExerciseAdapter extends RecyclerView.Adapter<HistoryExerciseAdapter.ViewHolder>{

    private String TAG = HistoryExerciseAdapter.class.getSimpleName();

    private long mLastClickTime = 0;

    private RecyclerView rvHistoryExercises;

    private List<ExerciseWithSeries> exercises;

    private Activity activity;
    private Context context;
    private DatabaseCallback callback;


    public HistoryExerciseAdapter(Activity activity, RecyclerView rvHistoryExercises) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.rvHistoryExercises = rvHistoryExercises;
    }

    public HistoryExerciseAdapter(Activity activity, RecyclerView rvHistoryExercises, DatabaseCallback callback) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.rvHistoryExercises = rvHistoryExercises;

        this.callback = callback;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new View
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.list_history_exercise, parent, false);

        v.setOnClickListener(view -> {
            // mis-clicking prevention, using threshold of 1000 ms
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

                int position = rvHistoryExercises.getChildAdapterPosition(view);

                Exercise exercise = exercises.get(position).getExercise();

                Intent intent = new Intent(context, HistorySerieActivity.class);
                intent.putExtra("id_exercise", exercise.getId());
                intent.putExtra("id_training", exercise.getIdTraining());
                intent.putExtra("name_exercise", exercise.getName());
                intent.putExtra("serie_total", exercise.getSerieTotal());

                intent.putExtra("position", position);

            activity.startActivity(intent);
        });

        return new ViewHolder(v);
    }

    public void setExercises(List<ExerciseWithSeries> e){
        exercises = e;
        notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public void onBindViewHolder(@NonNull final HistoryExerciseAdapter.ViewHolder viewHolder, final int i) {

        Exercise exercise = exercises.get(i).getExercise();
        long id = exercise.getId();
        int seq = exercise.getSequence();
        String sequence = Integer.toString(seq);

        int serie = exercise.getSerieTotal();
        String name = exercise.getName();
        String counter = "Séries: " + serie;

        viewHolder.tvSeqExercise.setText(sequence);
        viewHolder.tvName.setText(name);
        viewHolder.tvSeries.setText(counter);
    }

    @Override
    public int getItemCount() {
        if(exercises != null)
            return exercises.size();
        else return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvSeqExercise;
        final TextView tvName;
        final TextView tvSeries;


        ViewHolder(View view) {
            super(view);

            tvSeqExercise = view.findViewById(R.id.tvSeqExercise);
            tvName = view.findViewById(R.id.tvName);
            tvSeries = view.findViewById(R.id.tvSeries);
        }
    }
}
