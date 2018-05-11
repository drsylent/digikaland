package hu.bme.aut.digikaland.entities.objectives.solutions;

import hu.bme.aut.digikaland.entities.objectives.PhysicalObjective;
import hu.bme.aut.digikaland.ui.common.objectives.solutions.PhysicalEvaluateFragment;

public class PhysicalSolution extends Solution {
     public PhysicalSolution(PhysicalObjective o, int curr, int max){
        super(o, curr, max);
    }

     public PhysicalSolution(int curr, int max){
        super(curr, max);
    }

    public PhysicalSolution(int curr, int max, String answer){
        super(curr, max);
    }

    @Override
    public PhysicalEvaluateFragment createFragment() {
        return PhysicalEvaluateFragment.newInstance(this, getCurrentPoints(), getMaxPoints());
    }
}
