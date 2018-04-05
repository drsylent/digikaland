package hu.bme.aut.digikaland.entities.objectives.solutions;

import hu.bme.aut.digikaland.entities.objectives.PhysicalObjective;
import hu.bme.aut.digikaland.ui.common.objectives.solutions.PhysicalEvaluateFragment;

/**
 * Created by Sylent on 2018. 04. 03..
 */

public class PhysicalSolution extends Solution {
     public PhysicalSolution(PhysicalObjective o, int curr, int max){
        super(o, curr, max);
    }

    @Override
    public PhysicalEvaluateFragment createFragment() {
        return PhysicalEvaluateFragment.newInstance(this, getCurrentPoints(), getMaxPoints());
    }
}
