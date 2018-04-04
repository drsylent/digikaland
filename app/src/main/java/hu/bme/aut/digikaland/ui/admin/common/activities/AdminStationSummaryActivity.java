package hu.bme.aut.digikaland.ui.admin.common.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.entities.Contact;
import hu.bme.aut.digikaland.ui.common.activities.MapsActivity;
import hu.bme.aut.digikaland.ui.common.fragments.ContactFragment;
import hu.bme.aut.digikaland.ui.common.fragments.TextFragment;
import hu.bme.aut.digikaland.utility.development.MockGenerator;

public class AdminStationSummaryActivity extends AppCompatActivity {
    public final static String ARG_STATIONID = "stationid";
    public final static String ARG_LOCATION = "loc";
    public final static String ARG_SUBLOCATION = "subloc";
    public final static String ARG_CONTACT = "contact";
    public final static String ARG_EVALUATED = "eval";
    public final static String ARG_DONE = "done";
    public final static String ARG_SUM = "sum";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_station_summary);
        ActionBar toolbar = getSupportActionBar();
        if(toolbar != null){
            toolbar.setDisplayHomeAsUpEnabled(true);
            toolbar.setTitle(R.string.station);
        }
        if(savedInstanceState == null){
            int stationId = getIntent().getIntExtra(ARG_STATIONID, -1);
            String location = getIntent().getStringExtra(ARG_LOCATION);
            String sublocation = getIntent().getStringExtra(ARG_SUBLOCATION);
            Contact contact = (Contact) getIntent().getSerializableExtra(ARG_CONTACT);
            int evaluated = getIntent().getIntExtra(ARG_EVALUATED, -1);
            int done = getIntent().getIntExtra(ARG_DONE, -1);
            int sum = getIntent().getIntExtra(ARG_SUM, -1);
            TextView tvStationId = findViewById(R.id.adminStationSummaryId);
            tvStationId.setText(getResources().getString(R.string.station_id, stationId));
            TextView tvLocation = findViewById(R.id.adminStationSummaryLocation);
            tvLocation.setText(location);
            TextView tvSubLocation = findViewById(R.id.adminStationSummarySubLocation);
            tvSubLocation.setText(sublocation);
            if(contact != null) getSupportFragmentManager().beginTransaction().add(R.id.adminStationSummaryAdminContent,
                    ContactFragment.newInstance(contact, true)).commit();
            else getSupportFragmentManager().beginTransaction().add(R.id.adminStationSummaryAdminContent,
                    TextFragment.newInstance("Nincs felelőse az állomásnak", true)).commit();
            TextView tvStatus = findViewById(R.id.adminStationSummaryStatus);
            tvStatus.setText(getResources().getString(R.string.tri_status, evaluated, done, sum));
            // TODO: mi van, ha nincs helyszíne egy állomásnak? le kell "tiltani" a gombot, és a helyszínt is
            Button bMap = findViewById(R.id.adminStationSummaryMap);
            bMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startMap();
                }
            });
            Button bTeams = findViewById(R.id.adminStationSummaryTeams);
            bTeams.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startTeams();
                }
            });
        }
    }

    public void startMap(){
        Intent i = new Intent(AdminStationSummaryActivity.this, MapsActivity.class);
        // TODO: mindig a megfelelő állomásnak a helyszíne jelnjen itt meg
        i.putExtra(MapsActivity.MARKER_LOCATIONS, MockGenerator.mockMapData());
        startActivity(i);
    }

    public void startTeams(){
        Intent i = new Intent(AdminStationSummaryActivity.this, AdminTeamsActivity.class);
        i.putExtra(AdminTeamsActivity.ARG_TEAMS, MockGenerator.mockAdminTeamsList());
        startActivity(i);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
