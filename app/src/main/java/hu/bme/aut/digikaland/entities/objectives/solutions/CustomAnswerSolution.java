package hu.bme.aut.digikaland.entities.objectives.solutions;

import hu.bme.aut.digikaland.ui.common.objectives.solutions.CustomAnswerEvaluateFragment;

public class CustomAnswerSolution extends Solution {
    public String getAnswer() {
        return answer;
    }

    private String answer;

    public CustomAnswerSolution(int curr, int max, String ans){
        super(curr, max);
        answer = ans;
    }

    @Override
    public CustomAnswerEvaluateFragment createFragment() {
        return CustomAnswerEvaluateFragment.newInstance(this, getCurrentPoints(), getMaxPoints());
    }
}
