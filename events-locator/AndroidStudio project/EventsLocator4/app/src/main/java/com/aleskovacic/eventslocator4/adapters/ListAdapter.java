package com.aleskovacic.eventslocator4.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aleskovacic.eventslocator4.R;
import com.aleskovacic.eventslocator4.pojo.DataObject;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ListItemViewHolder> {

    private static ClickListener clickListener;
    private static LayoutInflater inflater;
    private Context context;
    private List<DataObject> data;

    public ListAdapter(Context context, List<DataObject> data) {
        inflater = LayoutInflater.from(context);
        this.data = data;
        this.context = context;
    }

    public void newData(List<DataObject> data) {
        this.data.clear();
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public DataObject getItem(int position) {
        return data.get(position);
    }

    @Override
    public ListItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ListItemViewHolder(inflater.inflate(R.layout.list_row, parent, false));
    }


    @Override
    public void onBindViewHolder(ListItemViewHolder holder, int position) {
        DataObject object = data.get(position);
        Picasso.with(context).load(object.getCoverPhoto())
                .placeholder(R.drawable.ic_image_black_48dp)
                .error(R.drawable.ic_broken_image_black_48dp)
                .into(holder.coverPhoto);

        holder.title.setText(object.getTitle());
        holder.startTime.setText(object.getStartDate());
        holder.place.setText(object.getPlaceName());
        holder.description.setText(object.getDescription());

    }

    public void setClickListener(ClickListener clickListener) {
        this.clickListener = clickListener;
    }

    @Override
    public int getItemCount() {
        if (data != null) {
            return data.size();
        }
        return 0;
    }

    public interface ClickListener {
        void itemClicked(View view, int position);
    }


    static class ListItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView coverPhoto;
        TextView title;
        TextView startTime;
        TextView place;
        TextView description;

        public ListItemViewHolder(View itemView) {
            super(itemView);
            coverPhoto = (ImageView) itemView.findViewById(R.id.cover_photo);
            title = (TextView) itemView.findViewById(R.id.title);
            startTime = (TextView) itemView.findViewById(R.id.start_time);
            place = (TextView) itemView.findViewById(R.id.place);
            description = (TextView) itemView.findViewById(R.id.description);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (clickListener != null) {
                clickListener.itemClicked(v, getAdapterPosition());
            }
        }
    }
}
