package hu.bme.aut.digikaland.ui.common.activities;

import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.ui.admin.AdminMainActivity;
import hu.bme.aut.digikaland.ui.client.activities.ClientMainActivity;
import hu.bme.aut.digikaland.ui.common.fragments.PrimaryCodeFragment;
import hu.bme.aut.digikaland.ui.common.fragments.SecondaryCodeFragment;

public class StartupActivity extends AppCompatActivity implements PrimaryCodeFragment.PrimaryCodeReady, SecondaryCodeFragment.SecondaryCodeReady {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(R.id.startupLayout, PrimaryCodeFragment.newInstance());
        ft.commit();
    }

    String raceCode;

    @Override
    public void onPrimaryCodeHit(String raceCode) {
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
        Intent intent;
        if(raceCode.toUpperCase().equals("ADMIN")){
            if(roleCode.toUpperCase().equals("TOTAL")){
                intent = new Intent(StartupActivity.this, AdminMainActivity.class);
            }
            else{
                intent = new Intent(StartupActivity.this, AdminMainActivity.class);
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
