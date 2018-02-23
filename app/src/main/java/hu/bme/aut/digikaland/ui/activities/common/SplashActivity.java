package hu.bme.aut.digikaland.ui.activities.common;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.ui.activities.admin.AdminMainActivity;
import hu.bme.aut.digikaland.ui.activities.client.ClientMainActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // itt lesz az adatbazis betoltese, es annak eldontese
        // melyik kepernyo keruljon betoltesre
        try{
            Thread.sleep(2000);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
        // jelenleg csak szimulalunk
        setContentView(R.layout.activity_splash);

        // elinditasa a megfelelo activitynek gombnyomasra
        setEvents();
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
                Intent intent = new Intent(SplashActivity.this, AdminMainActivity.class);
                // TODO: teljes adminsag
                startActivity(intent);
            }
        });

        adminSimple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // mintha feladat admin lenne
                Intent intent = new Intent(SplashActivity.this, AdminMainActivity.class);
                // TODO: feladat adminsag
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
    }
}
