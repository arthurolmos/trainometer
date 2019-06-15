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
import android.widget.Button;
import android.widget.TextView;

import com.arthurwosniaki.trainometer.HistoryExerciseActivity;
import com.arthurwosniaki.trainometer.R;
import com.arthurwosniaki.trainometer.entities.Training;
import com.arthurwosniaki.trainometer.database.DatabaseCallback;

import java.util.List;

public class HistoryTrainingAdapter extends RecyclerView.Adapter<HistoryTrainingAdapter.ViewHolder>{
    private String TAG = HistoryTrainingAdapter.class.getSimpleName();

    private long mLastClickTime = 0;

    private RecyclerView rvHistoryTraining;
    private List<Training> trainings;

    private Activity activity;
    private Context context;
    private DatabaseCallback callback;

    public HistoryTrainingAdapter(Activity activity, RecyclerView rvHistoryTraining, DatabaseCallback callback) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.callback = callback;

        this.rvHistoryTraining = rvHistoryTraining;
    }

    public void setTrainings(List<Training> t){
        trainings = t;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        // Create a new View
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.list_history_training, parent, false);

        v.setOnClickListener(view -> {
            // mis-clicking prevention, using threshold of 1000 ms
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

             int position = rvHistoryTraining.getChildAdapterPosition(view);

             Training t = trainings.get(position);

             Intent intent = new Intent(context, HistoryExerciseActivity.class);
             intent.putExtra("id_training", t.getId());intent.putExtra("name_training", t.getName());

             activity.startActivity(intent);
        });

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        final Training training = trainings.get(i);
        String name = training.getName();
        String desc = "Clique para ver o histórico.";

        String open = (training.isOpen()) ?  "Aberto" : "Fechado";

        viewHolder.tvTitle.setText(name);
        viewHolder.tvDescription.setText(desc);
        viewHolder.tvOpen.setText(open);
    }

    @Override
    public int getItemCount() {
        if(trainings != null)
            return trainings.size();
        else return 0;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTitle;
        final TextView tvDescription;
        final TextView tvOpen;
        final Button btnEdit;


        ViewHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvDescription = view.findViewById(R.id.tvDescription);
            tvOpen = view.findViewById(R.id.tvOpen);


            btnEdit = view.findViewById(R.id.btnEdit);
        }
    }
}
