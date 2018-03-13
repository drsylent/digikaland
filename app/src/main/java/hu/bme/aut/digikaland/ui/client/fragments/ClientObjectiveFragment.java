package hu.bme.aut.digikaland.ui.client.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.utility.TimeWriter;


public class ClientObjectiveFragment extends Fragment {
    private static final String ARG_STATIONNUMBER = "stationsum";
    private static final String ARG_STATIONCURRENT = "stationcurrent";
    private static final String ARG_TIME = "time";

    private CountDownTimer timer;
    private int stationNow;
    private int stationSum;
    private long timeLeft;

    private TextView countdown;

    private ClientActiveObjectiveListener ClientActivity;

    public ClientObjectiveFragment() {
        // Required empty public constructor
    }

    public static ClientObjectiveFragment newInstance(int now, int sum, long left) {
        ClientObjectiveFragment fragment = new ClientObjectiveFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_STATIONCURRENT, now);
        args.putLong(ARG_TIME, left);
        args.putInt(ARG_STATIONNUMBER, sum);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            stationNow = getArguments().getInt(ARG_STATIONCURRENT);
            stationSum = getArguments().getInt(ARG_STATIONNUMBER);
            timeLeft = getArguments().getLong(ARG_TIME);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(ARG_TIME, timeLeft);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_client_objective, container, false);
        Button open = root.findViewById(R.id.clientObjectiveOpen);
        open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ClientActivity != null) {
                    ClientActivity.onActiveObjectiveOpen();
                }
            }
        });
        TextView stationSummary = root.findViewById(R.id.clientObjectiveStation);
        stationSummary.setText("Állomás: " + stationNow + "/" + stationSum);
        countdown = root.findViewById(R.id.clientObjectiveCounter);
        if(savedInstanceState != null) timeLeft = savedInstanceState.getLong(ARG_TIME);
        countdown.setText(TimeWriter.countdownFormat(timeLeft));
        timer = new CountDownTimer(timeLeft*1000, 1000) {

            @Override
            public void onTick(long l) {
                countdown.setText(TimeWriter.countdownFormat(timeLeft));
                timeLeft--;
            }

            @Override
            public void onFinish() {
                countdown.setText(TimeWriter.countdownFormat(0));
            }
        }.start();
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ClientActiveObjectiveListener) {
            ClientActivity = (ClientActiveObjectiveListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ClientActiveObjectiveListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        timer.cancel();
        ClientActivity = null;
    }

    public interface ClientActiveObjectiveListener {
        void onActiveObjectiveOpen();
    }
}
