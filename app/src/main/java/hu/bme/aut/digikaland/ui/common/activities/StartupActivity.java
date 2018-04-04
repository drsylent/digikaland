package hu.bme.aut.digikaland.ui.common.activities;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.regex.Pattern;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.ui.admin.total.activities.AdminTotalMainActivity;
import hu.bme.aut.digikaland.ui.admin.station.activities.AdminStationMainActivity;
import hu.bme.aut.digikaland.ui.client.activities.ClientMainActivity;
import hu.bme.aut.digikaland.ui.common.fragments.PrimaryCodeFragment;
import hu.bme.aut.digikaland.ui.common.fragments.SecondaryCodeFragment;

public class StartupActivity extends AppCompatActivity implements PrimaryCodeFragment.PrimaryCodeReady, SecondaryCodeFragment.SecondaryCodeReady {
    private static final String ARG_RACECODE = "code";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        if(savedInstanceState == null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.startupLayout, PrimaryCodeFragment.newInstance());
            ft.commit();
        }
        else{
            raceCode = savedInstanceState.getString(ARG_RACECODE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_RACECODE, raceCode);
    }

    String raceCode = null;

    private boolean inputValidator(String string){
        return Pattern.matches("\\w{0,8}", string);
    }

    @Override
    public void onPrimaryCodeHit(String raceCode) {
        if(!inputValidator(raceCode)) return;
        // tervezo modba lepes
        if(raceCode.toUpperCase().equals("PLANMACH"))
            return;
        // itt tortenik az adatbazishoz csatlakozas
        // megkeresni a versenyt, ami ezzel a koddal van
        // ha nincs, jelezzuk a felhasznalonak
        this.raceCode = raceCode;
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.startupLayout, SecondaryCodeFragment.newInstance("RaceTitle"));
        ft.addToBackStack(null);
        ft.commit();
    }

    // TODO: ne lehessen visszaterni erre az activityre
    @Override
    public void onSecondaryCodeHit(String roleCode) {
        if(!inputValidator(raceCode)) return;
        Intent intent;
        if(raceCode.toUpperCase().equals("ADMIN")){
            if(roleCode.toUpperCase().equals("TOTAL")){
                intent = new Intent(StartupActivity.this, AdminTotalMainActivity.class);
            }
            else{
                intent = new Intent(StartupActivity.this, AdminStationMainActivity.class);
            }
        }
        else{
            if(roleCode.toUpperCase().equals("CAPTAIN")){
                intent = new Intent(StartupActivity.this, ClientMainActivity.class);
            }
            else{
                intent = new Intent(StartupActivity.this, ClientMainActivity.class);
            }
        }
        startActivity(intent);
    }
}
