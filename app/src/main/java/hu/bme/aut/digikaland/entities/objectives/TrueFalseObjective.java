package hu.bme.aut.digikaland.entities.objectives;

import hu.bme.aut.digikaland.ui.common.objectives.ObjectiveFragment;
import hu.bme.aut.digikaland.ui.common.objectives.TrueFalseObjectiveFragment;

public class TrueFalseObjective extends Objective {

    public boolean getAnswer() {
        return answer;
    }

    private boolean answer;

    public TrueFalseObjective(String q) {
        super(q);
    }

    @Override
    public ObjectiveFragment createFragment() {
        return TrueFalseObjectiveFragment.newInstance(this);
    }

    public void setAnswer(boolean ans) {
        answer = ans;
    }
}
