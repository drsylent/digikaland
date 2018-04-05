package hu.bme.aut.digikaland.entities.objectives.solutions;

import hu.bme.aut.digikaland.entities.objectives.MultipleChoiceObjective;
import hu.bme.aut.digikaland.ui.common.objectives.solutions.MultipleChoiceEvaluateFragment;

/**
 * Created by Sylent on 2018. 04. 03..
 */

public class MultipleChoiceSolution extends Solution {
    public int getAnswer() {
        return answer;
    }

    private int answer;

    public MultipleChoiceSolution(MultipleChoiceObjective o, int curr, int max, int ans){
        super(o, curr, max);
        answer = ans;
    }

    @Override
    public MultipleChoiceEvaluateFragment createFragment() {
        return MultipleChoiceEvaluateFragment.newInstance(this, getCurrentPoints(), getMaxPoints());
    }
}
