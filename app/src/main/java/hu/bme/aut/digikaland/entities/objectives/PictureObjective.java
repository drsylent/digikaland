package hu.bme.aut.digikaland.entities.objectives;

import hu.bme.aut.digikaland.ui.common.objectives.ObjectiveFragment;
import hu.bme.aut.digikaland.ui.common.objectives.PictureObjectiveFragment;

/**
 * Created by Sylent on 2018. 03. 03..
 */

public class PictureObjective extends Objective {
    private int maxPictures;

    public PictureObjective(String q, int maxPictureNumber) {
        super(q);
        maxPictures = maxPictureNumber;
    }

    public int getMaxPictures(){
        return maxPictures;
    }

    // TODO: mi lesz a paraméter?
    public void upload(){
        // feltoltese a kepeknek
    }

    @Override
    public ObjectiveFragment createFragment() {
        return PictureObjectiveFragment.newInstance(this);
    }
}
