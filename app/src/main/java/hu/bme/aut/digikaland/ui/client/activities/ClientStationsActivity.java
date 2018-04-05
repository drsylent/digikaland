package hu.bme.aut.digikaland.ui.client.activities;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import java.util.ArrayList;
import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.entities.station.StationClientPerspective;
import hu.bme.aut.digikaland.entities.objectives.Objective;
import hu.bme.aut.digikaland.ui.client.fragments.ClientStationAdapter;

public class ClientStationsActivity extends AppCompatActivity implements ClientStationAdapter.ClientStationListener {
    public final static String ARGS_STATIONS = "stations";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_stations);
        ActionBar toolbar = getSupportActionBar();
        if(toolbar != null){
            toolbar.setDisplayHomeAsUpEnabled(true);
            toolbar.setTitle(R.string.stations);
        }
        ArrayList<StationClientPerspective> stations = (ArrayList<StationClientPerspective>) getIntent().getBundleExtra(ARGS_STATIONS).getSerializable(ARGS_STATIONS);
        RecyclerView list = findViewById(R.id.clientStationList);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(new ClientStationAdapter(stations));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return true;
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
    public void onStartedStationClick(StationClientPerspective item) {
        ArrayList<Objective> objectives = item.station.getObjectives();
        if(objectives != null) {
            Intent i = new Intent(ClientStationsActivity.this, ClientObjectiveActivity.class);
            i.putExtra(ClientObjectiveActivity.ARGS_OBJECTIVES, objectives);
            startActivity(i);
        }
    }
}

