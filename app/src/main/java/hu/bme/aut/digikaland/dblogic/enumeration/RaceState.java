package hu.bme.aut.digikaland.dblogic.enumeration;

/**
 * Mi a verseny jelenlegi állapota.
 */
public enum RaceState {
    /**
     * A verseny még nem kezdődött el.
     */
    NotStarted,
    /**
     * A verseny már elkezdődöt.
     */
    Started,
    /**
     * A verseny befejeződött.
     */
    Ended
}
