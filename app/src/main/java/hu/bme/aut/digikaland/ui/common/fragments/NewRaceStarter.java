package hu.bme.aut.digikaland.ui.common.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;

import hu.bme.aut.digikaland.dblogic.CodeHandler;
import hu.bme.aut.digikaland.ui.admin.total.activities.AdminTotalMainActivity;
import hu.bme.aut.digikaland.ui.common.activities.StartupActivity;

import static android.content.Context.MODE_PRIVATE;

public class NewRaceStarter {

    public static Dialog getNewRaceDialog(final Context context){
        return new AlertDialog.Builder(context).setTitle("Új verseny").setMessage("Biztos új versenyt szeretnél kezdeni?")
                .setPositiveButton("Igen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        CodeHandler.getInstance().deleteCodes(context.getSharedPreferences(CodeHandler.SharedPreferencesName, MODE_PRIVATE));
                        Intent intent = new Intent(context, StartupActivity.class);
                        context.startActivity(intent);
                    }
                })
                .setNegativeButton("Nem", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).create();
    }
}
