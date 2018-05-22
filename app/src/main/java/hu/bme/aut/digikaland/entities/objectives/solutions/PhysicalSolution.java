package hu.bme.aut.digikaland.entities.objectives.solutions;

import hu.bme.aut.digikaland.ui.common.objectives.solutions.PhysicalEvaluateFragment;

public class PhysicalSolution extends Solution {

    // ezt így bent hagyjuk, így kompatibilis ez a konstruktor paraméterezés
    // a CustomAnswerSolution konstruktorának paraméterezésével
    public PhysicalSolution(int curr, int max, String answer){
        super(curr, max);
    }

    @Override
    public PhysicalEvaluateFragment createFragment() {
        return PhysicalEvaluateFragment.newInstance(this, getCurrentPoints(), getMaxPoints());
    }
}
