package hu.bme.aut.digikaland.ui.client.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.entities.Contact;
import hu.bme.aut.digikaland.ui.common.fragments.ContactFragment;

public class ClientStatusFragment extends Fragment {
    public static final String ARG_STATIONSUM = "stations";
    public static final String ARG_STATION_NUMBER = "stationnumber";
    public static final String ARG_RACENAME = "racename";
    public static final String ARG_TEAMNAME = "teamname";
    public static final String ARG_CAPTAIN = "captainname";

    private String raceName;
    private String teamName;
    private int stationNumber;
    private int stationSum;
    private Contact captain;

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
            captain = (Contact) getArguments().getSerializable(ARG_CAPTAIN);
            stationSum = getArguments().getInt(ARG_STATIONSUM);
            stationNumber = getArguments().getInt(ARG_STATION_NUMBER);

        }
    }

    private String getStationString(){
        if(stationNumber == -1) return getString(R.string.starting_place);
        if(stationNumber <= stationSum) return getString(R.string.station_status, stationNumber, stationSum);
        else return getString(R.string.ending_place);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_client_status, container, false);
        TextView tvRaceName = root.findViewById(R.id.clientStatusRaceName);
        TextView tvTeamName = root.findViewById(R.id.clientStatusTeamName);
        TextView tvStations = root.findViewById(R.id.clientStatusStations);
        if(savedInstanceState == null)
            getChildFragmentManager().beginTransaction().add(R.id.clientStatusContent, ContactFragment.newInstance(captain,true)).commit();
        tvRaceName.setText(raceName);
        tvTeamName.setText(teamName);
        tvStations.setText(getStationString());
        return root;
    }

}
