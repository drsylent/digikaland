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
import java.util.Date;

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
    public final static String ARG_SUMMARY = "summary";
    private boolean summaryMode;

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
        summaryMode = getIntent().getBooleanExtra(ARG_SUMMARY, false);
        RecyclerView list = findViewById(R.id.clientStationList);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(new AdminStationAdapter(stations, summaryMode));
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
        if(summaryMode) {
            Intent i = new Intent(AdminStationsActivity.this, AdminStationSummaryActivity.class);
            startActivity(MockGenerator.adminStationSummaryGenerator(i));
        }
        else{
            Intent i = new Intent(AdminStationsActivity.this, AdminEvaluateActivity.class);
            i.putExtra(AdminEvaluateActivity.ARG_SOLUTIONS, MockGenerator.mockSolutionList());
            i.putExtra(AdminEvaluateActivity.ARG_STATION, 2);
            i.putExtra(AdminEvaluateActivity.ARG_TIME, new Date(118, 3, 3).getTime());
            i.putExtra(AdminEvaluateActivity.ARG_TEAM, "Narancs csapat");
            i.putExtra(AdminEvaluateActivity.ARG_PENALTY, 23);
            i.putExtra(AdminEvaluateActivity.ARG_SEND, true);
            startActivity(i);
        }
    }
}
