package com.arthurwosniaki.trainometer.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.arthurwosniaki.trainometer.ExecuteExerciseActivity;
import com.arthurwosniaki.trainometer.R;
import com.arthurwosniaki.trainometer.database.DatabaseCallback;
import com.arthurwosniaki.trainometer.entities.Exercise;
import com.arthurwosniaki.trainometer.entities.pojos.ExerciseWithSeries;

import java.util.List;
import java.util.stream.Collectors;

public class ExecuteTrainingAdapter extends RecyclerView.Adapter<ExecuteTrainingAdapter.ViewHolder>{
    private String TAG = ExecuteTrainingAdapter.class.getSimpleName();

    private long mLastClickTime = 0;

    private RecyclerView rvExercises;

    private long idExecution;
    private List<ExerciseWithSeries> exercises;

    private Activity activity;
    private Context context;
    private DatabaseCallback callback;


    public ExecuteTrainingAdapter(Activity activity, RecyclerView rvExercises) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.rvExercises = rvExercises;
    }

    public ExecuteTrainingAdapter(Activity activity, RecyclerView rvExercises, DatabaseCallback callback) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.rvExercises = rvExercises;

        this.callback = callback;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new View
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.list_execute_training_small, parent, false);

        v.setOnClickListener(view -> {
            // mis-clicking prevention, using threshold of 1000 ms
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

             int position = rvExercises.getChildAdapterPosition(view);

             Exercise exercise = exercises.get(position).getExercise();

             Intent intent = new Intent(context, ExecuteExerciseActivity.class);
             intent.putExtra("id_execution", idExecution);
             intent.putExtra("id_exercise", exercise.getId());
             intent.putExtra("id_training", exercise.getIdTraining());
             intent.putExtra("name_exercise", exercise.getName());
             intent.putExtra("serie_total", exercise.getSerieTotal());

             intent.putExtra("position", position);

            activity.startActivity(intent);
        });

        return new ViewHolder(v);
    }

    public void setIdExecution(long id){
        idExecution = id;
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
    public void onBindViewHolder(@NonNull final ExecuteTrainingAdapter.ViewHolder viewHolder, final int i) {

        Exercise exercise = exercises.get(i).getExercise();
        long id = exercise.getId();

        String sequence = Integer.toString(exercise.getSequence());
        viewHolder.tvSeqExercise.setText(sequence);


        viewHolder.tvName.setText(exercise.getName());


        int serie = exercise.getSerieTotal();
        int count = exercises.get(i).getSeries()
                .stream().filter(s -> s.getIdExecution() == idExecution).collect(Collectors.toList()).size();

        String counter = "Séries: " + count + " : " + serie;
        viewHolder.tvSeries.setText(counter);
        if(count == serie){
            viewHolder.tvSeries.setTextColor(ContextCompat.getColor(context, R.color.SeaGreen));
        }else{
            viewHolder.tvSeries.setTextColor(ContextCompat.getColor(context, R.color.IndianRed));
        }


        String reps = exercise.getRepsTotal();
        String r = "Reps: " + reps;
        viewHolder.tvReps.setText(r);
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
        final TextView tvReps;
        final TextView tvSeries;


        ViewHolder(View view) {
            super(view);

            tvSeqExercise = view.findViewById(R.id.tvSeqExercise);
            tvName = view.findViewById(R.id.tvName);
            tvReps = view.findViewById(R.id.tvReps);
            tvSeries = view.findViewById(R.id.tvSeries);
        }
    }
}
