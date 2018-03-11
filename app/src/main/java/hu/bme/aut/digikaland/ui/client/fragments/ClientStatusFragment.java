package hu.bme.aut.digikaland.ui.client.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.ui.common.fragments.ContactFragment;

public class ClientStatusFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String ARG_RACENAME = "param1";
    public static final String ARG_TEAMNAME = "param2";
    public static final String ARG_STATIONS = "param3";
    public static final String ARG_CAPTAIN = "param4";
    public static final String ARG_PHONE = "param5";

    // TODO: Rename and change types of parameters
    private String raceName;
    private String teamName;
    private String stations;
    private String captain;
    private String phone;


    public ClientStatusFragment() {
        // Required empty public constructor
    }

    public static ClientStatusFragment newInstance(Bundle args) {
        ClientStatusFragment fragment = new ClientStatusFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            raceName = getArguments().getString(ARG_RACENAME);
            teamName = getArguments().getString(ARG_TEAMNAME);
            stations = getArguments().getString(ARG_STATIONS);
            captain = getArguments().getString(ARG_CAPTAIN);
            phone = getArguments().getString(ARG_PHONE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_client_status, container, false);
        TextView tvRaceName = root.findViewById(R.id.clientStatusRaceName);
        TextView tvTeamName = root.findViewById(R.id.clientStatusTeamName);
        TextView tvStations = root.findViewById(R.id.clientStatusStations);
        getChildFragmentManager().beginTransaction().add(R.id.clientStatusContent, ContactFragment.newInstance(captain, phone, true)).commit();
        tvRaceName.setText(raceName);
        tvTeamName.setText(teamName);
        tvStations.setText(stations);
        return root;
    }

}
