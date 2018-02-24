package hu.bme.aut.digikaland.ui.common.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import hu.bme.aut.digikaland.R;

public class PrimaryCodeFragment extends Fragment {

    private PrimaryCodeReady startupActivity;
    private EditText primaryHolder;

    private String getCode(){
        return primaryHolder.getText().toString();
    }

    public PrimaryCodeFragment() {
        // Required empty public constructor
    }

    public static PrimaryCodeFragment newInstance() {
        return new PrimaryCodeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_primary_code, container, false);
        Button sender = root.findViewById(R.id.primarySend);
        primaryHolder = root.findViewById(R.id.primaryCode);
        sender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startupActivity.onPrimaryCodeHit(getCode());
            }
        });
        primaryHolder.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    startupActivity.onPrimaryCodeHit(getCode());
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
        if (context instanceof PrimaryCodeReady) {
            startupActivity = (PrimaryCodeReady) context;
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

    public interface PrimaryCodeReady {
        void onPrimaryCodeHit(String raceCode);
    }
}
