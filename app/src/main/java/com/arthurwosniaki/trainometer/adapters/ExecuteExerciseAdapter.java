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
import com.arthurwosniaki.trainometer.database.viewmodels.SerieViewModel;
import com.arthurwosniaki.trainometer.entities.Serie;
import com.arthurwosniaki.trainometer.utils.AlertDialogUtil;
import com.arthurwosniaki.trainometer.utils.Converters;

import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import static java.lang.String.format;

public class ExecuteExerciseAdapter extends RecyclerView.Adapter<ExecuteExerciseAdapter.ViewHolder>{
    private String TAG = ExecuteExerciseAdapter.class.getSimpleName();

    private long mLastClickTime = 0;

    private List<Serie> series;

    private RecyclerView rvExecuteExercise;

    private Activity activity;
    private Context context;
    private DatabaseCallback callback;


    public ExecuteExerciseAdapter(Activity activity, RecyclerView recyclerView, DatabaseCallback callback) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.callback = callback;

        rvExecuteExercise = recyclerView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new View
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.list_execute_exercise, parent, false);

        v.setOnClickListener(view -> {
            //Mis-clicking prevention, using threshold of 1000 ms
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            final int position = rvExecuteExercise.getChildAdapterPosition(view);
            Serie s = series.get(position);
            openEditDialog(s);
        });

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final Serie serie = series.get(i);

        String sequence = format(Locale.US, "%02d", i+1) + ":";
        String weight = context.getResources().getString(R.string.weight) + ": " + serie.getWeight();
        String reps = context.getResources().getString(R.string.reps) + ": "  + serie.getReps();

        String comment = context.getResources().getString(R.string.comments) + ": " + serie.getComment();

        viewHolder.tvSequence.setText(sequence);
        viewHolder.tvWeight.setText(weight);
        viewHolder.tvReps.setText(reps);
        viewHolder.tvComment.setText(comment);
    }

    public void setSeries(List<Serie> s){
        series = s;
        notifyDataSetChanged();
    }

    private void openEditDialog(Serie serie){
        AtomicBoolean modified = new AtomicBoolean();
        modified.set(false);

        AlertDialog.Builder builder = new AlertDialogUtil(activity)
                .createEditDialog("Editar Série");

        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_edit_serie, null);

        EditText etWeight = view.findViewById(R.id.etWeight);
        String weight = Float.toString(serie.getWeight());
        etWeight.setText(weight);
        etWeight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                modified.set(true);

                if(!etWeight.getText().toString().equals("")){
                    String w = etWeight.getText().toString();
                    float weight = Float.parseFloat(w);
                    weight = Converters.roundDecimals(weight);

                    serie.setWeight(weight);
                }
            }
        });


        EditText etReps = view.findViewById(R.id.etReps);
        String reps = Integer.toString(serie.getReps());
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
                    int reps= Integer.parseInt(etReps.getText().toString());
                    serie.setReps(reps);
                }
            }
        });


        EditText etComment = view.findViewById(R.id.etComment);
        etComment.setText(serie.getComment());
        etComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                modified.set(true);

                serie.setComment(etComment.getText().toString());
            }
        });

        builder.setView(view)
                .setPositiveButton("OK", (positive, which) -> {
                    if(modified.get()){
                        new SerieViewModel(activity.getApplication(), callback)
                                .update(serie);
                    }
                })
                .setNegativeButton("Cancelar", (negative, which) -> {

                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void onItemSwipedRight(final int position) {
        Snackbar.make(activity.findViewById(R.id.ExecuteExerciseLayout), "Confirma a exclusão? Aguarde pare desfazer.", 2000)
                .setAction("Excluir", view -> {
                        Log.d(TAG, "Exclusion.");

                        Serie s = series.get(position);
                        new SerieViewModel(activity.getApplication(), callback).delete(s);

                }).addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
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

    @Override
    public int getItemCount() {
        if(series != null)
            return series.size();
        else return 0;
    }

    public void onItemMove(int initialPosition, int finalPosition) {
        if(initialPosition < getItemCount() && finalPosition < getItemCount()){
            if(initialPosition < finalPosition){
                for(int i = initialPosition; i < finalPosition; i++){
                    Collections.swap(series, i, i+1);
                }
            }else{
                for(int i = initialPosition; i > finalPosition; i--){
                    Collections.swap(series, i, i-1);
                }
            }
            notifyItemMoved(initialPosition, finalPosition);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvSequence;
        final TextView tvWeight;
        final TextView tvReps;
        final TextView tvComment;

        ViewHolder(View view) {
            super(view);

            tvSequence = view.findViewById(R.id.tvSequence);
            tvWeight = view.findViewById(R.id.tvWeight);
            tvReps = view.findViewById(R.id.tvReps);
            tvComment = view.findViewById(R.id.tvComment);
        }
    }
}
