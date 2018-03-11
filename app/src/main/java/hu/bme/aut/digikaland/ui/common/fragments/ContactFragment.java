package hu.bme.aut.digikaland.ui.common.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import hu.bme.aut.digikaland.R;

public class ContactFragment extends Fragment {
    private static final String ARG_NAME = "name";
    private static final String ARG_PHONE = "phone";
    private static final String ARG_CENTER = "center";

    private String name;
    private String phone;
    private boolean center;

    private ClientHelpListener clientHelp;

    public ContactFragment() {
        // Required empty public constructor
    }

    public static ContactFragment newInstance(String param1, String param2, boolean center) {
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, param1);
        args.putString(ARG_PHONE, param2);
        args.putBoolean(ARG_CENTER, center);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            name = getArguments().getString(ARG_NAME);
            phone = getArguments().getString(ARG_PHONE);
            center = getArguments().getBoolean(ARG_CENTER);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.item_contact, container, false);
        TextView tvName = root.findViewById(R.id.contactName);
        TextView tvPhone = root.findViewById(R.id.contactPhone);
        tvName.setText(name);
        tvPhone.setText(phone);
        if(center){
            tvName.setGravity(Gravity.CENTER_HORIZONTAL);
            tvPhone.setGravity(Gravity.CENTER_HORIZONTAL);
        }
        LinearLayout content = root.findViewById(R.id.contactContent);
        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clientHelp.phoneDial(phone);
            }
        });
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof ClientHelpListener) {
            clientHelp = (ClientHelpListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement ClientHelpListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        clientHelp = null;
    }


    public interface ClientHelpListener {
        void phoneDial(String phoneNumber);
    }
}
