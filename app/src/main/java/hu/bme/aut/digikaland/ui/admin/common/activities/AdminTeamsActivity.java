package hu.bme.aut.digikaland.ui.admin.common.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import java.util.ArrayList;
import java.util.Date;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.dblogic.AdminStationEngine;
import hu.bme.aut.digikaland.dblogic.enumeration.ErrorType;
import hu.bme.aut.digikaland.dblogic.RacePermissionHandler;
import hu.bme.aut.digikaland.dblogic.SolutionDownloadEngine;
import hu.bme.aut.digikaland.entities.Contact;
import hu.bme.aut.digikaland.entities.Location;
import hu.bme.aut.digikaland.entities.Team;
import hu.bme.aut.digikaland.entities.objectives.solutions.Solution;
import hu.bme.aut.digikaland.entities.station.StationAdminPerspective;
import hu.bme.aut.digikaland.ui.admin.common.fragments.AdminTeamsAdapter;

public class AdminTeamsActivity extends AppCompatActivity implements AdminTeamsAdapter.AdminTeamsListener, SolutionDownloadEngine.CommunicationInterface,
            AdminStationEngine.CommunicationInterface {
    public final static String ARG_TEAMS = "teams";
    public final static String ARG_SUMMARY = "summary";
    public final static String ARG_STATIONID = "stationid";
    private boolean summaryMode;
    private String stationId;
    private RecyclerView mainLayout;

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
        summaryMode = getIntent().getBooleanExtra(ARG_SUMMARY, false);
        stationId = getIntent().getStringExtra(ARG_STATIONID);
        mainLayout = findViewById(R.id.clientStationList);
        RecyclerView list = findViewById(R.id.clientStationList);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(new AdminTeamsAdapter(teams, summaryMode));
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
        if(summaryMode){
            prepareStations(team);
        }
        else{
            prepareEvaluation(team);
        }
    }

    private void prepareStations(Team team){
        lastTeamId = team.id;
        AdminStationEngine.getInstance(this).loadStationDataForTeam(team.id);
    }

    @Override
    public void adminStationError(ErrorType type) {
        showSnackBarMessage(type.getDefaultMessage());
    }

    @Override
    public void stationTeamDataLoaded(ArrayList<StationAdminPerspective> stations) {
        setStations(stations);
    }

    private void setStations(ArrayList<StationAdminPerspective> stations){
        Bundle stationData = new Bundle();
        stationData.putSerializable(AdminStationsActivity.ARGS_STATIONS , stations);
        goToStations(stationData);
    }

    private void goToStations(Bundle stationData){
        Intent i = new Intent(AdminTeamsActivity.this, AdminStationsActivity.class);
        i.putExtra(AdminStationsActivity.ARGS_STATIONS, stationData);
        i.putExtra(AdminStationsActivity.ARG_SUMMARY, false);
        i.putExtra(AdminStationsActivity.ARG_TEAM, lastTeamId);
        startActivity(i);
    }

    private String lastTeamId;

    private void prepareEvaluation(Team team){
        lastTeamId = team.id;
        SolutionDownloadEngine.getInstance(this).loadSolutions(stationId, lastTeamId);
    }

    private void setEvaluation(ArrayList<Solution> solutions, int penalty, Date time, String teamName){
        Intent i = new Intent(AdminTeamsActivity.this, AdminEvaluateActivity.class);
        i.putExtra(AdminEvaluateActivity.ARG_SOLUTIONS, solutions);
        i.putExtra(AdminEvaluateActivity.ARG_STATION, Integer.valueOf(stationId));
        i.putExtra(AdminEvaluateActivity.ARG_TIME, time);
        i.putExtra(AdminEvaluateActivity.ARG_TEAM, teamName);
        i.putExtra(AdminEvaluateActivity.ARG_PENALTY, penalty);
        boolean evaluatable;
        if(RacePermissionHandler.getInstance().getAdminMode() == RacePermissionHandler.AdminMode.Total) evaluatable = true;
        else evaluatable = RacePermissionHandler.getInstance().getStationReference().getId().equals(stationId);
        i.putExtra(AdminEvaluateActivity.ARG_SEND, evaluatable);
        i.putExtra(AdminEvaluateActivity.ARG_TEAMID, lastTeamId);
        goToEvaluation(i);
    }

    private void goToEvaluation(Intent i){
        startActivity(i);
    }

    @Override
    public void solutionsLoaded(ArrayList<Solution> solutions, int penalty, Date uploadTime, String teamName) {
        setEvaluation(solutions, penalty, uploadTime, teamName);
    }

    @Override
    public void solutionsLoadError(ErrorType type) {
        showSnackBarMessage(type.getDefaultMessage());
    }

    // TODO: jelenleg csak placeholder megjelenítésre
    private void showSnackBarMessage(String message) {
        Snackbar.make(mainLayout, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void allStationLoadCompleted(ArrayList<StationAdminPerspective> list) {

    }

    @Override
    public void stationSummaryLoaded(String stationId, Location location, ArrayList<Contact> stationAdmins) {

    }

    @Override
    public void allTeamStatusLoaded(ArrayList<Team> teams) {

    }
}