package hu.bme.aut.digikaland.ui.admin.common.activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import java.util.ArrayList;
import java.util.Date;

import hu.bme.aut.digikaland.dblogic.StationAdminEngine;
import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.dblogic.enumeration.ErrorType;
import hu.bme.aut.digikaland.dblogic.RaceRoleHandler;
import hu.bme.aut.digikaland.dblogic.SolutionDownloadEngine;
import hu.bme.aut.digikaland.entities.Contact;
import hu.bme.aut.digikaland.entities.EvaluationStatistics;
import hu.bme.aut.digikaland.entities.Location;
import hu.bme.aut.digikaland.entities.Team;
import hu.bme.aut.digikaland.entities.objectives.solutions.Solution;
import hu.bme.aut.digikaland.entities.station.StationAdminPerspective;
import hu.bme.aut.digikaland.entities.station.StationAdminPerspectiveSummary;
import hu.bme.aut.digikaland.ui.admin.common.fragments.AdminStationAdapter;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class AdminStationsActivity extends AppCompatActivity implements AdminStationAdapter.AdminStationListener, StationAdminEngine.StationAdminCommunicationInterface,
        SolutionDownloadEngine.SolutionDownloadCommunicationInterface {
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

    private boolean ignoredPermission = false;

    @Override
    public void onNotStartedStationLongClick(final StationAdminPerspective station) {
        new AlertDialog.Builder(this).setMessage("Szeretnéd elindítani ezt az állomást ennek a csapatnak?")
                .setPositiveButton("Igen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StationAdminEngine.getInstance(AdminStationsActivity.this).startStation(station.station.id, teamId);
                    }
                }).setNegativeButton("Mégse", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).show();
    }

    @Override
    public void stationStarted() {
        showSnackBarMessage("Az állomás elindítása sikeres volt.");
    }

    @Override
    public void onStationClick(StationAdminPerspective item) {
        if(summaryMode) {
            prepareStationSummary(item);
        }
        else{
            if(ignoredPermission) prepareEvaluation(item);
            else AdminStationsActivityPermissionsDispatcher.prepareEvaluationWithPermissionCheck(this, item);
        }
    }

    private String lastStationId;

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void prepareEvaluation(StationAdminPerspective item){
        lastStationId = item.station.id;
        SolutionDownloadEngine.getInstance(this).loadSolutions(lastStationId, teamId);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        AdminStationsActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void picturesCantLoad(){
        showSnackBarMessage("A képek így nem tudnak megjelenni.");
        ignoredPermission = true;
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
        if(RaceRoleHandler.getAdminMode() == RaceRoleHandler.AdminMode.Total) evaluatable = true;
        else evaluatable = RaceRoleHandler.getStationReference().getId().equals(lastStationId);
        i.putExtra(AdminEvaluateActivity.ARG_SEND, evaluatable);
        i.putExtra(AdminEvaluateActivity.ARG_TEAMID, teamId);
        goToEvaluation(i);
    }

    private void goToEvaluation(Intent i){
        startActivity(i);
    }

    private EvaluationStatistics statistics;
    private double lastLat;
    private double lastLon;

    private void prepareStationSummary(StationAdminPerspective item){
        StationAdminPerspectiveSummary summary = (StationAdminPerspectiveSummary) item;
        statistics = summary.statistics;
        lastLat = summary.latitude;
        lastLon = summary.longitude;
        StationAdminEngine.getInstance(this).loadStationData(item.station.id);
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
        placeData.putExtra(AdminStationSummaryActivity.ARG_LATITUDE, lastLat);
        placeData.putExtra(AdminStationSummaryActivity.ARG_LONGITUDE, lastLon);
        goToStationSummary(placeData);
    }

    private void goToStationSummary(Intent i){
        startActivity(i);
    }
}
