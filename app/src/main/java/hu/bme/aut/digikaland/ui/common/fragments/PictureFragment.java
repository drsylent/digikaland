package hu.bme.aut.digikaland.ui.common.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import hu.bme.aut.digikaland.R;

public class PictureFragment extends Fragment {
    private static final String ARG_PICTURE = "picture";
    ImageView imageView;

    // TODO: Kép típusa és kezelése

    private PictureFragmentListener listener;

    public PictureFragment() {
        // Required empty public constructor
    }

    public static PictureFragment newInstance() {
        PictureFragment fragment = new PictureFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_picture, container, false);
        imageView = root.findViewById(R.id.picturePlace);
        // TODO: kép beállítása, ha van

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: csak ha van kép
                listener.onExistingPictureClicked();
            }
        });
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PictureFragmentListener) {
            listener = (PictureFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement PictureFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface PictureFragmentListener {
        void onExistingPictureClicked();
    }
}
