package hu.bme.aut.digikaland.ui.common.activities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.regex.Pattern;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.dblogic.CodeHandler;
import hu.bme.aut.digikaland.dblogic.enumeration.ErrorType;
import hu.bme.aut.digikaland.dblogic.RaceNameEngine;
import hu.bme.aut.digikaland.dblogic.RaceRoleHandler;
import hu.bme.aut.digikaland.ui.admin.total.activities.AdminTotalMainActivity;
import hu.bme.aut.digikaland.ui.admin.station.activities.AdminStationMainActivity;
import hu.bme.aut.digikaland.ui.client.activities.ClientMainActivity;
import hu.bme.aut.digikaland.ui.common.fragments.PrimaryCodeFragment;
import hu.bme.aut.digikaland.ui.common.fragments.SecondaryCodeFragment;

public class StartupActivity extends AppCompatActivity implements PrimaryCodeFragment.PrimaryCodeReady, SecondaryCodeFragment.SecondaryCodeReady,
        RaceNameEngine.RaceNameCommunicationInterface, RaceRoleHandler.RaceRoleCommunicationInterface {
    private static final String ARG_RACECODE = "racecode";
    private static final String ARG_ROLECODE = "rolecode";

    private LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);
        mainLayout = findViewById(R.id.startupLayout);
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
        outState.putString(ARG_ROLECODE, roleCode);
    }

    String raceCode = null;
    String roleCode = null;

    private boolean isValidInput(String string){
        return Pattern.matches("\\w{0,8}", string);
    }
    private Button disabledButton;
    // le kell tiltani a gombot, hogy ne nyomogassa a felhasználó
    @Override
    public void onPrimaryCodeHit(String raceCode, Button button) {
        if(!isValidInput(raceCode)){
            raceNameLoadingError(ErrorType.IllegalCharacter);
            return;
        }
        if(raceCode.toUpperCase().equals("NFC"))
            startActivity(new Intent(StartupActivity.this, NFCActivity.class));
        button.setEnabled(false);
        this.raceCode = raceCode;
        disabledButton = button;
        RaceNameEngine.getInstance(this).loadRaceName(raceCode);
    }

    @Override
    public void onSecondaryCodeHit(String roleCode, Button button) {
        if(!isValidInput(roleCode)){
            permissionError(ErrorType.IllegalCharacter);
            return;
        }
        button.setEnabled(false);
        this.roleCode = roleCode;
        disabledButton = button;
        RaceRoleHandler.getInstance(this).loadPermissionData(raceCode, roleCode);
    }

    private void showSnackBarMessage(String message) {
        Snackbar.make(mainLayout, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void raceNameLoaded(String result) {
        disabledButton.setEnabled(true);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.startupLayout, SecondaryCodeFragment.newInstance(result));
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void raceNameLoadingError(ErrorType type) {
        disabledButton.setEnabled(true);
        showSnackBarMessage(type.getDefaultMessage());
    }

    @Override
    public void permissionError(ErrorType type) {
        disabledButton.setEnabled(true);
        showSnackBarMessage(type.getDefaultMessage());
    }

    @Override
    public void permissionReady() {
        disabledButton.setEnabled(true);
        CodeHandler.getInstance().setCodes(raceCode, roleCode, getSharedPreferences(CodeHandler.SharedPreferencesName, MODE_PRIVATE));
        Intent intent;
        if(RaceRoleHandler.getMainMode() == RaceRoleHandler.MainMode.Admin){
            if(RaceRoleHandler.getAdminMode() == RaceRoleHandler.AdminMode.Total){
                intent = new Intent(StartupActivity.this, AdminTotalMainActivity.class);
            }
            else{
                intent = new Intent(StartupActivity.this, AdminStationMainActivity.class);
            }
        }
        else{
            if(RaceRoleHandler.getClientMode() == RaceRoleHandler.ClientMode.Captain){
                intent = new Intent(StartupActivity.this, ClientMainActivity.class);
            }
            else{
                intent = new Intent(StartupActivity.this, ClientMainActivity.class);
            }
        }
        startActivity(intent);
        finishAffinity();
    }
}
