package com.udacity.josep.udacityproject1;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by jple on 03/09/16.
 *
 */

public class SimpleRVAdapter extends RecyclerView.Adapter<SimpleRVAdapter.SimpleViewHolder> {
    private List<DownloadingTask.Trailer> trailers;
    private List<String> dataSource;
    private AdapterView.OnItemClickListener onItemClickListener;


    public SimpleRVAdapter(List<String> dataArgs){
        dataSource = dataArgs;
    }

    public SimpleRVAdapter(List<DownloadingTask.Trailer> dataArgs, List<String> dataSource){
        this.trailers = dataArgs;
        this.dataSource = dataSource;
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        // create a new view
        View view = new TextView(parent.getContext());
        final SimpleViewHolder viewHolder = new SimpleViewHolder(view);
        if(trailers!=null && trailers.size()>0) {
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final int position = viewHolder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        parent.getContext().startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(trailers.get(position).getUrl())));
                    }

                }
            });
        }
        return viewHolder;
    }

    public static class SimpleViewHolder extends RecyclerView.ViewHolder{
        public TextView textView;
        public SimpleViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView;
        }
    }

    public void setOnItemClickListener(AdapterView.OnItemClickListener listener){
        onItemClickListener = listener;
    }

    @Override
    public void onBindViewHolder(SimpleViewHolder holder, int position) {
        holder.textView.setText(dataSource.get(position));
    }

    @Override
    public int getItemCount() {
        return dataSource.size();
    }
}