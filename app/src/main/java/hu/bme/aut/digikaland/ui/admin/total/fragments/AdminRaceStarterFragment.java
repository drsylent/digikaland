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
import hu.bme.aut.digikaland.entities.Location;

public class AdminRaceStarterFragment extends Fragment {
    private static final String ARG_LOCATION = "loc";
    private static final String ARG_TIME = "time";

    private Location location;
    private Date time;

    private AdminStarterListener totalAdmin;

    public AdminRaceStarterFragment() {
        // Required empty public constructor
    }

    public static AdminRaceStarterFragment newInstance(Location location, Date time) {
        AdminRaceStarterFragment fragment = new AdminRaceStarterFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_LOCATION, location);
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
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_admin_race_starter, container, false);
        TextView tvLocation = root.findViewById(R.id.adminStarterLocation);
        tvLocation.setText(location.main);
        TextView tvSubLocation = root.findViewById(R.id.adminStarterSubLocation);
        tvSubLocation.setText(location.detailed);
        TextView tvTime = root.findViewById(R.id.adminStarterTime);
        tvTime.setText(getResources().getString(R.string.date, time));
        Button bEnd = root.findViewById(R.id.adminStarterStart);
        bEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalAdmin.onStartPressed();
            }
        });
        Button bHelp = root.findViewById(R.id.adminStarterHelp);
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
        if (context instanceof AdminStarterListener) {
            totalAdmin = (AdminStarterListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement AdminStarterListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        totalAdmin = null;
    }

    public interface AdminStarterListener {
        void onStartPressed();
        void onHelpPressed();
    }
}
