package hu.bme.aut.digikaland.entities.objectives;

import hu.bme.aut.digikaland.ui.common.objectives.ObjectiveFragment;
import hu.bme.aut.digikaland.ui.common.objectives.TrueFalseObjectiveFragment;

/**
 * Created by Sylent on 2018. 03. 03..
 */

public class TrueFalseObjective extends Objective {

    public TrueFalseObjective(String q) {
        super(q);
    }

    @Override
    public ObjectiveFragment createFragment() {
        return TrueFalseObjectiveFragment.newInstance(this);
    }

    public void upload(boolean answer) {
        // itt pedig a feltoles vegbemegy majd valahogyan
    }
}
