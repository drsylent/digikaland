package hu.bme.aut.digikaland.entities.objectives;

import hu.bme.aut.digikaland.ui.common.objectives.ObjectiveFragment;
import hu.bme.aut.digikaland.ui.common.objectives.PhysicalObjectiveFragment;

public class PhysicalObjective extends Objective {
    public PhysicalObjective(String q) {
        super(q);
    }

    @Override
    public ObjectiveFragment createFragment() {
        return PhysicalObjectiveFragment.newInstance(this);
    }
}
