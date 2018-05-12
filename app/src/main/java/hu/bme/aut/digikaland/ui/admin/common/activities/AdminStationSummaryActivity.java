package hu.bme.aut.digikaland.ui.admin.common.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.entities.Contact;
import hu.bme.aut.digikaland.entities.EvaluationStatistics;
import hu.bme.aut.digikaland.entities.Location;
import hu.bme.aut.digikaland.ui.common.activities.MapsActivity;
import hu.bme.aut.digikaland.ui.common.fragments.ContactFragment;
import hu.bme.aut.digikaland.ui.common.fragments.TextFragment;
import hu.bme.aut.digikaland.utility.development.MockGenerator;

public class AdminStationSummaryActivity extends AppCompatActivity {
    public final static String ARG_STATIONID = "stationid";
    public final static String ARG_LOCATION = "loc";
    public final static String ARG_CONTACT = "contact";
    public final static String ARG_STATUS = "status";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_station_summary);
        ActionBar toolbar = getSupportActionBar();
        if(toolbar != null){
            toolbar.setDisplayHomeAsUpEnabled(true);
            toolbar.setTitle(R.string.station);
        }

        int stationId = getIntent().getIntExtra(ARG_STATIONID, -1);
        Location location = (Location) getIntent().getSerializableExtra(ARG_LOCATION);
        ArrayList<Contact> contacts = (ArrayList<Contact>) getIntent().getSerializableExtra(ARG_CONTACT);
        EvaluationStatistics status = (EvaluationStatistics) getIntent().getSerializableExtra(ARG_STATUS);
        TextView tvStationId = findViewById(R.id.adminStationSummaryId);
        tvStationId.setText(getResources().getString(R.string.station_id, stationId));
        TextView tvLocation = findViewById(R.id.adminStationSummaryLocation);
        TextView tvSubLocation = findViewById(R.id.adminStationSummarySubLocation);
        Button bMap = findViewById(R.id.adminStationSummaryMap);
        if(location != null){
            tvLocation.setText(location.main);
            tvSubLocation.setText(location.detailed);
            bMap.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startMap();
                }
            });
        }
        else{
            tvLocation.setText(R.string.no_location_station);
            tvSubLocation.setText(R.string.solve_on_the_route);
            bMap.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        }
        if(savedInstanceState == null) {
            if (contacts != null)
                for(Contact contact : contacts)
                getSupportFragmentManager().beginTransaction().add(R.id.adminStationSummaryAdminContent,
                        ContactFragment.newInstance(contact, true)).commit();
            else
                getSupportFragmentManager().beginTransaction().add(R.id.adminStationSummaryAdminContent,
                        TextFragment.newInstance("Nincs felelőse az állomásnak", true)).commit();
        }
        TextView tvStatus = findViewById(R.id.adminStationSummaryStatus);
        tvStatus.setText(getResources().getString(R.string.tri_status, status.evaluated, status.done, status.all));
        Button bTeams = findViewById(R.id.adminStationSummaryTeams);
        bTeams.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTeams();
            }
        });
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
