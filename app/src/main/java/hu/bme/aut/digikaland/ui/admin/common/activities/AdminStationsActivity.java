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

import hu.bme.aut.digikaland.dblogic.AdminStationEngine;
import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.dblogic.ErrorType;
import hu.bme.aut.digikaland.dblogic.RacePermissionHandler;
import hu.bme.aut.digikaland.dblogic.SolutionDownloadEngine;
import hu.bme.aut.digikaland.entities.Contact;
import hu.bme.aut.digikaland.entities.EvaluationStatistics;
import hu.bme.aut.digikaland.entities.Location;
import hu.bme.aut.digikaland.entities.Team;
import hu.bme.aut.digikaland.entities.objectives.solutions.Solution;
import hu.bme.aut.digikaland.entities.station.StationAdminPerspective;
import hu.bme.aut.digikaland.entities.station.StationAdminPerspectiveSummary;
import hu.bme.aut.digikaland.ui.admin.common.fragments.AdminStationAdapter;
import hu.bme.aut.digikaland.utility.development.MockGenerator;

public class AdminStationsActivity extends AppCompatActivity implements AdminStationAdapter.AdminStationListener, AdminStationEngine.CommunicationInterface,
            SolutionDownloadEngine.CommunicationInterface{
    public final static String ARGS_STATIONS = "stations";
    public final static String ARG_SUMMARY = "summary";
    public final static String ARG_TEAM = "team";
    private boolean summaryMode;
    private RecyclerView mainLayout;
    private String teamId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_stations);
        ActionBar toolbar = getSupportActionBar();
        if(toolbar != null){
            toolbar.setDisplayHomeAsUpEnabled(true);
            toolbar.setTitle(R.string.stations);
        }
        mainLayout = findViewById(R.id.clientStationList);
        ArrayList<StationAdminPerspective> stations = (ArrayList<StationAdminPerspective>) getIntent().getBundleExtra(ARGS_STATIONS).getSerializable(ARGS_STATIONS);
        teamId = getIntent().getStringExtra(ARG_TEAM);
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
        if(summaryMode) {
            prepareStationSummary(item);
        }
        else{
            prepareEvaluation(item);
        }
    }

    private String lastStationId;

    private void prepareEvaluation(StationAdminPerspective item){
        lastStationId = item.station.id;
        SolutionDownloadEngine.getInstance(this).loadSolutions(lastStationId, teamId);
    }

    @Override
    public void solutionsLoaded(ArrayList<Solution> solutions, int penalty, Date uploadTime, String teamName) {
        setEvaluation(solutions, penalty, uploadTime, teamName);
    }

    @Override
    public void solutionsLoadError(ErrorType type) {
        showSnackBarMessage(type.getDefaultMessage());
    }

    private void setEvaluation(ArrayList<Solution> solutions, int penalty, Date time, String teamName){
        Intent i = new Intent(AdminStationsActivity.this, AdminEvaluateActivity.class);
        i.putExtra(AdminEvaluateActivity.ARG_SOLUTIONS, solutions);
        i.putExtra(AdminEvaluateActivity.ARG_STATION, Integer.valueOf(lastStationId));
        i.putExtra(AdminEvaluateActivity.ARG_TIME, time);
        i.putExtra(AdminEvaluateActivity.ARG_TEAM, teamName);
        i.putExtra(AdminEvaluateActivity.ARG_PENALTY, penalty);
        boolean evaluatable;
        if(RacePermissionHandler.getInstance().getAdminMode() == RacePermissionHandler.AdminMode.Total) evaluatable = true;
        else evaluatable = RacePermissionHandler.getInstance().getStationReference().getId().equals(lastStationId);
        i.putExtra(AdminEvaluateActivity.ARG_SEND, evaluatable);
        i.putExtra(AdminEvaluateActivity.ARG_TEAMID, teamId);
        goToEvaluation(i);
    }

    private void goToEvaluation(Intent i){
        startActivity(i);
    }

    private EvaluationStatistics statistics;

    private void prepareStationSummary(StationAdminPerspective item){
        StationAdminPerspectiveSummary summary = (StationAdminPerspectiveSummary) item;
        statistics = new EvaluationStatistics(summary.evaluated, summary.done, summary.sum);
        AdminStationEngine.getInstance(this).loadStationData(item.station.id);
    }

    // TODO: jelenleg csak placeholder megjelenítésre
    private void showSnackBarMessage(String message) {
        Snackbar.make(mainLayout, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void adminStationError(ErrorType type) {
        showSnackBarMessage(type.getDefaultMessage());
    }

    @Override
    public void stationTeamDataLoaded(ArrayList<StationAdminPerspective> stations) {

    }

    @Override
    public void allStationLoadCompleted(ArrayList<StationAdminPerspective> list) {

    }

    @Override
    public void stationSummaryLoaded(String stationId, Location location, ArrayList<Contact> stationAdmins) {
        setStationSummary(stationId, location, stationAdmins);
    }

    @Override
    public void allTeamStatusLoaded(ArrayList<Team> teams) {

    }

    private void setStationSummary(String stationId, Location location, ArrayList<Contact> stationAdmins){
        Intent placeData = new Intent(AdminStationsActivity.this, AdminStationSummaryActivity.class);
        placeData.putExtra(AdminStationSummaryActivity.ARG_STATUS, statistics);
        placeData.putExtra(AdminStationSummaryActivity.ARG_LOCATION, location);
        placeData.putExtra(AdminStationSummaryActivity.ARG_STATIONID, Integer.parseInt(stationId));
        placeData.putExtra(AdminStationSummaryActivity.ARG_CONTACT, stationAdmins);
        goToStationSummary(placeData);
    }

    private void goToStationSummary(Intent i){
        startActivity(i);
    }
}
