package hu.bme.aut.digikaland.ui.common.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

public class NewRaceStarter {
    private Context context;

    public NewRaceStarter(Context context){
        this.context = context;
    }

    public Dialog getNewRaceDialog(DialogInterface.OnClickListener listener){
        return new AlertDialog.Builder(context).setTitle("Új verseny").setMessage("Biztos új versenyt szeretnél kezdeni?")
                .setPositiveButton("Igen", listener)
                .setNegativeButton("Nem", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).create();
    }
}
