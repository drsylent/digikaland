package hu.bme.aut.digikaland.ui.client.fragments;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.entities.Station;

public class ClientStationAdapter extends RecyclerView.Adapter<ClientStationAdapter.ClientStationViewHolder> {
    private List<Station> stations;
    private int colorDone;
    private int colorNotStarted;
    private int colorStarted;

    public ClientStationAdapter(List<Station> s){
        stations = s;
    }

    @Override
    public ClientStationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_client_station, parent, false);
        colorDone = parent.getResources().getColor(R.color.colorDone);
        colorNotStarted = parent.getResources().getColor(R.color.colorNot);
        colorStarted = parent.getResources().getColor(R.color.colorCurrently);
        return new ClientStationViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ClientStationViewHolder holder, int position) {
        final Station item = stations.get(position);
        holder.station.setText(item.number + ". állomás (" + item.id + " id)");
        int color;
        switch (item.status){
            case Done:
                color = colorDone;
                break;
            case Started:
                color = colorStarted;
                // TODO: feladat activityre lépés
                break;
            case NotStarted:
                color = colorNotStarted;
                break;
            default:
                color = 0;
                break;
        }
        holder.station.setBackgroundColor(color);
    }

    @Override
    public int getItemCount() {
        return stations.size();
    }

    class ClientStationViewHolder extends RecyclerView.ViewHolder{
        TextView station;

        ClientStationViewHolder(View itemView){
            super(itemView);
            station = itemView.findViewById(R.id.clientStationItem);
        }
    }
}
