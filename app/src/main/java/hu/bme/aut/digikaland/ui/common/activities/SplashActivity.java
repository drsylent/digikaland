package hu.bme.aut.digikaland.ui.common.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.ui.admin.total.activities.AdminTotalMainActivity;
import hu.bme.aut.digikaland.ui.admin.station.activities.AdminStationMainActivity;
import hu.bme.aut.digikaland.ui.client.activities.ClientMainActivity;
import hu.bme.aut.digikaland.utility.development.MockGenerator;

public class SplashActivity extends AppCompatActivity {

    private final static int milestoneVersion = 0;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private Button testButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // itt lesz az adatbazis betoltese, es annak eldontese
        // melyik kepernyo keruljon betoltesre
        try{
            Thread.sleep(500);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        // jelenleg csak szimulalunk
        setContentView(R.layout.activity_splash);

        // elinditasa a megfelelo activitynek gombnyomasra
        setEvents();
        getData();
    }

    /**
     * Esemenykezelok megalkotasa
     */
    private void setEvents(){
        Button firstEnter = findViewById(R.id.devFirstEnter);
        Button adminTotal = findViewById(R.id.devAdminTotalEnter);
        Button adminSimple = findViewById(R.id.devAdminObjectiveEnter);
        Button clientCaptain = findViewById(R.id.devClientCaptainEnter);
        Button clientSimple = findViewById(R.id.devClientSimpleEnter);
        testButton = findViewById(R.id.devFirebaseTest);

        firstEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // mintha elso inditas lenne
                Intent intent = new Intent(SplashActivity.this, StartupActivity.class);
                startActivity(intent);
            }
        });

        adminTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // mintha teljes admin lenne aki belep
                Intent intent = new Intent(SplashActivity.this, AdminTotalMainActivity.class);
                // TODO: teljes adminsag
                startActivity(intent);
            }
        });

        adminSimple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // mintha feladat admin lenne
                Intent intent = new Intent(SplashActivity.this, AdminStationMainActivity.class);
                // TODO: feladat adminsag - átadni, hogy melyik feladatnak az adminja!
                startActivity(intent);
            }
        });

        clientCaptain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // mintha kapitány kliens lenne
                Intent intent = new Intent(SplashActivity.this, ClientMainActivity.class);
                // TODO: kapitanysag
                startActivity(intent);
            }
        });

        clientSimple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // mintha egyszeru kliens lenne
                Intent intent = new Intent(SplashActivity.this, ClientMainActivity.class);
                // TODO: sima kliens
                startActivity(intent);
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
}
