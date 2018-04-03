package hu.bme.aut.digikaland.ui.common.objectives.solutions;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import hu.bme.aut.digikaland.R;

public class PointDisplayFragment extends Fragment {
    private static final String ARG_CURRENT = "current";
    private static final String ARG_MAX = "max";
    private static final String ARG_HOST = "hosttag";

    private int currentPoints;
    private int maxPoints;
    private Button button;
    private String hostTag;

    private PointHandleActivity mListener;

    public PointDisplayFragment() {
        // Required empty public constructor
    }

    public static PointDisplayFragment newInstance(int current, int max, String hostTag) {
        PointDisplayFragment fragment = new PointDisplayFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CURRENT, current);
        args.putInt(ARG_MAX, max);
        args.putString(ARG_HOST, hostTag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentPoints = getArguments().getInt(ARG_CURRENT);
            maxPoints = getArguments().getInt(ARG_MAX);
            hostTag = getArguments().getString(ARG_HOST);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_point_display, container, false);
        button = root.findViewById(R.id.pointDisplay);
        setPoints(currentPoints);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onButtonPressed();
            }
        });
        return root;
    }

    public void onButtonPressed() {
        if (mListener != null) {
            mListener.settingPoint(hostTag, currentPoints, maxPoints);
        }
    }

    public void setPoints(int points){
        currentPoints = points;
        button.setText(getResources().getString(R.string.point_status, currentPoints, maxPoints));
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PointHandleActivity) {
            mListener = (PointHandleActivity) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement PointHandleActivity");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface PointHandleActivity {
        // TODO: Kommunikáció a pontszámról
        void settingPoint(String hostTag, int current, int max);
    }
}
