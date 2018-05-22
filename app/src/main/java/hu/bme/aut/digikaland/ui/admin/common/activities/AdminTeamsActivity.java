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

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.dblogic.StationAdminEngine;
import hu.bme.aut.digikaland.dblogic.enumeration.ErrorType;
import hu.bme.aut.digikaland.dblogic.RacePermissionHandler;
import hu.bme.aut.digikaland.dblogic.SolutionDownloadEngine;
import hu.bme.aut.digikaland.entities.Contact;
import hu.bme.aut.digikaland.entities.Location;
import hu.bme.aut.digikaland.entities.Team;
import hu.bme.aut.digikaland.entities.objectives.solutions.Solution;
import hu.bme.aut.digikaland.entities.station.StationAdminPerspective;
import hu.bme.aut.digikaland.ui.admin.common.fragments.AdminTeamsAdapter;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class AdminTeamsActivity extends AppCompatActivity implements AdminTeamsAdapter.AdminTeamsListener, SolutionDownloadEngine.CommunicationInterface,
        StationAdminEngine.StationAdminCommunicationInterface {
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

    private boolean ignoredPermission = false;

    @Override
    public void onTeamClicked(Team team) {
        if(summaryMode){
            prepareStations(team);
        }
        else{
            if(ignoredPermission) prepareEvaluation(team);
            else AdminTeamsActivityPermissionsDispatcher.prepareEvaluationWithPermissionCheck(this, team);
        }
    }

    @Override
    public void onTeamActivation(final Team team) {
        new AlertDialog.Builder(this).setMessage("Szeretnéd elindítani ezt az állomást ennek a csapatnak?")
                .setPositiveButton("Igen", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        StationAdminEngine.getInstance(AdminTeamsActivity.this).startStation(stationId, team.id);
                    }
                }).setNegativeButton("Mégse", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        }).show();
    }

    private void prepareStations(Team team){
        lastTeamId = team.id;
        StationAdminEngine.getInstance(this).loadStationDataForTeam(team.id);
    }

    @Override
    public void stationStarted() {
        showSnackBarMessage("Az állomás elindítása sikeres volt.");
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

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void prepareEvaluation(Team team){
        lastTeamId = team.id;
        SolutionDownloadEngine.getInstance(this).loadSolutions(stationId, lastTeamId);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        AdminTeamsActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @OnPermissionDenied(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void picturesCantLoad(){
        showSnackBarMessage("A képek így nem tudnak megjelenni.");
        ignoredPermission = true;
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