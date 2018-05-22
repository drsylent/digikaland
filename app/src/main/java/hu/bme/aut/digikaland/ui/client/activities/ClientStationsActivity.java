package hu.bme.aut.digikaland.ui.client.activities;

import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import java.util.ArrayList;
import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.dblogic.enumeration.ErrorType;
import hu.bme.aut.digikaland.dblogic.ObjectiveEngine;
import hu.bme.aut.digikaland.dblogic.RaceRoleHandler;
import hu.bme.aut.digikaland.dblogic.StationsEngine;
import hu.bme.aut.digikaland.entities.station.StationClientPerspective;
import hu.bme.aut.digikaland.entities.objectives.Objective;
import hu.bme.aut.digikaland.ui.client.fragments.ClientStationAdapter;

public class ClientStationsActivity extends AppCompatActivity implements ClientStationAdapter.ClientStationListener, StationsEngine.CommunicationInterface,
        ObjectiveEngine.ObjectiveCommunicationInterface {
    public final static String ARGS_STATIONS = "stations";

    RecyclerView mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_stations);
        ActionBar toolbar = getSupportActionBar();
        if(toolbar != null){
            toolbar.setDisplayHomeAsUpEnabled(true);
            toolbar.setTitle(R.string.stations);
        }
        // mockolt
        //ArrayList<StationClientPerspective> stations = (ArrayList<StationClientPerspective>) getIntent().getBundleExtra(ARGS_STATIONS).getSerializable(ARGS_STATIONS);
        ArrayList<StationClientPerspective> stations = (ArrayList<StationClientPerspective>) getIntent().getSerializableExtra(ARGS_STATIONS);
        mainLayout = findViewById(R.id.clientStationList);
        mainLayout.setLayoutManager(new LinearLayoutManager(this));
        mainLayout.setAdapter(new ClientStationAdapter(stations));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return true;
    }

    private void refresh(){
        showSnackBarMessage(getString(R.string.refresh));
        StationsEngine.getInstance(this).loadStationList();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_refresh:
                refresh();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // mock alapú
//    @Override
//    public void onStartedStationClick(StationClientPerspective item) {
//        ArrayList<Objective> objectives = item.station.getObjectives();
//        if(objectives != null) {
//            Intent i = new Intent(ClientStationsActivity.this, ClientObjectiveActivity.class);
//            i.putExtra(ClientObjectiveActivity.ARGS_OBJECTIVES, objectives);
//            startActivity(i);
//        }
//    }

    @Override
    public void onStartedStationClick(StationClientPerspective item) {
        ObjectiveEngine.getInstance(this).loadObjectives(item.station.id);
    }

    private void goToObjectives(ArrayList<Objective> objectives){
        Intent i = new Intent(ClientStationsActivity.this, ClientObjectiveActivity.class);
        i.putExtra(ClientObjectiveActivity.ARGS_OBJECTIVES, objectives);
        i.putExtra(ClientObjectiveActivity.ARG_SEND, RaceRoleHandler.getClientMode() == RaceRoleHandler.ClientMode.Captain);
        startActivity(i);
    }

    // TODO: jelenleg csak placeholder megjelenítésre
    private void showSnackBarMessage(String message) {
        Snackbar.make(mainLayout, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void stationListLoaded(ArrayList<StationClientPerspective> stations) {
        mainLayout.setAdapter(new ClientStationAdapter(stations));
    }

    @Override
    public void stationLoadingError(ErrorType type) {
        showSnackBarMessage(type.getDefaultMessage());
    }

    @Override
    public void objectivesLoaded(ArrayList<Objective> objectives) {
        goToObjectives(objectives);
    }

    @Override
    public void objectiveLoadError(ErrorType type) {
        showSnackBarMessage(type.getDefaultMessage());
    }
}

