package hu.bme.aut.digikaland.ui.common.fragments;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.entities.Contact;

public class TitleContactFragment extends Fragment {
    private static final String ARG_TITLE = "title";
    private static final String ARG_CONTACT = "contactn";
    private static final String ARG_CENTER = "center";

    private String title;
    private Contact contact;
    private boolean center;

    public TitleContactFragment() {
        // Required empty public constructor
    }

    public static TitleContactFragment newInstance(String title, Contact contact, boolean center) {
        TitleContactFragment fragment = new TitleContactFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putSerializable(ARG_CONTACT, contact);
        args.putBoolean(ARG_CENTER, center);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE);
            contact = (Contact) getArguments().getSerializable(ARG_CONTACT);
            center = getArguments().getBoolean(ARG_CENTER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_title_contact, container, false);
        TextView tv = root.findViewById(R.id.titleContactTitle);
        tv.setText(title);
        if(center) tv.setGravity(Gravity.CENTER_HORIZONTAL);
        if(savedInstanceState == null)
        getChildFragmentManager().beginTransaction().add(R.id.titleContactContent, ContactFragment.newInstance(contact, center)).commit();
        return root;
    }

}
