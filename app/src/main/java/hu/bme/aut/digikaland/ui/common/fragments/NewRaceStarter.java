package hu.bme.aut.digikaland.ui.common.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.dblogic.CodeHandler;
import hu.bme.aut.digikaland.dblogic.RaceRoleHandler;
import hu.bme.aut.digikaland.ui.common.activities.StartupActivity;

import static android.content.Context.MODE_PRIVATE;

/**
 * Ezzel az osztállyal könnyen el lehet indítani egy új versenyt.
 */
public class NewRaceStarter {
    /**
     * Egy dialógust dob fel arról, hogy a felhasználó új versenyt szeretne-e kezdeni.
     * Ha igen, akkor elvégzi a UI-adminisztratiív teendőket, hogy a kódkérési képernyő
     * kerüljön ismét előtérbe.
     * @param activity Az activity, amely kezdeményezi a dialógust.
     * @return A dialógus, melyet meg kell jeleníteni.
     */
    public static Dialog getNewRaceDialog(final AppCompatActivity activity){
        return new AlertDialog.Builder(activity).setTitle(R.string.new_race)
                .setMessage(R.string.sure_new_race)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        CodeHandler.getInstance().deleteCodes(activity
                                .getSharedPreferences(CodeHandler.SharedPreferencesName, MODE_PRIVATE));
                        RaceRoleHandler.reset();
                        Intent intent = new Intent(activity, StartupActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        activity.startActivity(intent);
                        activity.finishAffinity();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).create();
    }
}
