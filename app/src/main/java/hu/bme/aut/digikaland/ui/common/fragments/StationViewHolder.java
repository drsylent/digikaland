package hu.bme.aut.digikaland.ui.common.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import hu.bme.aut.digikaland.R;

/**
 * Created by Sylent on 2018. 04. 04..
 */

public class StationViewHolder extends RecyclerView.ViewHolder{
    public TextView station;

    public StationViewHolder(View itemView){
        super(itemView);
        station = itemView.findViewById(R.id.clientStationItem);
    }
}
