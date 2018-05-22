package hu.bme.aut.digikaland.ui.common.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import hu.bme.aut.digikaland.R;

/**
 * Egy egyszerű szövegmegjelenítő fragment logikája - állítható, hogy középen legyen-e a szöveg.
 */
public class TextFragment extends Fragment {
    private static final String ARG_TEXT = "text";
    private static final String ARG_CENTER = "center";

    private String text;
    private boolean center;

    public TextFragment() {
        // Required empty public constructor
    }

    public static TextFragment newInstance(String text, boolean center) {
        TextFragment fragment = new TextFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TEXT, text);
        args.putBoolean(ARG_CENTER, center);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            text = getArguments().getString(ARG_TEXT);
            center = getArguments().getBoolean(ARG_CENTER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_text, container, false);
        TextView tv = root.findViewById(R.id.text_place);
        tv.setText(text);
        if(center) tv.setGravity(Gravity.CENTER_HORIZONTAL);
        return root;
    }
}
