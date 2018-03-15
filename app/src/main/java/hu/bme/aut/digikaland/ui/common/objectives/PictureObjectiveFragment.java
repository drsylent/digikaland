package hu.bme.aut.digikaland.ui.common.objectives;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.entities.objectives.PictureObjective;
import hu.bme.aut.digikaland.ui.common.fragments.PictureFragment;
import hu.bme.aut.digikaland.utility.FragmentListSaver;

public class PictureObjectiveFragment extends ObjectiveFragment {
    private static final String ARG_PICTUREFRAGMENTS = "pics";
    private PictureObjectiveListener objectiveActivity;
    private ArrayList<PictureFragment> fragments = new ArrayList<>();

    public PictureObjectiveFragment() {
        // Required empty public constructor
    }

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
        int numberOfPictures = getMaxNumberOfPictures();
        if(savedInstanceState == null)
        for(int i = 0; i < numberOfPictures; i++){
            PictureFragment fragment = PictureFragment.newInstance(getTag());
            fragments.add(fragment);
            getChildFragmentManager().beginTransaction().add(R.id.pictureAnswer, fragment, PictureFragment.generateTag()).commit();
        }
        else{
            ArrayList<String> tags = savedInstanceState.getStringArrayList(ARG_PICTUREFRAGMENTS);
            FragmentListSaver<PictureFragment> load = new FragmentListSaver<>();
            fragments = load.fragmentTagLoad(tags, getChildFragmentManager(), PictureFragment.class);
        }
        Button bCamera = root.findViewById(R.id.pictureCameraButton);
        bCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                objectiveActivity.activateCamera(getTag());
            }
        });
        Button bGallery = root.findViewById(R.id.pictureGalleryButton);
        bGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                objectiveActivity.activateGallery(getTag());
            }
        });
        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        FragmentListSaver<PictureFragment> save = new FragmentListSaver<>();
        outState.putStringArrayList(ARG_PICTUREFRAGMENTS, save.fragmentTagSave((fragments)));
    }

    private int getMaxNumberOfPictures(){
        return ((PictureObjective) getObjective()).getMaxPictures();
    }

    public int getRemainingNumberOfPictures(){
        int free = 0;
        for(PictureFragment f : fragments){
            if(f.isEmpty()) free++;
        }
        return free;
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

    private PictureFragment getFirstFreeFragment(){
        for(PictureFragment f : fragments){
            if(f.isEmpty()) return f;
        }
        return null;
    }

    public void givePicture(Uri uri){
        PictureFragment pf = getFirstFreeFragment();
        if(pf != null) pf.setPicture(uri);
    }

    public boolean isFreePicture(){
        return getFirstFreeFragment() != null;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        objectiveActivity = null;
    }

    public interface PictureObjectiveListener {
        void activateCamera(String tag);
        void activateGallery(String tag);
    }
}
