package hu.bme.aut.digikaland.entities;

import java.io.Serializable;

// TODO: elterjeszteni máshova is
public class EvaluationStatistics implements Serializable {
    public int evaluated;
    public int done;
    public int all;
    public EvaluationStatistics(int ev, int don, int notst){
        evaluated = ev;
        done = don;
        all = notst;
    }
}
