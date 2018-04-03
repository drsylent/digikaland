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

    private int currentPoints;
    private int maxPoints;

    private OnFragmentInteractionListener mListener;

    public PointDisplayFragment() {
        // Required empty public constructor
    }

    public static PointDisplayFragment newInstance(int current, int max) {
        PointDisplayFragment fragment = new PointDisplayFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_CURRENT, current);
        args.putInt(ARG_MAX, max);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            currentPoints = getArguments().getInt(ARG_CURRENT);
            maxPoints = getArguments().getInt(ARG_MAX);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_point_display, container, false);
        Button button = root.findViewById(R.id.pointDisplay);
        button.setText(getResources().getString(R.string.point_status, currentPoints, maxPoints));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        return root;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
