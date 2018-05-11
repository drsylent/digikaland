package hu.bme.aut.digikaland.ui.admin.common.fragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.entities.station.StationAdminPerspectiveTeam;
import hu.bme.aut.digikaland.entities.station.StationAdminPerspective;
import hu.bme.aut.digikaland.entities.station.StationAdminPerspectiveSummary;
import hu.bme.aut.digikaland.ui.common.fragments.StationViewHolder;

public class AdminStationAdapter extends RecyclerView.Adapter<StationViewHolder>  {
    private List<StationAdminPerspective> stations;
    private int colorNeutral;
    private int colorDone;
    private int colorNotStarted;
    private int colorStarted;
    private AdminStationListener activity;
    private boolean summaryMode;

    public AdminStationAdapter(List<StationAdminPerspective> s, boolean summary){
        stations = s;
        summaryMode = summary;
    }

    @Override
    public StationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        onAttach(parent.getContext());
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_simple, parent, false);
        colorNeutral = parent.getResources().getColor(R.color.colorPrimary);
        colorDone = parent.getResources().getColor(R.color.colorDone);
        colorNotStarted = parent.getResources().getColor(R.color.colorNot);
        colorStarted = parent.getResources().getColor(R.color.colorCurrently);
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
        if(summaryMode){
            final StationAdminPerspectiveSummary item = (StationAdminPerspectiveSummary) stations.get(position);
            holder.station.setText( ((Context) activity).getString(R.string.station_admin_list_item_summary, Integer.parseInt(item.station.id), item.evaluated, item.done, item.sum) );
            holder.station.setBackgroundColor(colorNeutral);
            holder.station.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.onStationClick(item);
                }
            });
        }
        else{
            final StationAdminPerspectiveTeam item = (StationAdminPerspectiveTeam) stations.get(position);
            holder.station.setText(((Context) activity).getString(R.string.station_admin_list_item, Integer.parseInt(item.station.id)));
            int color;
            switch (item.status) {
                case Evaluated:
                    color = colorDone;
                    holder.station.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            activity.onStationClick(item);
                        }
                    });
                    break;
                case Done:
                    color = colorStarted;
                    holder.station.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            activity.onStationClick(item);
                        }
                    });
                    break;
                case NotArrivedYet:
                    color = colorNotStarted;
                    break;
                default:
                    color = 0;
                    break;
            }
            holder.station.setBackgroundColor(color);
        }

    }

    @Override
    public int getItemCount() {
        return stations.size();
    }

    public interface AdminStationListener{
        void onStationClick(StationAdminPerspective station);
    }
}
