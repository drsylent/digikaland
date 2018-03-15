package hu.bme.aut.digikaland.entities.objectives;

import java.io.Serializable;
import hu.bme.aut.digikaland.ui.common.objectives.ObjectiveFragment;

/**
 * Ez az osztály reprezentál egy feladatot, melyet végre kell hajtani. Absztrakt osztály, a leszármazottai valósítják meg, hogy mit kell tenni.
 */
public abstract class Objective implements Serializable{
    // Befejezési idő az állomáshoz lesz kötve!
    private String question;

    public Objective(String q){
        question = q;
    }

    public String getQuestion(){
        return question;
    }

    public abstract ObjectiveFragment createFragment();
}
