package hu.bme.aut.digikaland.entities.objectives.solutions;

import java.util.ArrayList;

import hu.bme.aut.digikaland.entities.objectives.PictureObjective;
import hu.bme.aut.digikaland.ui.common.objectives.solutions.PictureEvaluateFragment;

/**
 * Created by Sylent on 2018. 04. 03..
 */

public class PictureSolution extends Solution {
    public ArrayList<String> getAnswer() {
        return answer;
    }

    private ArrayList<String> answer;

    public PictureSolution(PictureObjective o, int curr, int max, ArrayList<String> ans){
        super(o, curr, max);
        answer = ans;
    }

    @Override
    public PictureEvaluateFragment createFragment() {
        return PictureEvaluateFragment.newInstance(this, getCurrentPoints(), getMaxPoints());
    }
}
