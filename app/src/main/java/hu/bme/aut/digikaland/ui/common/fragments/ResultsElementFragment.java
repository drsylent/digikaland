package hu.bme.aut.digikaland.ui.common.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import hu.bme.aut.digikaland.R;

/**
 * Egy elem a végeredmény képernyőn (helyezés, csapatnév, pontszám)
 */
public class ResultsElementFragment extends Fragment {
    private static final String ARG_POSITION = "POSITION";
    private static final String ARG_TEAMNAME = "TEAMNAME";
    private static final String ARG_POINTS = "POINTS";

    private int position;
    private String teamName;
    private double points;

    public ResultsElementFragment() {
        // Required empty public constructor
    }

    public static ResultsElementFragment newInstance(int pos, String name, double point) {
        ResultsElementFragment fragment = new ResultsElementFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, pos);
        args.putString(ARG_TEAMNAME, name);
        args.putDouble(ARG_POINTS, point);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POSITION);
            teamName = getArguments().getString(ARG_TEAMNAME);
            points = getArguments().getDouble(ARG_POINTS);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_results_element, container, false);
        TextView tvPosition = root.findViewById(R.id.resultsPosition);
        tvPosition.setText(getString(R.string.position_number, position));
        TextView tvName = root.findViewById(R.id.resultsTeamName);
        tvName.setText(teamName);
        TextView tvPoints = root.findViewById(R.id.resultsPoint);
        tvPoints.setText(getString(R.string.point_number, points));
        return root;
    }
}
