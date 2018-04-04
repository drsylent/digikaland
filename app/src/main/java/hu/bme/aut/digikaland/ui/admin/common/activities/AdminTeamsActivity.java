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
import hu.bme.aut.digikaland.entities.Team;
import hu.bme.aut.digikaland.entities.objectives.Objective;
import hu.bme.aut.digikaland.ui.admin.common.fragments.AdminTeamsAdapter;
import hu.bme.aut.digikaland.ui.admin.station.activities.AdminStationMainActivity;
import hu.bme.aut.digikaland.utility.development.MockGenerator;

/**
 * Created by Sylent on 2018. 04. 04..
 */

public class AdminTeamsActivity extends AppCompatActivity implements AdminTeamsAdapter.AdminTeamsListener {
    public final static String ARG_TEAMS = "teams";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_stations);
        ActionBar toolbar = getSupportActionBar();
        if(toolbar != null){
            toolbar.setDisplayHomeAsUpEnabled(true);
            toolbar.setTitle(R.string.teams);
        }
        ArrayList<Team> teams = (ArrayList<Team>) getIntent().getBundleExtra(ARG_TEAMS).getSerializable(ARG_TEAMS);
        RecyclerView list = findViewById(R.id.clientStationList);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(new AdminTeamsAdapter(teams));
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
    public void onTeamClicked(Team team) {
        // TODO: nincs még adatfolyam
        Intent i = new Intent(AdminTeamsActivity.this, AdminEvaluateActivity.class);
        i.putExtra(AdminEvaluateActivity.ARG_SOLUTIONS, MockGenerator.mockSolutionList());
        i.putExtra(AdminEvaluateActivity.ARG_STATION, 2);
        i.putExtra(AdminEvaluateActivity.ARG_TIME, new Date(118, 3, 3).getTime());
        i.putExtra(AdminEvaluateActivity.ARG_TEAM, "Narancs csapat");
        i.putExtra(AdminEvaluateActivity.ARG_PENALTY, 23);
        i.putExtra(AdminEvaluateActivity.ARG_SEND, true);
        startActivity(i);
    }
}