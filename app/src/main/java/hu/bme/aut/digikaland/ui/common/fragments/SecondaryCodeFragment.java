package hu.bme.aut.digikaland.ui.common.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import hu.bme.aut.digikaland.R;

public class SecondaryCodeFragment extends Fragment {
    private static final String ARG_RACENAME = "racename";

    private SecondaryCodeReady startupActivity;
    private EditText secondaryHolder;
    private String raceName;

    private String getCode(){
        return secondaryHolder.getText().toString();
    }

    public SecondaryCodeFragment() {
        // Required empty public constructor
    }

    public static SecondaryCodeFragment newInstance(String name) {
        SecondaryCodeFragment fragment = new SecondaryCodeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RACENAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            raceName = getArguments().getString(ARG_RACENAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_secondary_code, container, false);
        TextView raceTitle = root.findViewById(R.id.SecondaryRaceTitle);
        raceTitle.setText(raceName);
        final Button sender = root.findViewById(R.id.secondarySend);
        secondaryHolder = root.findViewById(R.id.secondaryCode);
        sender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startupActivity.onSecondaryCodeHit(secondaryHolder.getText().toString(), sender);
            }
        });
        secondaryHolder.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    mgr.hideSoftInputFromWindow(secondaryHolder.getWindowToken(), 0);
                    startupActivity.onSecondaryCodeHit(getCode(), sender);
                    handled = true;
                }
                return handled;

            }
        });
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SecondaryCodeReady) {
            startupActivity = (SecondaryCodeReady) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SecondaryCodeReady");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        startupActivity = null;
    }

    public interface SecondaryCodeReady {
        void onSecondaryCodeHit(String roleCode, Button button);
    }
}
