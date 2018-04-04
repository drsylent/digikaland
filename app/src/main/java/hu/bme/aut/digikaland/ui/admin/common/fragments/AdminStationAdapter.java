package hu.bme.aut.digikaland.ui.admin.common.fragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.entities.StationAdminPerspective;
import hu.bme.aut.digikaland.ui.common.fragments.StationViewHolder;

public class AdminStationAdapter extends RecyclerView.Adapter<StationViewHolder>  {
    private List<StationAdminPerspective> stations;
    private int color;
    private AdminStationListener activity;

    public AdminStationAdapter(List<StationAdminPerspective> s){
        stations = s;
    }

    @Override
    public StationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        onAttach(parent.getContext());
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_client_station, parent, false);
        color = parent.getResources().getColor(R.color.colorPrimary);
        return new StationViewHolder(itemView);
    }

    private void onAttach(Context context) {
        if (context instanceof AdminStationListener) {
            activity = (AdminStationListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement AdminStationListener");
        }
    }

    @Override
    public void onBindViewHolder(StationViewHolder holder, int position) {
        final StationAdminPerspective item = stations.get(position);
        holder.station.setText( ((Context) activity).getString(R.string.station_admin_list_item, item.station.id, item.evaluated, item.done, item.sum) );
        holder.station.setBackgroundColor(color);
        holder.station.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.onStationClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return stations.size();
    }

    public interface AdminStationListener{
        void onStationClick(StationAdminPerspective station);
    }
}
