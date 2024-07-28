package com.example.brs;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SeatsAdapter extends BaseAdapter {

    private final Context context;
    private final String[] seats;
    private final String route;
    private final String time;
    private final int fare;
    private final String date;
    private final boolean isRoundTrip;

    private String cnic_ids, email_coll;

    public SeatsAdapter(Context context, String[] seats, String cnic_id, String email_colls, String route, String time, int fare, String date, boolean isRoundTrip) {
        this.context = context;
        this.seats = seats;
        this.cnic_ids = cnic_id;
        this.email_coll = email_colls;
        this.route = route;
        this.time = time;
        this.fare = fare;
        this.date = date;
        this.isRoundTrip = isRoundTrip;
    }

    @Override
    public int getCount() {
        return seats.length;
    }

    @Override
    public Object getItem(int position) {
        return seats[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.activity_gridseats, parent, false);
            holder = new ViewHolder();
            holder.textView = convertView.findViewById(R.id.gridViewSeats);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.textView.setText(seats[position]);

        // Set click listener for each seat
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Change color of the clicked seat
                holder.textView.setBackgroundColor(context.getResources().getColor(R.color.clicked_seat_color));

                // Start new activity
                Intent intent = new Intent(context, Personaldetails.class);
                intent.putExtra("seat", seats[position]);
                intent.putExtra("cnic_id", cnic_ids);
                intent.putExtra("email_coll", email_coll);
                intent.putExtra("route", route);
                intent.putExtra("time", time);
                intent.putExtra("fare", fare);
                intent.putExtra("date", date);
                intent.putExtra("isRoundTrip", isRoundTrip);
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    static class ViewHolder {
        TextView textView;
    }
}
