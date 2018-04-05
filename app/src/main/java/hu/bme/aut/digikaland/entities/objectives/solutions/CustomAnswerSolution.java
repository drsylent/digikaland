package hu.bme.aut.digikaland.entities.objectives.solutions;

import hu.bme.aut.digikaland.entities.objectives.CustomAnswerObjective;
import hu.bme.aut.digikaland.ui.common.objectives.solutions.CustomAnswerEvaluateFragment;

/**
 * Created by Sylent on 2018. 04. 03..
 */

public class CustomAnswerSolution extends Solution {
    public String getAnswer() {
        return answer;
    }

    private String answer;

    public CustomAnswerSolution(CustomAnswerObjective o, int curr, int max, String ans){
        super(o, curr, max);
        answer = ans;
    }

    @Override
    public CustomAnswerEvaluateFragment createFragment() {
        return CustomAnswerEvaluateFragment.newInstance(this, getCurrentPoints(), getMaxPoints());
    }
}
