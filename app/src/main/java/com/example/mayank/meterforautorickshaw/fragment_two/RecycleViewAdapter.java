package com.example.mayank.meterforautorickshaw.fragment_two;


import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mayank.meterforautorickshaw.R;

import java.util.List;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.RidesViewHolder> {

    public static class RidesViewHolder extends RecyclerView.ViewHolder{

        CardView cv ;
        TextView pickupAndDestination ;
        TextView fare ;
        ImageView auto_photo ;

        RidesViewHolder(View itemView){

            super(itemView) ;
            cv = (CardView) itemView.findViewById(R.id.cv) ;
            pickupAndDestination = (TextView) itemView.findViewById(R.id.pickupAndDestination) ;
            fare = (TextView) itemView.findViewById(R.id.fare) ;
            auto_photo = (ImageView) itemView.findViewById(R.id.auto_photo) ;

        }

    }

    List<Rides> rides ;

    public RecycleViewAdapter(List<Rides> rides){
        this.rides = rides ;
    }


    @Override
    public RecycleViewAdapter.RidesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.previous_rides_cardview, parent, false);
        RidesViewHolder rvh = new RidesViewHolder(v);
        return rvh;
    }

    @Override
    public void onBindViewHolder(RecycleViewAdapter.RidesViewHolder holder, int position) {

        holder.pickupAndDestination.setText(rides.get(position).pickupAndDestination);
        holder.fare.setText(rides.get(position).fare);
        holder.auto_photo.setImageResource(rides.get(position).photo_id);

    }

    @Override
    public int getItemCount() {
        return rides.size() ;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}
