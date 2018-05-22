package hu.bme.aut.digikaland.dblogic;

import android.content.SharedPreferences;

/**
 * Ez a szolgáltatás menti el a versenyen használt kódokat az alkalmazás futásának idejére,
 * illetve perzisztálja is őket, hogy visszalépéskor egyből vissza lehessen csatlazkoni.
 */
public class CodeHandler {
    private static final CodeHandler ourInstance = new CodeHandler();
    private final static String RaceCodeString = "RaceCode";
    private final static String RoleCodeString = "RoleCode";
    public final static String SharedPreferencesName = "CodePreferences";

    public static CodeHandler getInstance() {
        return ourInstance;
    }

    private CodeHandler() {
    }

    private String RaceCode = null;
    private String RoleCode = null;

    public String getRaceCode() {
        return RaceCode;
    }

    public String getRoleCode() {
        return RoleCode;
    }

    /**
     * Elvégzi a kódok inicializálást a megadott SharedPreferencesből.
     * @param preferences A SharedPreferences, melyből kinyerhetők a kódok.
     * @return Sikeres volt-e az inicializálás.
     */
    public boolean initialize(SharedPreferences preferences){
        boolean exists = preferences.contains(RaceCodeString)
                && preferences.contains(RoleCodeString);
        if(exists){
            RaceCode = preferences.getString(RaceCodeString, null);
            RoleCode = preferences.getString(RoleCodeString, null);
        }
        return exists;
    }

    /**
     * Beállítja a megadott kódokat a megadott SharedPreferences-be.
     * @param race A versenykód, melyet el kell menteni.
     * @param role A szerepkód, melyet el kell menteni.
     * @param preferences A SharedPreferences, melybe a mentés történik.
     */
    public void setCodes(String race, String role, SharedPreferences preferences){
        deleteCodes(preferences);
        RaceCode = race;
        RoleCode = role;
        preferences.edit().putString(RaceCodeString, RaceCode)
                .putString(RoleCodeString, RoleCode).apply();
    }

    /**
     * A megadott SharedPreferencesből, és a szolgáltatásból törli a beállított kódokat.
     * @param preferences A SharedPreferences, melyből ki kell törölni a kódokat.
     */
    public void deleteCodes(SharedPreferences preferences){
        RaceCode = null;
        RoleCode = null;
        preferences.edit().remove(RaceCodeString).remove(RoleCodeString).apply();
    }
}
