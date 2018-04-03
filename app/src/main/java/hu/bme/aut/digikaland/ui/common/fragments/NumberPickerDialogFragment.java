package hu.bme.aut.digikaland.ui.common.fragments;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.ui.common.objectives.solutions.SolutionFragment;

/**
 * Created by Sylent on 2018. 04. 03..
 */

public class NumberPickerDialogFragment extends DialogFragment {
    private static final String ARG_MAX = "maxnumber";
    private static final String ARG_CURRENT = "currentnumber";
    private static final String ARG_HOST = "hosttag";

    private PointSettingInterface activity;
    private NumberPicker numberPicker;
    private String hostTag;
    private int maxPoints;
    private int currentPoints;

    public static NumberPickerDialogFragment newInstance(String hostTag, int current, int maxnumber){
        NumberPickerDialogFragment fragment = new NumberPickerDialogFragment();
        Bundle args = new Bundle();
        args.putString(ARG_HOST, hostTag);
        args.putInt(ARG_MAX, maxnumber);
        args.putInt(ARG_CURRENT, current);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            hostTag = getArguments().getString(ARG_HOST);
            maxPoints = getArguments().getInt(ARG_MAX);
            currentPoints = getArguments().getInt(ARG_CURRENT);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PointSettingInterface) {
            activity = (PointSettingInterface) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement PointHandleActivity");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
    }

    public interface PointSettingInterface {
        void pointSet(String fragmentTag, int point);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View root = inflater.inflate(R.layout.item_number_picker, null);
        numberPicker = root.findViewById(R.id.number_picker);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(maxPoints);
        numberPicker.setValue(currentPoints);
        builder.setView(root)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        activity.pointSet(hostTag, numberPicker.getValue());
                    }
                })
                .setNegativeButton("Mégse", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
        return builder.create();
    }
}
