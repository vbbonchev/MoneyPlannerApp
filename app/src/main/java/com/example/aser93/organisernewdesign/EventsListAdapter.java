package com.example.aser93.organisernewdesign;


import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public final class EventsListAdapter extends ArrayAdapter<EventsListItem> {

    private final int eventsItemLayoutResource;

    //eventItemLayoutResource is the id of the layout used for the item
    public EventsListAdapter(final Context context, final int eventsItemLayoutResource) {
        super(context, 0);
        this.eventsItemLayoutResource = eventsItemLayoutResource;
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {

        // We need to get the best view (re-used if possible) and then
        // retrieve its corresponding ViewHolder, which optimizes lookup efficiency

        final View view = getWorkingView(convertView);
        final ViewHolder viewHolder = getViewHolder(view);
        final EventsListItem entry = getItem(position);

//         Setting the title view is straightforward
        Log.d("color", "entry color: " + entry.getColor());
        viewHolder.titleView.setBackgroundColor(Color.parseColor(entry.getColor()));
        viewHolder.titleView.getBackground().setAlpha(100);


//        parent.setBackgroundColor(Color.parseColor(entry.getColor()));
//        parent.getBackground().setAlpha(20);
//        viewHolder.descriptionView.setBackgroundColor(Color.parseColor(entry.getColor()));
//       int a= viewHolder.descriptionView.getParent();

        viewHolder.titleView.setText(entry.getTitle());
        viewHolder.descriptionView.setText(entry.getDescription());
        return view;
    }

//reuse View if possible of inflate new
    private View getWorkingView(final View convertView) {
        // The workingView is basically just the convertView re-used if possible
        // or inflated new if not possible
        View workingView = null;

        if(null == convertView) {
            final Context context = getContext();
            final LayoutInflater inflater = (LayoutInflater)context.getSystemService
                    (Context.LAYOUT_INFLATER_SERVICE);

            workingView = inflater.inflate(eventsItemLayoutResource, null);
        } else {
            workingView = convertView;
        }

        return workingView;
    }

    //gets a viewHolder from working view
    private ViewHolder getViewHolder(final View workingView) {
        final Object tag = workingView.getTag();
        ViewHolder viewHolder = null;


        if(null == tag || !(tag instanceof ViewHolder)) {
            viewHolder = new ViewHolder();

            viewHolder.titleView = (TextView) workingView.findViewById(R.id.eventListTitle);
            viewHolder.descriptionView = (TextView) workingView.findViewById(R.id.eventListDescription);

            workingView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) tag;
        }

        return viewHolder;
    }

//class to hold the Views
    private static class ViewHolder {
        public TextView titleView;
        public TextView descriptionView;

    }


}
