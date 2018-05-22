package hu.bme.aut.digikaland.entities.objectives;

import android.net.Uri;

import java.util.ArrayList;
import hu.bme.aut.digikaland.ui.common.objectives.ObjectiveFragment;
import hu.bme.aut.digikaland.ui.common.objectives.PictureObjectiveFragment;

public class PictureObjective extends Objective {
    private int maxPictures;

    private ArrayList<Uri> pictures;

    public ArrayList<Uri> getPictures() {
        return pictures;
    }

    public PictureObjective(String q, int maxPictureNumber) {
        super(q);
        maxPictures = maxPictureNumber;
    }

    public int getMaxPictures(){
        return maxPictures;
    }

    public void setPictures(ArrayList<Uri> pics){
        pictures = pics;
    }

    @Override
    public ObjectiveFragment createFragment() {
        return PictureObjectiveFragment.newInstance(this);
    }
}
