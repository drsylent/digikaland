package hu.bme.aut.digikaland.entities;

import java.io.Serializable;

/**
 * Egy osztály, mely összefoglalja, egy állomáson mi a kiértékelés állapota.
 */
public class EvaluationStatistics implements Serializable {
    public int evaluated;
    public int done;
    public int all;
    public EvaluationStatistics(int ev, int don, int all){
        evaluated = ev;
        done = don;
        this.all = all;
    }
}
