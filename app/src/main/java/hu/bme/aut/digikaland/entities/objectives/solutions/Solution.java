package hu.bme.aut.digikaland.entities.objectives.solutions;

import java.io.Serializable;

import hu.bme.aut.digikaland.entities.objectives.Objective;
import hu.bme.aut.digikaland.ui.common.objectives.solutions.EvaluateFragment;

/**
 * Created by Sylent on 2018. 04. 03..
 */

public abstract class Solution implements Serializable{
    private Objective objective;

    public int getCurrentPoints() {
        return currentPoints;
    }

    public int getMaxPoints() {
        return maxPoints;
    }

    private int currentPoints;
    private int maxPoints;

    public Solution(Objective o, int curr, int max){ objective = o; currentPoints = curr; maxPoints = max; }

    public Objective getObjective(){
        return objective;
    }

    public void upload(int point){
        // TODO: feltöltése a megadott pontszámnak
    }

    public abstract EvaluateFragment createFragment();
}
