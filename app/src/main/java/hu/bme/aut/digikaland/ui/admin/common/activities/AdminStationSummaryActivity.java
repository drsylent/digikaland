package hu.bme.aut.digikaland.ui.admin.common.activities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import hu.bme.aut.digikaland.AdminStationEngine;
import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.dblogic.ErrorType;
import hu.bme.aut.digikaland.entities.Contact;
import hu.bme.aut.digikaland.entities.EvaluationStatistics;
import hu.bme.aut.digikaland.entities.Location;
import hu.bme.aut.digikaland.entities.Team;
import hu.bme.aut.digikaland.entities.station.StationAdminPerspective;
import hu.bme.aut.digikaland.ui.common.activities.MapsActivity;
import hu.bme.aut.digikaland.ui.common.fragments.ContactFragment;
import hu.bme.aut.digikaland.ui.common.fragments.TextFragment;
import hu.bme.aut.digikaland.utility.development.MockGenerator;

public class AdminStationSummaryActivity extends AppCompatActivity implements AdminStationEngine.CommunicationInterface {
    public final static String ARG_STATIONID = "stationid";
    public final static String ARG_LOCATION = "loc";
    public final static String ARG_CONTACT = "contact";
    public final static String ARG_STATUS = "status";

    private LinearLayout mainLayout;
    private int stationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_station_summary);
        ActionBar toolbar = getSupportActionBar();
        if(toolbar != null){
            toolbar.setDisplayHomeAsUpEnabled(true);
            toolbar.setTitle(R.string.station);
        }
        mainLayout = findViewById(R.id.stationSummaryLayout);
        stationId = getIntent().getIntExtra(ARG_STATIONID, -1);
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
                prepareTeams();
            }
        });
    }

    public void startMap(){
        Intent i = new Intent(AdminStationSummaryActivity.this, MapsActivity.class);
        // TODO: mindig a megfelelő állomásnak a helyszíne jelnjen itt meg
        i.putExtra(MapsActivity.MARKER_LOCATIONS, MockGenerator.mockMapData());
        startActivity(i);
    }

    public void prepareTeams(){
        AdminStationEngine.getInstance(this).loadTeamList(Integer.toString(stationId));
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

    @Override
    public void adminStationError(ErrorType type) {
        showSnackBarMessage(type.getDefaultMessage());
    }

    @Override
    public void allStationLoadCompleted(ArrayList<StationAdminPerspective> list) {

    }

    @Override
    public void stationSummaryLoaded(String stationId, Location location, ArrayList<Contact> stationAdmins) {

    }

    @Override
    public void allTeamStatusLoaded(ArrayList<Team> teams) {
        setTeams(teams);
    }

    private void setTeams(ArrayList<Team> teams){
        Bundle teamData = new Bundle();
        teamData.putSerializable(AdminTeamsActivity.ARG_TEAMS, teams);
        goToTeams(teamData);
    }

    private void goToTeams(Bundle teamData){
        Intent i = new Intent(AdminStationSummaryActivity.this, AdminTeamsActivity.class);
        i.putExtra(AdminTeamsActivity.ARG_TEAMS, teamData);
        startActivity(i);
    }

    // TODO: jelenleg csak placeholder megjelenítésre
    private void showSnackBarMessage(String message) {
        Snackbar.make(mainLayout, message, Snackbar.LENGTH_LONG).show();
    }
}
