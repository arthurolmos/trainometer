package com.arthurwosniaki.trainometer.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.arthurwosniaki.trainometer.CopyTrainingActivity;
import com.arthurwosniaki.trainometer.EditTrainingActivity;
import com.arthurwosniaki.trainometer.ExecuteTrainingActivity;
import com.arthurwosniaki.trainometer.R;
import com.arthurwosniaki.trainometer.database.DatabaseCallback;
import com.arthurwosniaki.trainometer.database.viewmodels.ExecutionViewModel;
import com.arthurwosniaki.trainometer.database.viewmodels.TrainingViewModel;
import com.arthurwosniaki.trainometer.entities.Execution;
import com.arthurwosniaki.trainometer.entities.Training;
import com.arthurwosniaki.trainometer.entities.pojos.TrainingWithExecutions;
import com.arthurwosniaki.trainometer.utils.AlertDialogUtil;
import com.arthurwosniaki.trainometer.utils.Converters;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class MainMenuAdapter extends RecyclerView.Adapter<MainMenuAdapter.ViewHolder>{
    private String TAG = MainMenuAdapter.class.getSimpleName();

    private long mLastClickTime = 0;

    private RecyclerView rvMainMenu;
    private List<TrainingWithExecutions> trainings;

    private Activity activity;
    private Context context;
    private DatabaseCallback callback;

    public MainMenuAdapter(Activity activity, RecyclerView rvMainMenu, DatabaseCallback callback) {
        this.activity = activity;
        this.context = activity.getApplicationContext();
        this.callback = callback;

        this.rvMainMenu = rvMainMenu;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        // Create a new View
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.list_main_menu, parent, false);


        //OnClick of View
        v.setOnClickListener(view -> {
            //Mis-clicking prevention, using threshold of 1000 ms
            if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();

            final int position = rvMainMenu.getChildAdapterPosition(view);

            TrainingWithExecutions t = trainings.get(position);
            if(t.getOpenExecution() != null){
                Log.d(TAG, "Training with Execution.");
                openTraining(t, ExecuteTrainingActivity.class);

            } else{
                AlertDialog.Builder dialog = new AlertDialogUtil(activity)
                        .createAlertDialog("INICIAR", "Deseja iniciar o treino?");
                dialog.setPositiveButton("OK", (d, which) -> {
                    Log.d(TAG, "Creating new Execution...");

                    LocalDate date = LocalDate.now();
                    new ExecutionViewModel(activity.getApplication(), callback).insert(new Execution(t.getTraining().getId(), date, true));

                    openTraining(t, ExecuteTrainingActivity.class);

                }).setNegativeButton("Cancelar", (d, which)->{

                });
                dialog.show();
            }
        });

        return new ViewHolder(v);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        final TrainingWithExecutions t = trainings.get(i);

        //Training Name
        String name = t.getTraining().getName();
        viewHolder.txtHeader.setText(name);

        //Open Execution
        String desc = "";
        if(t.getExecutions().size() > 0) {
            Log.d(TAG, "Executions found... Filtering for open Execution");

            List<Execution> executions = t.getExecutions().stream()
                    .filter(Execution::isOpen)
                    .collect(Collectors.toList());

            if (executions.size() > 0) {
                Log.d(TAG, "Execution ID found: " + executions.get(0));

                t.setOpenExecution(executions.get(0));
                LocalDate date = t.getOpenExecution().getDateStart();
                String d = Converters.localDateToString(date);

                desc = "Execução em andamento iniciada em " + d + ".";

            } else {
                Log.d(TAG, "Open Execution not found!");
            }
        }
        viewHolder.txtFooter.setText(desc);

        //Edit Button
        viewHolder.btnEdit.setOnClickListener(view -> {
            openTraining(t, EditTrainingActivity.class);
        });

        //Copy Button
        viewHolder.btnCopy.setOnClickListener(view -> {
            openTraining(t, CopyTrainingActivity.class);
        });
    }

    private void openTraining(TrainingWithExecutions t, Class nextActivity){
        Intent intent = new Intent(context, nextActivity);
        intent.putExtra("id_training", t.getTraining().getId());

        activity.startActivity(intent);
    }


    public void setTrainings(List<TrainingWithExecutions> t){
        trainings = t;
        notifyDataSetChanged();
    }

    public void onItemSwipedRight(final int position) {
        Training t = trainings.get(position).getTraining();

        Snackbar.make(activity.findViewById(R.id.MainMenuLayout),
                activity.getString(R.string.delete_item, t.getName()), 2000)
                .setAction(R.string.delete, view -> {
                    Log.d(TAG, "Exclusion.");

                    new TrainingViewModel(activity.getApplication(), callback).delete(t);

                }).addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                notifyItemChanged(position);
            }
        }).show();
    }

    public void onItemSwipedLeft(final int position) {
        Training t = trainings.get(position).getTraining();

        Snackbar.make(activity.findViewById(R.id.MainMenuLayout),
                activity.getString(R.string.archive_item, t.getName()), 2000)
                .setAction(R.string.archive, view -> {
                    Log.d(TAG, "Update Closed Status");

                    t.setOpen(false);

                    new TrainingViewModel(activity.getApplication(), callback).update(t);

                }).addCallback(new BaseTransientBottomBar.BaseCallback<Snackbar>() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                super.onDismissed(transientBottomBar, event);
                notifyItemChanged(position);
            }
        }).show();
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
        final TextView txtHeader;
        final TextView txtFooter;
        final ImageButton btnEdit;
        final ImageButton btnCopy;


        ViewHolder(View view) {
            super(view);
            txtHeader = view.findViewById(R.id.tvTitle);
            txtFooter = view.findViewById(R.id.tvDescription);

            btnEdit = view.findViewById(R.id.btnEdit);
            btnCopy = view.findViewById(R.id.btnCopy);
        }
    }
}
