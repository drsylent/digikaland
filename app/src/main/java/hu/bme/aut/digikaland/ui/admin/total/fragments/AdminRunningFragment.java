package hu.bme.aut.digikaland.ui.admin.total.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.entities.EvaluationStatistics;
import hu.bme.aut.digikaland.entities.Location;

public class AdminRunningFragment extends Fragment {
    private static final String ARG_LOCATION = "loc";
    private static final String ARG_TIME = "time";
    private static final String ARG_STATISTICS = "stats";

    private Location location;
    private EvaluationStatistics statistics;
    private Date time;

    private AdminRunningListener totalAdmin;

    public AdminRunningFragment() {
        // Required empty public constructor
    }

    public static AdminRunningFragment newInstance(Location location, Date time, EvaluationStatistics stats) {
        AdminRunningFragment fragment = new AdminRunningFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_LOCATION, location);
        args.putSerializable(ARG_STATISTICS, stats);
        args.putSerializable(ARG_TIME, time);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            location = (Location) getArguments().getSerializable(ARG_LOCATION);
            time = (Date) getArguments().getSerializable(ARG_TIME);
            statistics = (EvaluationStatistics) getArguments().getSerializable(ARG_STATISTICS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_admin_running, container, false);
        TextView tvLocation = root.findViewById(R.id.adminRunningEndLocation);
        tvLocation.setText(location.main);
        TextView tvSubLocation = root.findViewById(R.id.adminRunningEndSubLocation);
        tvSubLocation.setText(location.detailed);
        TextView tvTime = root.findViewById(R.id.adminRunningEndTime);
        tvTime.setText(getResources().getString(R.string.date, time));
        TextView tvStatus = root.findViewById(R.id.adminRunningStatus);
        tvStatus.setText(getResources().getString(R.string.tri_status, statistics.evaluated, statistics.done, statistics.all));
        Button bEnd = root.findViewById(R.id.adminRunningEndButton);
        bEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalAdmin.onEndPressed();
            }
        });
        Button bHelp = root.findViewById(R.id.adminRunningHelp);
        bHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalAdmin.onHelpPressed();
            }
        });
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AdminRunningListener) {
            totalAdmin = (AdminRunningListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement AdminRunningListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        totalAdmin = null;
    }

    public interface AdminRunningListener {
        void onEndPressed();
        void onHelpPressed();
    }
}
