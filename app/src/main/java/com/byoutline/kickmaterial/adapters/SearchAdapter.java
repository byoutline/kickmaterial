package com.byoutline.kickmaterial.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.byoutline.kickmaterial.R;
import com.byoutline.kickmaterial.model.Project;
import com.byoutline.secretsauce.utils.ViewUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays search results
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private final List<Project> dataset = new ArrayList<>();
    private Context context;
    private ProjectClickListener projectClickListener;

    public SearchAdapter(Context context, ProjectClickListener projectClickListener) {
        this.context = context;
        this.projectClickListener = projectClickListener;
    }

    public Project getItem(int position) {
        return dataset.get(position);
    }

    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // Create a new view by inflating the row item xml.
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_item, parent, false);
        ViewHolder holder = new ViewHolder(v, projectClickListener);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Project project = getItem(position);
        if (project != null) {
            ViewUtils.setText(holder.titleTv, project.getProjectName());
            ViewUtils.setText(holder.descTv, project.desc);

            Picasso.with(context).load(project.getPhotoUrl()).into(holder.photoIv);
        }

    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public void setItems(List<Project> items) {
        synchronized (dataset) {
            dataset.clear();
            dataset.addAll(items);
            notifyDataSetChanged();
        }
    }

    public void clear() {
        synchronized (dataset) {
            dataset.clear();
            notifyDataSetChanged();
        }
    }


    // Create the ViewHolder class to keep references to your views
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ProjectClickListener listener;

        ImageView photoIv;
        TextView titleTv;
        TextView descTv;

        /**
         * @param v The container view which holds the elements from the row item xml
         */
        public ViewHolder(View v, ProjectClickListener projectClickListener) {
            super(v);
            this.listener = projectClickListener;
            photoIv = (ImageView) v.findViewById(R.id.search_item_photo_iv);
            titleTv = (TextView) v.findViewById(R.id.search_item_title_tv);
            descTv = (TextView) v.findViewById(R.id.search_item_desc_tv);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (listener != null) {
                SharedViews views = new SharedViews(photoIv, titleTv);
                listener.projectClicked(getAdapterPosition(), views);
            }
        }
    }
}