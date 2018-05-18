package hu.bme.aut.digikaland.entities.objectives.solutions;

import android.support.annotation.NonNull;

import java.io.Serializable;

import hu.bme.aut.digikaland.entities.objectives.Objective;
import hu.bme.aut.digikaland.ui.common.objectives.solutions.EvaluateFragment;

public abstract class Solution implements Serializable, Comparable<Solution>{
    public void setObjective(Objective objective) {
        this.objective = objective;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;

    // százalékban
    private int penalty;

    public int getPenalty() {
        return penalty;
    }

    public void setPenalty(int penalty) {
        this.penalty = penalty;
    }

    private Objective objective;

    public int getCurrentPoints() {
        return currentPoints;
    }

    public int getMaxPoints() {
        return maxPoints;
    }

    public void setCurrentPoints(int currentPoints) {
        this.currentPoints = currentPoints;
    }

    private int currentPoints;
    private int maxPoints;

    // TODO: deprecated
    public Solution(Objective o, int curr, int max){ objective = o; currentPoints = curr; maxPoints = max; }

    public Solution(int curr, int max){ currentPoints = curr; maxPoints = max; }

    @Override
    public int compareTo(@NonNull Solution solution) {
        return this.id.compareTo(solution.id);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Solution && ((Solution) obj).id.equals(this.id);
    }

    public Objective getObjective(){
        return objective;
    }

    public void upload(int point){
        // TODO: feltöltése a megadott pontszámnak
    }

    public abstract EvaluateFragment createFragment();
}
