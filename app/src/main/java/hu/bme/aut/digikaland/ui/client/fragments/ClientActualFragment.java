package hu.bme.aut.digikaland.ui.client.fragments;

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
import hu.bme.aut.digikaland.utility.TimeWriter;

public class ClientActualFragment extends Fragment {
    public static final String ARG_STATIONS = "stations";
    public static final String ARG_STATION_NUMBER = "stationnumber";
    public static final String ARG_LOCATION = "location";
    public static final String ARG_SUBLOCATION = "detailedlocation";
    public static final String ARG_TIME = "time";

    private int stationSum;
    private int stationNumber;
    private String location;
    private String subLocation;
    private Date time;

    private ClientActualMainListener clientActual;

    public ClientActualFragment() {
        // Required empty public constructor
    }

    public static ClientActualFragment newInstance(Bundle arguments) {
        ClientActualFragment fragment = new ClientActualFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            stationSum = getArguments().getInt(ARG_STATIONS);
            stationNumber = getArguments().getInt(ARG_STATION_NUMBER);
            location = getArguments().getString(ARG_LOCATION);
            subLocation = getArguments().getString(ARG_SUBLOCATION);
            time = new Date(getArguments().getLong(ARG_TIME));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_client_actual, container, false);
        Button map = root.findViewById(R.id.clientActualMap);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clientActual.mapActivation();
            }
        });
        Button help = root.findViewById(R.id.clientActualHelp);
        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clientActual.helpActivation();
            }
        });
        TextView tvStation = root.findViewById(R.id.clientStationText);
        TextView tvLocation = root.findViewById(R.id.clientStationLocation);
        TextView tvSubLocation = root.findViewById(R.id.clientStationSubLocation);
        TextView tvTime = root.findViewById(R.id.clientStationTime);
        tvStation.setText(getStationString());
        tvLocation.setText(location);
        tvSubLocation.setText(subLocation);
        tvTime.setText(TimeWriter.dateFormat(time));
        return root;
    }

    private String getStationString(){
        if(stationNumber == 0) return "Kezdő gyülekezőhely";
        if(stationNumber <= stationSum) return "Állomás: " + stationNumber + "/" + stationSum;
        else return "Eredményhirdetés";
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ClientActualMainListener) {
            clientActual = (ClientActualMainListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ClientActualMainListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        clientActual = null;
    }

    public interface ClientActualMainListener {
        void mapActivation();
        void helpActivation();
    }
}
