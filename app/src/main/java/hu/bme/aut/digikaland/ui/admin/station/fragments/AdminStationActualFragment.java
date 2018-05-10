package hu.bme.aut.digikaland.ui.admin.station.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.entities.Contact;
import hu.bme.aut.digikaland.entities.Location;
import hu.bme.aut.digikaland.ui.common.fragments.TextFragment;
import hu.bme.aut.digikaland.ui.common.fragments.TitleContactFragment;

public class AdminStationActualFragment extends Fragment {
    private static final String ARG_LOCATION = "location";
    private static final String ARG_SUBLOCATION = "detailedlocation";
    private static final String ARG_FULLLOCATION = "fulllocation";

    // Mivel a gazda activity fogja kezelni a hálózati kapcsolatot, és gondoskodik az adatok frisseségéért
    // ezért praktikusabb, ha a változó adatokat ott tároljuk.
    private String location;
    private String sublocation;

    private Location fullLocation;

    private AdminActivityInterface activity;

    public AdminStationActualFragment() {
        // Required empty public constructor
    }

    // TODO: deprecated
    public static AdminStationActualFragment newInstance(String location, String sublocation) {
        AdminStationActualFragment fragment = new AdminStationActualFragment();
        Bundle arguments = new Bundle();
        arguments.putString(ARG_LOCATION, location);
        arguments.putString(ARG_SUBLOCATION, sublocation);
        fragment.setArguments(arguments);
        return fragment;
    }

    public static AdminStationActualFragment newInstance(Location location) {
        AdminStationActualFragment fragment = new AdminStationActualFragment();
        Bundle arguments = new Bundle();
        arguments.putSerializable(ARG_FULLLOCATION, location);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            location = getArguments().getString(ARG_LOCATION);
            sublocation = getArguments().getString(ARG_SUBLOCATION);
            fullLocation = (Location) getArguments().getSerializable(ARG_FULLLOCATION);
        }
    }

    TextView stationStatus;
    public void setStationStatusValues(){
        int e = activity.getEvaluated();
        int d = activity.getDone();
        int s = activity.getSum();
        stationStatus.setText(getString(R.string.tri_status, e, d, s));
    }

    public void setNextTeamValues(){
        if(!activity.areAllTeamsDone()){
            String nextTeam = activity.getNextTeamName();
            Contact nextContact = activity.getNextTeamContact();
            getChildFragmentManager().beginTransaction().replace(R.id.adminStationNextTeamContent, TitleContactFragment.newInstance(nextTeam, nextContact, true)).commit();
        }
        else{
            getChildFragmentManager().beginTransaction().replace(R.id.adminStationNextTeamContent, TextFragment.newInstance("Nincs több csapat!", true)).commit();
        }
    }

    Button evaluate;
    public void buttonEvaluate(){
        if(activity.isToEvaluate()) {
            evaluate.setTextColor(getResources().getColor(R.color.colorBlack));
            evaluate.setBackgroundColor(getResources().getColor(R.color.colorCurrently));
        }
        else{
            evaluate.setTextColor(getResources().getColor(R.color.colorWhite));
            evaluate.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        }
    }

    public void refreshAllData(){
        setStationStatusValues();
        setNextTeamValues();
        buttonEvaluate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_station_admin_actual, container, false);
        stationStatus = root.findViewById(R.id.adminStationActualStatus);
        TextView stationloc = root.findViewById(R.id.adminStationStationLocation);
        TextView stationsubloc = root.findViewById(R.id.adminStationStationSubLocation);
        stationloc.setText(fullLocation.main);
        stationsubloc.setText(fullLocation.detailed);
        evaluate = root.findViewById(R.id.adminStationActualEvaluate);
        evaluate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.onEvaluateActivation();
            }
        });
        Button objectives = root.findViewById(R.id.adminStationActualObjectives);
        objectives.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.onObjectivesActivation();
            }
        });
        Button help = root.findViewById(R.id.adminStationActualHelp);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.onHelpActivation();
            }
        });
        refreshAllData();
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AdminActivityInterface) {
            activity = (AdminActivityInterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement AdminActivityInterface");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }


    public interface AdminActivityInterface {
        int getEvaluated();
        int getDone();
        int getSum();
        boolean areAllTeamsDone();
        String getNextTeamName();
        Contact getNextTeamContact();
        void onEvaluateActivation();
        void onObjectivesActivation();
        void onHelpActivation();
        boolean isToEvaluate();
    }
}
