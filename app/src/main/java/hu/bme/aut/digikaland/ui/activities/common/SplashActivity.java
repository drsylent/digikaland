package hu.bme.aut.digikaland.ui.activities.common;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import hu.bme.aut.digikaland.R;

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
            }
        });

        adminTotal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // mintha teljes admin lenne aki belep
            }
        });

        adminSimple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // mintha feladat admin lenne
            }
        });

        clientCaptain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // mintha kapitány kliens lenne
            }
        });

        clientSimple.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // mintha egyszeru kliens lenne
            }
        });
    }
}
