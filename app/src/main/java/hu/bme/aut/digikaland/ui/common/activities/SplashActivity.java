package hu.bme.aut.digikaland.ui.common.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.dblogic.CodeHandler;
import hu.bme.aut.digikaland.dblogic.ErrorType;
import hu.bme.aut.digikaland.dblogic.RacePermissionHandler;
import hu.bme.aut.digikaland.ui.admin.total.activities.AdminTotalMainActivity;
import hu.bme.aut.digikaland.ui.admin.station.activities.AdminStationMainActivity;
import hu.bme.aut.digikaland.ui.client.activities.ClientMainActivity;
import hu.bme.aut.digikaland.utility.development.MockGenerator;

public class SplashActivity extends AppCompatActivity implements RacePermissionHandler.CommunicationInterface {

    private final static int milestoneVersion = 0;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button testButton;
    private LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // itt lesz az adatbazis betoltese, es annak eldontese
        // melyik kepernyo keruljon betoltesre
//        try{
//            Thread.sleep(500);
//        }catch(InterruptedException e){
//            e.printStackTrace();
//        }
        // jelenleg csak szimulalunk
        setContentView(R.layout.activity_splash);

        mainLayout = findViewById(R.id.splash_layout);

        // elinditasa a megfelelo activitynek gombnyomasra
        setEvents();

        //getData();
        if(!RacePermissionHandler.getInstance(this).startUp(getSharedPreferences(CodeHandler.SharedPreferencesName ,MODE_PRIVATE))){
            firstEnter();
        }
    }

    private void firstEnter(){
        Intent intent = new Intent(SplashActivity.this, StartupActivity.class);
        startActivity(intent);
    }

    private void adminTotalEnter(){
        // mintha teljes admin lenne aki belep
        Intent intent = new Intent(SplashActivity.this, AdminTotalMainActivity.class);
        // TODO: teljes adminsag
        startActivity(intent);
    }

    private void adminStationEnter(){
        // mintha feladat admin lenne
        Intent intent = new Intent(SplashActivity.this, AdminStationMainActivity.class);
        // TODO: feladat adminsag - átadni, hogy melyik feladatnak az adminja!
        startActivity(intent);
    }

    private void clientCaptainEnter(){
        // mintha kapitány kliens lenne
        Intent intent = new Intent(SplashActivity.this, ClientMainActivity.class);
        // TODO: kapitanysag
        startActivity(intent);
    }

    private void clientNormalEnter(){
        // mintha egyszeru kliens lenne
        Intent intent = new Intent(SplashActivity.this, ClientMainActivity.class);
        // TODO: sima kliens
        startActivity(intent);
    }

    /**
     * Esemenykezelok megalkotasa
     */
    private void setEvents(){
        final Button firstEnter = findViewById(R.id.devFirstEnter);
        Button adminTotal = findViewById(R.id.devAdminTotalEnter);
        Button adminSimple = findViewById(R.id.devAdminObjectiveEnter);
        Button clientCaptain = findViewById(R.id.devClientCaptainEnter);
        Button clientSimple = findViewById(R.id.devClientSimpleEnter);
        testButton = findViewById(R.id.devFirebaseTest);

        firstEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstEnter();
            }
        });

        adminTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                adminTotalEnter();
            }
        });

        adminSimple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            adminStationEnter();
            }
        });

        clientCaptain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            clientCaptainEnter();
            }
        });

        clientSimple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            clientNormalEnter();
            }
        });

        if(milestoneVersion == 1){
            adminTotal.setEnabled(false);
            adminSimple.setEnabled(false);
            clientSimple.setEnabled(false);
            final Context that = this;
            MockGenerator.introDialog(this, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    MockGenerator.milestoneOneDialog(that).show();
                }
            }).show();
        }
        if(milestoneVersion == 2){
            final Context that = this;
            MockGenerator.introDialog(this, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    MockGenerator.milestoneTwoDialog(that).show();
                }
            }).show();
        }
    }

    private void setTestButtonText(String text){
        testButton.setText(text);
    }

    private void getData(){
        DocumentReference docRef = db.collection("test").document("testdocument");
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        Log.e("Firebase", "DocumentSnapshot data: " + document.getData().get("teststring"));
                        setTestButtonText((String) document.getData().get("teststring"));
                    } else {
                        Log.e("Firabase", "No such document");
                    }
                } else {
                    Log.e("Firabase", "get failed with ", task.getException());
                }
            }
        });
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
        RacePermissionHandler rph = RacePermissionHandler.getInstance(this);
        if(rph.getMainMode() == RacePermissionHandler.MainMode.Admin){
            if(rph.getAdminMode() == RacePermissionHandler.AdminMode.Total){
                adminTotalEnter();
            }
            else{
                adminStationEnter();
            }
        }
        else{
            if(rph.getClientMode() == RacePermissionHandler.ClientMode.Captain){
                clientCaptainEnter();
            }
            else{
                clientNormalEnter();
            }
        }
    }
}
