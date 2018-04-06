package hu.bme.aut.digikaland.dblogic;

import android.content.SharedPreferences;
import android.util.Log;

/**
 * Ez az osztály kezeli a beírt kódokat, mert perzisztálni kell őket, hogy ha újraindul az alkalmazás,
 * egyből vissza lehessen csatlakozni a játékba megfelelő móddal.
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

    public boolean initialize(SharedPreferences preferences){
        boolean exists = preferences.contains(RaceCodeString) && preferences.contains(RoleCodeString);
        if(exists){
            RaceCode = preferences.getString(RaceCodeString, null);
            RoleCode = preferences.getString(RoleCodeString, null);
        }
        return exists;
    }

    public void setCodes(String race, String role, SharedPreferences preferences){
        deleteCodes(preferences);
        RaceCode = race;
        RoleCode = role;
        preferences.edit().putString(RaceCodeString, RaceCode).putString(RoleCodeString, RoleCode).apply();
        Log.e("CodeHandler", race + " " + role);
    }

    public void deleteCodes(SharedPreferences preferences){
        RaceCode = null;
        RoleCode = null;
        preferences.edit().remove(RaceCodeString).remove(RoleCodeString).apply();
    }
}
