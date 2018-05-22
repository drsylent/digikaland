package hu.bme.aut.digikaland.entities.objectives.solutions;

import hu.bme.aut.digikaland.ui.common.objectives.solutions.TrueFalseEvaluateFragment;

public class TrueFalseSolution extends Solution {
    public boolean getAnswer() {
        return answer;
    }

    private boolean answer;

    public TrueFalseSolution(int curr, int max, boolean ans){
        super(curr, max);
        answer = ans;
    }

    @Override
    public TrueFalseEvaluateFragment createFragment() {
        return TrueFalseEvaluateFragment.newInstance(this, getCurrentPoints(), getMaxPoints());
    }
}
