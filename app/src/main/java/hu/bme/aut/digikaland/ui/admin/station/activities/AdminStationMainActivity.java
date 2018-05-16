package hu.bme.aut.digikaland.ui.admin.station.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import hu.bme.aut.digikaland.dblogic.AdminStationEngine;
import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.dblogic.AdminEngine;
import hu.bme.aut.digikaland.dblogic.CodeHandler;
import hu.bme.aut.digikaland.dblogic.ContactsEngineFull;
import hu.bme.aut.digikaland.dblogic.enumeration.ErrorType;
import hu.bme.aut.digikaland.dblogic.ObjectiveEngine;
import hu.bme.aut.digikaland.dblogic.ResultsEngine;
import hu.bme.aut.digikaland.dblogic.SolutionDownloadEngine;
import hu.bme.aut.digikaland.entities.Contact;
import hu.bme.aut.digikaland.entities.Location;
import hu.bme.aut.digikaland.entities.Team;
import hu.bme.aut.digikaland.entities.objectives.Objective;
import hu.bme.aut.digikaland.entities.objectives.solutions.Solution;
import hu.bme.aut.digikaland.entities.station.StationAdminPerspective;
import hu.bme.aut.digikaland.entities.station.StationAdminPerspectiveSummary;
import hu.bme.aut.digikaland.entities.station.StationMapData;
import hu.bme.aut.digikaland.ui.admin.common.activities.AdminEvaluateActivity;
import hu.bme.aut.digikaland.ui.admin.common.activities.AdminHelpActivity;
import hu.bme.aut.digikaland.ui.admin.common.activities.AdminStationsActivity;
import hu.bme.aut.digikaland.ui.admin.common.activities.AdminTeamsActivity;
import hu.bme.aut.digikaland.ui.admin.common.fragments.AdminRaceStarterFragment;
import hu.bme.aut.digikaland.ui.admin.station.fragments.AdminStationActualFragment;
import hu.bme.aut.digikaland.ui.admin.total.activities.AdminTotalMainActivity;
import hu.bme.aut.digikaland.ui.client.activities.ClientObjectiveActivity;
import hu.bme.aut.digikaland.ui.common.activities.MapsActivity;
import hu.bme.aut.digikaland.ui.common.activities.SplashActivity;
import hu.bme.aut.digikaland.ui.common.activities.StartupActivity;
import hu.bme.aut.digikaland.ui.common.fragments.NewRaceStarter;
import hu.bme.aut.digikaland.ui.common.fragments.ResultsFragment;
import hu.bme.aut.digikaland.utility.development.MockGenerator;

public class AdminStationMainActivity extends AppCompatActivity implements AdminStationActualFragment.AdminActivityInterface, ResultsFragment.ResultsFragmentListener,
        AdminRaceStarterFragment.AdminStarterListener, AdminEngine.CommunicationInterface, ResultsEngine.CommunicationInterface, ObjectiveEngine.CommunicationInterface,
        ContactsEngineFull.CommunicationInterface, SolutionDownloadEngine.CommunicationInterface, AdminStationEngine.CommunicationInterface {
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private LinearLayout mainLayout;
    private AdminStationActualFragment fragment = null;
    private AdminEngine db;

    private boolean uiReady = false;
    private boolean postLoad = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiReady = false;
        postLoad = false;
        db = AdminEngine.getInstance(this);
        db.loadState();
        setContentView(R.layout.activity_admin_main);
        mainLayout = findViewById(R.id.adminStationContent);
        drawerLayout = findViewById(R.id.adminDrawer);
        NavigationView nav = findViewById(R.id.adminNavigation);
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();
                switch(item.getItemId()){
                    case R.id.adminMap:
                        prepareMap();
                        break;
                    case R.id.adminStations:
                        prepareStations();
                        break;
                    case R.id.adminTeams:
                        prepareTeams();
                        break;
                    case R.id.adminNewRace:
                        prepareNewRace();
                        break;
                }
                invalidateOptionsMenu();
                return false;
            }
        });
        setupToolbar();
        nav.getMenu().getItem(0).setChecked(true);
        toolbar.setTitle(R.string.actual);
        uiReady = true;
        if(postLoad) executePostLoad();
        //if(savedInstanceState == null) setNotStarted();
    }

    private void prepareStations() {
        loadStationsForMap = false;
        AdminStationEngine.getInstance(this).loadStationDatas(db.getTeamSum());
    }

    private void executePostLoad(){
        switch (db.getLoadResult()){
            case Starting: startingStateLoaded(); break;
            case Running: runningStateLoaded(); break;
//            case Station: stationStateLoaded(); break;
            case Ending: endingStateLoaded(); break;
        }
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(findViewById(R.id.adminNavigation))) drawerLayout.closeDrawers();
        else super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return true;
    }

    @Override
    public void onNewRaceStart() {
        prepareNewRace();
    }

    @Override
    public void onStartPressed() {
        showSnackBarMessage(getString(R.string.stadmin_cant_start_race));
    }

    @Override
    public void onHelpPressed() {
        onHelpActivation();
    }

    @Override
    public void adminError(ErrorType type) {
        showSnackBarMessage(type.getDefaultMessage());
    }

    @Override
    public void startingStateLoaded() {
        setNotStarted();
    }

    @Override
    public void runningStateLoaded() {
        evaluated = db.getEvaluated();
        done = db.getDone();
        teamName = db.getArrivingTeamName();
        teamContact = db.getNextTeamContact();
        if(uiReady){
            goToActual(db.getLastLoadedLocation());
        }
        else postLoad = true;
    }

    @Override
    public void endingStateLoaded() {
        if(uiReady) {
            ResultsEngine.getInstance(this).loadResults();
        }
        else postLoad = true;
    }

    @Override
    public void resultsLoaded(ArrayList<String> teamNames, ArrayList<Integer> teamPoints) {
        state = ContentState.Results;
        getSupportFragmentManager().beginTransaction().replace(R.id.adminStationContent, ResultsFragment.newInstance(teamNames, teamPoints)).commit();
    }

    @Override
    public void resultsError(ErrorType type) {
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

    @Override
    public void allDataLoaded(ArrayList<Contact> totalAdmins, HashMap<String, ArrayList<Contact>> stationAdmins, HashMap<String, Contact> captains) {
        setHelp(totalAdmins, stationAdmins, captains);
    }

    @Override
    public void contactsError(ErrorType type) {
        showSnackBarMessage(type.getDefaultMessage());
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
        if(loadStationsForMap) setMap(list);
        else setStations(list);
    }

    @Override
    public void stationSummaryLoaded(String stationId, Location location, ArrayList<Contact> stationAdmins) {

    }

    @Override
    public void allTeamStatusLoaded(ArrayList<Team> teams) {
        setTeams(teams);
    }

    private void setStations(ArrayList<StationAdminPerspective> list){
        Bundle stationData = new Bundle();
        stationData.putSerializable(AdminStationsActivity.ARGS_STATIONS , list);
        goToStations(stationData);
    }

    private void goToStations(Bundle bundle){
        Intent i = new Intent(AdminStationMainActivity.this, AdminStationsActivity.class);
        i.putExtra(AdminStationsActivity.ARGS_STATIONS, bundle);
        i.putExtra(AdminStationsActivity.ARG_SUMMARY, true);
        startActivity(i);
    }

    private enum ContentState{
        NotStarted,
        Actual,
        Results
    }

    private ContentState state;

    private void setNotStarted(){
        if(uiReady)
        goToNotStarted(db.getLastLoadedLocation(), db.getLastLoadedStartingTime());
        else postLoad = true;
    }

    private void goToNotStarted(Location loc, Date time){
        state = ContentState.NotStarted;
        getSupportFragmentManager().beginTransaction().replace(R.id.adminStationContent,
                AdminRaceStarterFragment.newInstance(loc, time, false)).commit();
    }

    private void goToActual(Location location){
        state = ContentState.Actual;
        if(fragment == null) {
            fragment = AdminStationActualFragment.newInstance(location);
            getSupportFragmentManager().beginTransaction().replace(R.id.adminStationContent, fragment).commit();
        }
        else fragment.refreshAllData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        showSnackBarMessage(getResources().getString(R.string.refresh));
        db.loadState();
        return super.onOptionsItemSelected(item);
    }

    private boolean loadStationsForMap;

    private void prepareMap(){
        loadStationsForMap = true;
        AdminStationEngine.getInstance(this).loadStationDatas(db.getTeamSum());
    }

    private void setMap(ArrayList<StationAdminPerspective> stationPerspectives){
        ArrayList<StationMapData> stations = new ArrayList<>();
        int myIndex = -1;
        for(int i = 0; i < stationPerspectives.size(); i++){
            StationAdminPerspectiveSummary summary = (StationAdminPerspectiveSummary) stationPerspectives.get(i);
            if(summary.station.id.equals(db.getMyStationId()))
                myIndex = i;
            stations.add(new StationMapData(summary.station, summary.latitude, summary.longitude, summary.statistics));
        }
        Bundle locationData = new Bundle();
        locationData.putSerializable(MapsActivity.MARKER_LOCATIONS, stations);
        locationData.putBoolean(MapsActivity.MARKER_INTERACTIVITY, true);
        locationData.putInt(MapsActivity.MARKER_SPECIAL, myIndex);
        goToMap(locationData);
    }

    private void goToMap(Bundle locationData){
        Intent i = new Intent(AdminStationMainActivity.this, MapsActivity.class);
        i.putExtra(MapsActivity.MARKER_LOCATIONS, locationData);
        startActivity(i);
    }

    private void prepareTeams(){
        AdminStationEngine.getInstance(this).loadTeamList(db.getMyStationId());
    }

    private void setTeams(ArrayList<Team> teams){
        Bundle teamData = new Bundle();
        teamData.putSerializable(AdminTeamsActivity.ARG_TEAMS, teams);
        goToTeams(teamData);
    }

    private void goToTeams(Bundle teamData){
        Intent i = new Intent(AdminStationMainActivity.this, AdminTeamsActivity.class);
        i.putExtra(AdminTeamsActivity.ARG_TEAMS, teamData);
        i.putExtra(AdminTeamsActivity.ARG_STATIONID, db.getMyStationId());
        startActivity(i);
    }

    private void setupToolbar(){
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
        ActionBarDrawerToggle toggler = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        toggler.syncState();
        drawerLayout.addDrawerListener(toggler);
    }

    // TODO: jelenleg csak placeholder megjelenítésre
    private void showSnackBarMessage(String message) {
        Snackbar.make(mainLayout, message, Snackbar.LENGTH_LONG).show();
    }

    private int evaluated = -1;
    private int done = -1;

    @Override
    public int getEvaluated() {
        return evaluated;
    }

    @Override
    public int getDone() {
        return done;
    }

    @Override
    public int getSum() {
        return db.getTeamSum();
    }

    @Override
    public boolean areAllTeamsDone() {
        return done == getSum();
    }

    private String teamName = null;

    @Override
    public String getNextTeamName() {
        return teamName;
    }

    private Contact teamContact = null;

    @Override
    public Contact getNextTeamContact() {
        return teamContact;
    }

    @Override
    public void onEvaluateActivation() {
        if(isToEvaluate()) {
//            Intent i = new Intent(AdminStationMainActivity.this, AdminEvaluateActivity.class);
//            startActivity(MockGenerator.mockEvaluateIntent(i));
            SolutionDownloadEngine.getInstance(this).loadSolutions(db.getMyStationId(), db.getNextEvaluateTeamId());
        }
        else showSnackBarMessage("Nincs kiértékelésre váró csapat!");
    }

    @Override
    public void solutionsLoaded(ArrayList<Solution> solutions, int penalty, Date uploadTime, String teamName) {
        setEvaluator(solutions, penalty, uploadTime, teamName);
    }

    @Override
    public void solutionsLoadError(ErrorType type) {
        showSnackBarMessage(type.getDefaultMessage());
    }

    private void setEvaluator(ArrayList<Solution> solutions, int penalty, Date time, String teamName){
        Intent i = new Intent(AdminStationMainActivity.this, AdminEvaluateActivity.class);
        i.putExtra(AdminEvaluateActivity.ARG_SOLUTIONS, solutions);
        i.putExtra(AdminEvaluateActivity.ARG_STATION, Integer.valueOf(db.getMyStationId()));
        i.putExtra(AdminEvaluateActivity.ARG_TIME, time);
        i.putExtra(AdminEvaluateActivity.ARG_TEAM, teamName);
        i.putExtra(AdminEvaluateActivity.ARG_PENALTY, penalty);
        i.putExtra(AdminEvaluateActivity.ARG_SEND, true);
        i.putExtra(AdminEvaluateActivity.ARG_TEAMID, db.getNextEvaluateTeamId());
        goToEvaluation(i);
    }

    private void goToEvaluation(Intent i){
        startActivity(i);
    }

    @Override
    public void onObjectivesActivation() {
        ObjectiveEngine.getInstance(this).loadObjectives(db.getMyStationId());
    }

    private void goToObjectives(ArrayList<Objective> objectives){
        Intent i = new Intent(AdminStationMainActivity.this, ClientObjectiveActivity.class);
        i.putExtra(ClientObjectiveActivity.ARGS_OBJECTIVES, objectives);
        i.putExtra(ClientObjectiveActivity.ARG_SEND, false);
        startActivity(i);
    }

    @Override
    public void onHelpActivation() {
        ContactsEngineFull.getInstance(this).loadAllData();
    }

    private void setHelp(ArrayList<Contact> totalAdmins, HashMap<String, ArrayList<Contact>> stationAdmins, HashMap<String, Contact> captains){
        Bundle i = new Bundle();
        i.putSerializable(AdminHelpActivity.ARG_OBJECTADMINS, stationAdmins);
        i.putSerializable(AdminHelpActivity.ARG_TOTALADMINS, totalAdmins);
        i.putSerializable(AdminHelpActivity.ARG_CAPTAINS, captains);
        goToHelp(i);
    }

    private void goToHelp(Bundle helpBundle){
        Intent i = new Intent(AdminStationMainActivity.this, AdminHelpActivity.class);
        i.putExtra(AdminHelpActivity.ARG_HELPDATA, helpBundle);
        startActivity(i);
    }

    @Override
    public boolean isToEvaluate() {
        return done != evaluated;
    }

    private void prepareNewRace(){
        NewRaceStarter.getNewRaceDialog(this).show();
    }

}
