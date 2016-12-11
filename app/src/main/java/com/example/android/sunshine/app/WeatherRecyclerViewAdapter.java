package com.example.android.sunshine.app;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class WeatherRecyclerViewAdapter extends RecyclerView.Adapter<WeatherRecyclerViewAdapter.CustomViewHolder> {
    private List<DateTimeItem> mFeedItemList;
    private Context mContext;
    // this class will bind the holder with the relevant data object that we want to edit during run time
    public WeatherRecyclerViewAdapter(Context context, List<DateTimeItem> feedItemList) {
        mFeedItemList = feedItemList;
        mContext = context;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        // change it according to the ppt
        View view;
        CustomViewHolder viewHolder;

        view = LayoutInflater.from(mContext).inflate(R.layout.list_item_forecast, null);
        viewHolder = new CustomViewHolder(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(CustomViewHolder customViewHolder, int position) {
        DateTimeItem feedItem = mFeedItemList.get(position);

        int iResourceId = feedItem.getResourceId();// T he data object return his resource id for img as we go by supporting methods in the data object class ...
        // will take default resource if ==0
        if (iResourceId == 0) {
            iResourceId = R.drawable.ic_launcher_sunshine;
            //Render image using Picasso library
//            Picasso.with(mContext).load( iResourceId)
//                    .error(R.drawable.ic_launcher_sunshine)
//                    .placeholder(R.drawable.ic_launcher_sunshine)
//                    .into(customViewHolder.imageView);

        }
        customViewHolder.imageView.setImageResource(iResourceId);

        //Setting text view title
        customViewHolder.textView.setText(feedItem.getTitle());
    }

    @Override
    public int getItemCount() {
        return (null != mFeedItemList ? mFeedItemList.size() : 0);
    }

    class CustomViewHolder extends RecyclerView.ViewHolder {
        protected ImageView imageView;
        protected TextView textView;
        // the holder takes the data from the view into a holder object that contains the view for each one of the list item (row ) elements
        public CustomViewHolder(View view) {
            super(view);
            this.imageView = (ImageView) view.findViewById(R.id.thumbnail);
            this.textView = (TextView) view.findViewById(R.id.list_item_forecast_textview);
        }
    }
}