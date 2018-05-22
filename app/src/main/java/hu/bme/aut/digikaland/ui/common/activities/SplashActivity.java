package hu.bme.aut.digikaland.ui.common.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.dblogic.CodeHandler;
import hu.bme.aut.digikaland.dblogic.enumeration.ErrorType;
import hu.bme.aut.digikaland.dblogic.RaceRoleHandler;
import hu.bme.aut.digikaland.ui.admin.total.activities.AdminTotalMainActivity;
import hu.bme.aut.digikaland.ui.admin.station.activities.AdminStationMainActivity;
import hu.bme.aut.digikaland.ui.client.activities.ClientMainActivity;

public class SplashActivity extends AppCompatActivity implements RaceRoleHandler.RaceRoleCommunicationInterface {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // itt lesz az adatbazis betoltese, es annak eldontese
        // melyik kepernyo keruljon betoltesre
        setContentView(R.layout.activity_splash);

        if(!RaceRoleHandler.getInstance(this).startUp(getSharedPreferences(CodeHandler.SharedPreferencesName ,MODE_PRIVATE))){
            firstEnter();
        }
    }

    private void firstEnter(){
        Intent intent = new Intent(SplashActivity.this, StartupActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    private void adminTotalEnter(){
        Intent intent = new Intent(SplashActivity.this, AdminTotalMainActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    private void adminStationEnter(){
        Intent intent = new Intent(SplashActivity.this, AdminStationMainActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    private void clientCaptainEnter(){
        Intent intent = new Intent(SplashActivity.this, ClientMainActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    private void clientNormalEnter(){
        Intent intent = new Intent(SplashActivity.this, ClientMainActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    @Override
    public void permissionError(ErrorType type) {
        CodeHandler.getInstance().deleteCodes(getSharedPreferences(CodeHandler.SharedPreferencesName, MODE_PRIVATE));
        firstEnter();
    }

    @Override
    public void permissionReady() {
        if(RaceRoleHandler.getMainMode() == RaceRoleHandler.MainMode.Admin){
            if(RaceRoleHandler.getAdminMode() == RaceRoleHandler.AdminMode.Total){
                adminTotalEnter();
            }
            else{
                adminStationEnter();
            }
        }
        else{
            if(RaceRoleHandler.getClientMode() == RaceRoleHandler.ClientMode.Captain){
                clientCaptainEnter();
            }
            else{
                clientNormalEnter();
            }
        }
    }
}
