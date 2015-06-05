package com.melodicloud.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.melodicloud.R;
import com.melodicloud.common.Global;
import com.melodicloud.util.LogUtil;

import java.text.DecimalFormat;
import java.util.ArrayList;

import de.voidplus.soundcloud.Track;

/**
 * Created by Salem on 26-Apr-15.
 */
public class SongsAdapter extends RecyclerView.Adapter<SongsAdapter.ViewHolder> implements Filterable {


    private ArrayList<Track> mDataset;

    private ArrayList<Track> mOriginalDataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    public SongsAdapter(ArrayList<Track> myDataset) {
        mOriginalDataset = myDataset;
        mDataset = (ArrayList<Track>) mOriginalDataset.clone();
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtSongName;
        public TextView txtDuration;
        public ImageView imgThumb;
        public ImageView imgState;

        public ViewHolder(View v) {
            super(v);
            txtSongName = (TextView) v.findViewById(R.id.title);
            txtDuration = (TextView) v.findViewById(R.id.duration);
            imgThumb = (ImageView) v.findViewById(R.id.thumb);
            imgState = (ImageView) v.findViewById(R.id.state);
        }

        public void build(Track track) {
            txtSongName.setText(track.getTitle());

            int secs = track.getDuration() / 1000;

            int mins = secs / 60;
            secs = secs - mins * 60;

            txtDuration.setText(mins + ":" + secs);

            Glide.with(Global.appContext).load(track.getArtworkUrl()).into(imgThumb);

            //track.getStreamUrl()
        }
    }

    @Override
    public Filter getFilter() {
        return new SongsFilter();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public SongsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_song, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final Track track = mDataset.get(position);

        holder.build(track);

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public Track getItem(int position) {
        return mDataset.get(position);
    }

    class SongsFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            ArrayList<Track> filteredTracks = new ArrayList<>();

            // perform your search here using the searchConstraint String.

            constraint = constraint.toString().toLowerCase();
            for (int i = 0; i < mOriginalDataset.size(); i++) {
                Track track = mOriginalDataset.get(i);
                if (track.getTitle().toLowerCase().startsWith(constraint.toString())) {
                    filteredTracks.add(track);
                }
            }

            results.count = filteredTracks.size();
            results.values = filteredTracks;
            LogUtil.e("VALUES", results.values.toString());

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            mDataset = (ArrayList<Track>) results.values;
            notifyDataSetChanged();
        }
    }
}
