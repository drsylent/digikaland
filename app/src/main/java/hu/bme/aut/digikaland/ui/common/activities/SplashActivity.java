package hu.bme.aut.digikaland.ui.common.activities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.firestore.FirebaseFirestore;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.dblogic.CodeHandler;
import hu.bme.aut.digikaland.dblogic.enumeration.ErrorType;
import hu.bme.aut.digikaland.dblogic.RaceRoleHandler;
import hu.bme.aut.digikaland.ui.admin.total.activities.AdminTotalMainActivity;
import hu.bme.aut.digikaland.ui.admin.station.activities.AdminStationMainActivity;
import hu.bme.aut.digikaland.ui.client.activities.ClientMainActivity;
import hu.bme.aut.digikaland.utility.development.MockGenerator;

public class SplashActivity extends AppCompatActivity implements RaceRoleHandler.RaceRoleCommunicationInterface {

    private final static int milestoneVersion = 0;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button testButton;
    private LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // itt lesz az adatbazis betoltese, es annak eldontese
        // melyik kepernyo keruljon betoltesre
        setContentView(R.layout.activity_splash);

        mainLayout = findViewById(R.id.splash_layout);

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
        // mintha teljes admin lenne aki belep
        Intent intent = new Intent(SplashActivity.this, AdminTotalMainActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    private void adminStationEnter(){
        // mintha feladat admin lenne
        Intent intent = new Intent(SplashActivity.this, AdminStationMainActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    private void clientCaptainEnter(){
        // mintha kapitány kliens lenne
        Intent intent = new Intent(SplashActivity.this, ClientMainActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    private void clientNormalEnter(){
        // mintha egyszeru kliens lenne
        Intent intent = new Intent(SplashActivity.this, ClientMainActivity.class);
        startActivity(intent);
        finishAffinity();
    }

    // TODO: jelenleg csak placeholder megjelenítésre
    private void showSnackBarMessage(String message) {
        Snackbar.make(mainLayout, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void permissionError(ErrorType type) {
        CodeHandler.getInstance().deleteCodes(getSharedPreferences(CodeHandler.SharedPreferencesName, MODE_PRIVATE));
        Log.e("Splash permission error", type.getDefaultMessage());
        firstEnter();
    }

    @Override
    public void permissionReady() {
        RaceRoleHandler rph = RaceRoleHandler.getInstance(this);
        if(rph.getMainMode() == RaceRoleHandler.MainMode.Admin){
            if(rph.getAdminMode() == RaceRoleHandler.AdminMode.Total){
                adminTotalEnter();
            }
            else{
                adminStationEnter();
            }
        }
        else{
            if(rph.getClientMode() == RaceRoleHandler.ClientMode.Captain){
                clientCaptainEnter();
            }
            else{
                clientNormalEnter();
            }
        }
    }
}
