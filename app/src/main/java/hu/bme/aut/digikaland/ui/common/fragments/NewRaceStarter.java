package hu.bme.aut.digikaland.ui.common.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import hu.bme.aut.digikaland.dblogic.CodeHandler;
import hu.bme.aut.digikaland.dblogic.RaceRoleHandler;
import hu.bme.aut.digikaland.ui.common.activities.StartupActivity;

import static android.content.Context.MODE_PRIVATE;

public class NewRaceStarter {

    public static Dialog getNewRaceDialog(final AppCompatActivity activity){
        return new AlertDialog.Builder(activity).setTitle("Új verseny").setMessage("Biztos új versenyt szeretnél kezdeni?")
                .setPositiveButton("Igen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        CodeHandler.getInstance().deleteCodes(activity.getSharedPreferences(CodeHandler.SharedPreferencesName, MODE_PRIVATE));
                        RaceRoleHandler.reset();
                        Intent intent = new Intent(activity, StartupActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        activity.startActivity(intent);
                        activity.finishAffinity();
                    }
                })
                .setNegativeButton("Nem", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).create();
    }
}
