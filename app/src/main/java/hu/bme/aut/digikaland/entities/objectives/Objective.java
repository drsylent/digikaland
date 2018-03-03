package hu.bme.aut.digikaland.entities.objectives;

import android.support.v4.app.Fragment;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import hu.bme.aut.digikaland.ui.common.objectives.ObjectiveFragment;

/**
 * Created by Sylent on 2018. 03. 03..
 */

public abstract class Objective implements Serializable{
    // Befejezi ido az allomashoz lesz kotve!
    private String question;

    public Objective(String q){
        question = q;
    }

    public String getQuestion(){
        return question;
    }

    public abstract ObjectiveFragment createFragment();
}
