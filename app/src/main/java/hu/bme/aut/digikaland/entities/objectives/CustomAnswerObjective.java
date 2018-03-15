package hu.bme.aut.digikaland.entities.objectives;

import hu.bme.aut.digikaland.ui.common.objectives.CustomAnswerObjectiveFragment;
import hu.bme.aut.digikaland.ui.common.objectives.ObjectiveFragment;

/**
 * Olyan feladatot reprezentáló osztály, melyre egy saját, szöveges választ kell adni.
 */
public class CustomAnswerObjective extends Objective {

    public CustomAnswerObjective(String q){
        super(q);
    }

    @Override
    public ObjectiveFragment createFragment() {
        return CustomAnswerObjectiveFragment.newInstance(this);
    }

    public void upload(String answer){
        // feltoltes
    }
}
