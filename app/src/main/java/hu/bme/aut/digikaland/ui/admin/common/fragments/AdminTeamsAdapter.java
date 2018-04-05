package hu.bme.aut.digikaland.ui.admin.common.fragments;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.entities.Team;
import hu.bme.aut.digikaland.ui.common.fragments.StationViewHolder;

public class AdminTeamsAdapter extends RecyclerView.Adapter<StationViewHolder> {
    private List<Team> teams;
    private int colorNeutral;
    private int colorDone;
    private int colorNotStarted;
    private int colorStarted;
    private AdminTeamsListener activity;
    private boolean summaryMode;

    public AdminTeamsAdapter(List<Team> s, boolean summary) {
        teams = s;
        summaryMode = summary;
    }

    @Override
    public StationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        onAttach(parent.getContext());
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_simple, parent, false);
        colorDone = parent.getResources().getColor(R.color.colorDone);
        colorNotStarted = parent.getResources().getColor(R.color.colorNot);
        colorStarted = parent.getResources().getColor(R.color.colorCurrently);
        colorNeutral = parent.getResources().getColor(R.color.colorPrimary);
        return new StationViewHolder(itemView);
    }

    private void onAttach(Context context) {
        if (context instanceof AdminTeamsListener) {
            activity = (AdminTeamsListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement AdminTeamsListener");
        }
    }

    @Override
    public void onBindViewHolder(StationViewHolder holder, int position) {
        final Team item = teams.get(position);
        holder.station.setText(item.name);
        if(summaryMode){
            holder.station.setBackgroundColor(colorNeutral);
            holder.station.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity.onTeamClicked(item);
                }
            });
        }
        else{
            int color;
            switch (item.status) {
                case Evaluated:
                    color = colorDone;
                    holder.station.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            activity.onTeamClicked(item);
                        }
                    });
                    break;
                case Done:
                    color = colorStarted;
                    holder.station.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            activity.onTeamClicked(item);
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
        return teams.size();
    }

    public interface AdminTeamsListener {
        void onTeamClicked(Team team);
    }
}
