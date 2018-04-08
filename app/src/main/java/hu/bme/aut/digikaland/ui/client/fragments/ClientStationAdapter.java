package hu.bme.aut.digikaland.ui.client.fragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.entities.station.StationClientPerspective;
import hu.bme.aut.digikaland.ui.common.fragments.StationViewHolder;

public class ClientStationAdapter extends RecyclerView.Adapter<StationViewHolder> {
    private List<StationClientPerspective> stations;
    private int colorDone;
    private int colorNotStarted;
    private int colorStarted;
    private int colorWhite;
    private ClientStationListener activity;

    public ClientStationAdapter(List<StationClientPerspective> s){
        stations = s;
    }

    @Override
    public StationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        onAttach(parent.getContext());
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_simple, parent, false);
        colorDone = parent.getResources().getColor(R.color.colorPrimaryDark);
        colorWhite = parent.getResources().getColor(R.color.colorWhite);
        colorNotStarted = parent.getResources().getColor(R.color.colorNot);
        colorStarted = parent.getResources().getColor(R.color.colorCurrently);
        return new StationViewHolder(itemView);
    }

    private void onAttach(Context context) {
        if (context instanceof ClientStationListener) {
            activity = (ClientStationListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ClientStationListener");
        }
    }

    @Override
    public void onBindViewHolder(StationViewHolder holder, int position) {
        final StationClientPerspective item = stations.get(position);
        holder.station.setText( ((Context) activity).getString(R.string.station_show, item.station.number, item.station.oldId) );
        int color;
        switch (item.status){
            case Done:
                color = colorDone;
                holder.station.setTextColor(colorWhite);
                break;
            case Started:
                color = colorStarted;
                holder.station.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        activity.onStartedStationClick(item);
                    }
                });
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

    public interface ClientStationListener{
        void onStartedStationClick(StationClientPerspective station);
    }
}
