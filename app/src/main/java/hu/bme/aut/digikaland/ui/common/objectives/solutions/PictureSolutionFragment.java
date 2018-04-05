package hu.bme.aut.digikaland.ui.common.objectives.solutions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.entities.objectives.solutions.PictureSolution;
import hu.bme.aut.digikaland.ui.common.fragments.PictureFragment;
import hu.bme.aut.digikaland.utility.FragmentListSaver;


public class PictureSolutionFragment extends SolutionFragment {
    private static final String ARG_PICTUREFRAGMENTS = "pics";
    private ArrayList<PictureFragment> fragments = new ArrayList<>();

    public PictureSolutionFragment() {
        // Required empty public constructor
    }

    @Override
    public void upload() {
    }

    public static PictureSolutionFragment newInstance(PictureSolution sol) {
        PictureSolutionFragment fragment = new PictureSolutionFragment();
        fragment.setArguments(createBundle(sol));
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_picture_evaluate, container, false);
        TextView tvQuestion = root.findViewById(R.id.pictureSolutionQuestion);
        tvQuestion.setText(getSolution().getObjective().getQuestion());
        int numberOfPictures = getMaxNumberOfPictures();
        PictureSolution solution = (PictureSolution) getSolution();
        if(savedInstanceState == null)
            for(int i = 0; i < numberOfPictures; i++){
                PictureFragment fragment = PictureFragment.newInstance(getTag(), solution.getAnswer().get(i));
                fragments.add(fragment);
                getChildFragmentManager().beginTransaction().add(R.id.pictureSolutionAnswer, fragment, PictureFragment.generateTag()).commit();
            }
        else{
            ArrayList<String> tags = savedInstanceState.getStringArrayList(ARG_PICTUREFRAGMENTS);
            FragmentListSaver<PictureFragment> load = new FragmentListSaver<>();
            fragments = load.fragmentTagLoad(tags, getChildFragmentManager(), PictureFragment.class);
        }
        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        FragmentListSaver<PictureFragment> save = new FragmentListSaver<>();
        outState.putStringArrayList(ARG_PICTUREFRAGMENTS, save.fragmentTagSave((fragments)));
    }

    private int getMaxNumberOfPictures(){
        return ((PictureSolution) getSolution()).getAnswer().size();
    }


}
