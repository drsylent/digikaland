package hu.bme.aut.digikaland.dblogic.enumeration;

/**
 * A játék állapota micsoda egy felhasználó szemszögéből - mi lett a fő betöltés eredménye.
 */
public enum LoadResult {
    /**
     * A verseny épp elindul / még nem kezdődött el.
     */
    Starting,
    /**
     * A verseny már megy, és úton vannak a versenyzők.
     */
    Running,
    /**
     * A kliens jelenleg egy állomáson feladatot old.
     */
    Station,
    /**
     * A verseny a végénél tart.
     */
    Ending
}
