package com.arthurwosniaki.trainometer.adapters;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arthurwosniaki.trainometer.R;
import com.arthurwosniaki.trainometer.entities.pojos.ExerciseHistory;
import com.arthurwosniaki.trainometer.database.DatabaseCallback;

import java.util.List;

public class HistorySerieAdapter extends RecyclerView.Adapter<HistorySerieAdapter.ViewHolder>{
    private String TAG = HistorySerieAdapter.class.getSimpleName();

    private long serieTotal;

    private List<ExerciseHistory> histories;
    private Activity activity;
    private Context context;
    private DatabaseCallback callback;


    public HistorySerieAdapter(Activity activity, DatabaseCallback callback) {
        this.activity = activity;
        this.context = activity.getApplicationContext();

        this.callback = callback;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a new View
        LayoutInflater inflater = LayoutInflater.from(
                parent.getContext());
        View v = inflater.inflate(R.layout.list_history_serie, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
        ExerciseHistory h = histories.get(i);

        viewHolder.tvDate.setText(h.getDate());

        //Creating the Weight & Reps section
        for(int counter = 0; counter < serieTotal; counter++){
            LinearLayout layout = new LinearLayout(context);
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setGravity(View.TEXT_ALIGNMENT_CENTER);

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(8, 8, 8, 8);
            layout.setLayoutParams(lp);

            if(h.getReps().size() > counter){
                setFields(layout, h.getReps().get(counter));
            }else{
                setFields(layout, "");
            }

            if(h.getWeight().size() > counter){
                setFields(layout, h.getWeight().get(counter));
            }else{
                setFields(layout, "");
            }

            viewHolder.linearLayout.addView(layout);
        }

        //Creating the Comments section
        if(h.getComments().size() == 0){
            viewHolder.bottomLayout.setVisibility(View.GONE);

        }else{
            //Add OnClick event only if Comments is not empty
            viewHolder.bottomLayout.setOnClickListener(v -> {
                if(viewHolder.commentsLayout.getVisibility() == View.VISIBLE){
                    viewHolder.commentsLayout.setVisibility(View.GONE);
                    viewHolder.imgArrowDown.setVisibility(View.GONE);
                    viewHolder.imgArrowLeft.setVisibility(View.VISIBLE);
                }else{
                    viewHolder.commentsLayout.setVisibility(View.VISIBLE);
                    viewHolder.imgArrowDown.setVisibility(View.VISIBLE);
                    viewHolder.imgArrowLeft.setVisibility(View.GONE);
                }
            });

            for(int counter = 0; counter < h.getComments().size(); counter++) {
                if(h.getComments().size() > counter){
                    if(!h.getComments().get(counter).equals("")){
                        setComments(viewHolder.commentsLayout, h.getComments().get(counter), counter);
                    }
                }
            }
        }
    }


    private void setFields(LinearLayout layout, String s) {
            TextView textView = new TextView(context);
            textView.setText(s);

            LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            tvParams.setMargins(8, 8, 8, 8);
            textView.setLayoutParams(tvParams);

            layout.addView(textView);
    }

    private void setComments(LinearLayout layout, String s, int counter) {
        String c = String.format("%02d", counter+1);
        String text = c + ": " + s;

        TextView textView = new TextView(context);
        textView.setText(text);

        LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        tvParams.setMargins(8, 8, 8, 8);
        textView.setLayoutParams(tvParams);

        layout.addView(textView);
    }

    public void setHistories(List<ExerciseHistory> h){
        histories = h;
        notifyDataSetChanged();
    }

    public void setSerieTotal(long s){
        serieTotal = s;
    }


    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        if(histories != null)
            return histories.size();
        else return 0;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvDate;

        final ImageView imgArrowDown;
        final ImageView imgArrowLeft;

        final LinearLayout bottomLayout;
        final LinearLayout linearLayout;
        final LinearLayout commentsLayout;

        ViewHolder(View view) {
            super(view);

            imgArrowDown = view.findViewById(R.id.imgArrowDown);
            imgArrowLeft = view.findViewById(R.id.imgArrowLeft);

            tvDate = view.findViewById(R.id.tvDate);

            bottomLayout = view.findViewById(R.id.bottomLayout);
            linearLayout = view.findViewById(R.id.linearLayout);
            commentsLayout = view.findViewById(R.id.commentsLayout);
        }
    }
}
