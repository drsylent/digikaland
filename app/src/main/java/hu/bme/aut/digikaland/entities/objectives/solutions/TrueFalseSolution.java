package hu.bme.aut.digikaland.entities.objectives.solutions;

import hu.bme.aut.digikaland.entities.objectives.TrueFalseObjective;
import hu.bme.aut.digikaland.ui.common.objectives.solutions.TrueFalseEvaluateFragment;

/**
 * Created by Sylent on 2018. 04. 03..
 */

public class TrueFalseSolution extends Solution {
    public boolean getAnswer() {
        return answer;
    }

    private boolean answer;

    public TrueFalseSolution(TrueFalseObjective o, int curr, int max, boolean ans){
        super(o, curr, max);
        answer = ans;
    }

    @Override
    public TrueFalseEvaluateFragment createFragment() {
        return TrueFalseEvaluateFragment.newInstance(this, getCurrentPoints(), getMaxPoints());
    }
}
