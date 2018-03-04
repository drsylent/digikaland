package hu.bme.aut.digikaland.ui.common.objectives;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.entities.objectives.PictureObjective;
import hu.bme.aut.digikaland.ui.common.fragments.PictureFragment;

public class PictureObjectiveFragment extends ObjectiveFragment {

    private PictureObjectiveListener objectiveActivity;

    public PictureObjectiveFragment() {
        // Required empty public constructor
    }

    LinearLayout pictures;

    @Override
    public void upload() {
        PictureObjective obj = (PictureObjective) getObjective();
        obj.upload();
    }

    public static PictureObjectiveFragment newInstance(PictureObjective obj) {
        PictureObjectiveFragment fragment = new PictureObjectiveFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_OBJECTIVE, obj);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_picture_objective, container, false);
        TextView tvQuestion = root.findViewById(R.id.pictureQuestion);
        tvQuestion.setText(getObjective().getQuestion());
        int numberOfPictures = ((PictureObjective) getObjective()).getMaxPictures();
        for(int i = 0; i < numberOfPictures; i++){
            // TODO: képek frissítése, ha történt beillesztés
            getChildFragmentManager().beginTransaction().add(R.id.pictureAnswer, PictureFragment.newInstance()).commit();
        }
        Button bCamera = root.findViewById(R.id.pictureCameraButton);
        bCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                objectiveActivity.activateCamera();
            }
        });
        Button bGallery = root.findViewById(R.id.pictureGalleryButton);
        bGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                objectiveActivity.activateGallery();
            }
        });
        return root;
    }

    public void refreshPictures(){
        // TODO: ha megvan a kép, akkor az összes fragmentet eldobjuk, és újra hozzáadjuk őket

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PictureObjectiveListener) {
            objectiveActivity = (PictureObjectiveListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement PictureObjectiveListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        objectiveActivity = null;
    }

    public interface PictureObjectiveListener {
        void activateCamera();
        void activateGallery();
    }
}
