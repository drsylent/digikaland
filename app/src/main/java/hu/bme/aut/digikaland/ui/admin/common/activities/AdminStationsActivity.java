package hu.bme.aut.digikaland.ui.admin.common.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.entities.StationAdminPerspective;
import hu.bme.aut.digikaland.entities.StationClientPerspective;
import hu.bme.aut.digikaland.entities.objectives.Objective;
import hu.bme.aut.digikaland.ui.admin.common.fragments.AdminStationAdapter;
import hu.bme.aut.digikaland.ui.client.activities.ClientStationsActivity;
import hu.bme.aut.digikaland.ui.client.fragments.ClientStationAdapter;
import hu.bme.aut.digikaland.utility.development.MockGenerator;

public class AdminStationsActivity extends AppCompatActivity implements AdminStationAdapter.AdminStationListener {
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
        ArrayList<StationAdminPerspective> stations = (ArrayList<StationAdminPerspective>) getIntent().getBundleExtra(ARGS_STATIONS).getSerializable(ARGS_STATIONS);
        RecyclerView list = findViewById(R.id.clientStationList);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(new AdminStationAdapter(stations));
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
    public void onStationClick(StationAdminPerspective item) {
        // TODO: adatfolyam itt nincs
        // kérdés lesz, hogy egyből mindent letöltsünk, vagy ha rákattint a felhasználó
        // csak akkor töltsük le az adatokat
        Intent i = new Intent(AdminStationsActivity.this, AdminStationSummaryActivity.class);
        startActivity(MockGenerator.adminStationSummaryGenerator(i));
    }
}
