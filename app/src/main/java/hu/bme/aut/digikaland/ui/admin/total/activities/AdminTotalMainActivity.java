package hu.bme.aut.digikaland.ui.admin.total.activities;

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

import hu.bme.aut.digikaland.AdminStationEngine;
import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.dblogic.AdminTotalEngine;
import hu.bme.aut.digikaland.dblogic.ContactsEngineFull;
import hu.bme.aut.digikaland.dblogic.ErrorType;
import hu.bme.aut.digikaland.dblogic.ResultsEngine;
import hu.bme.aut.digikaland.dblogic.enumeration.RaceState;
import hu.bme.aut.digikaland.entities.Contact;
import hu.bme.aut.digikaland.entities.EvaluationStatistics;
import hu.bme.aut.digikaland.entities.Location;
import hu.bme.aut.digikaland.entities.Team;
import hu.bme.aut.digikaland.entities.station.StationAdminPerspective;
import hu.bme.aut.digikaland.ui.admin.common.activities.AdminHelpActivity;
import hu.bme.aut.digikaland.ui.admin.common.activities.AdminStationsActivity;
import hu.bme.aut.digikaland.ui.admin.common.activities.AdminTeamsActivity;
import hu.bme.aut.digikaland.ui.admin.common.fragments.AdminRaceStarterFragment;
import hu.bme.aut.digikaland.ui.admin.total.fragments.AdminRunningFragment;
import hu.bme.aut.digikaland.ui.common.activities.MapsActivity;
import hu.bme.aut.digikaland.ui.common.activities.SplashActivity;
import hu.bme.aut.digikaland.ui.common.fragments.ResultsFragment;
import hu.bme.aut.digikaland.utility.development.MockGenerator;

public class AdminTotalMainActivity extends AppCompatActivity implements ResultsFragment.ResultsFragmentListener,
        AdminRunningFragment.AdminRunningListener, AdminRaceStarterFragment.AdminStarterListener, AdminTotalEngine.CommunicationInterface, ResultsEngine.CommunicationInterface,
        AdminStationEngine.CommunicationInterface, ContactsEngineFull.CommunicationInterface{

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private LinearLayout mainLayout;

    private boolean uiReady = false;
    private boolean postLoad = false;

    private AdminTotalEngine db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        uiReady = false;
        postLoad = false;
        db = AdminTotalEngine.getInstance(this);
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
                        startMap();
                        break;
                    case R.id.adminStations:
                        prepareStations();
                        break;
                    case R.id.adminTeams:
                        startTeams();
                        break;
                }
                invalidateOptionsMenu();
                return false;
            }
        });
        setupToolbar();
        nav.getMenu().getItem(0).setChecked(true);
        toolbar.setTitle(R.string.actual);
       // if(savedInstanceState == null) setNotStarted();
        uiReady = true;
        if(postLoad) executePostLoad();
    }

    private void executePostLoad(){
        switch (db.getLoadResult()){
            case Starting: startingStateLoaded(); break;
            case Running: runningStateLoaded(); break;
            case Ending: endingStateLoaded(); break;
        }
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(findViewById(R.id.adminNavigation))) drawerLayout.closeDrawers();
        else super.onBackPressed();
    }

    @Override
    public void onNewRaceStart() {
        startActivity(new Intent(AdminTotalMainActivity.this, SplashActivity.class));
    }

    @Override
    public void onStartPressed() {
        prepareStatusUpdate(RaceState.Started);
    }

    private void prepareStatusUpdate(RaceState state){
        db.updateRaceStatus(state);
    }

    @Override
    public void statusUpdateSuccessful() {
        showSnackBarMessage("Feltöltés sikeres");
        db.loadState();
    }

    @Override
    public void onEndPressed() {
        prepareStatusUpdate(RaceState.Ended);
    }

    @Override
    public void onHelpPressed() {
        ContactsEngineFull.getInstance(this).loadAllData();
    }

    @Override
    public void allDataLoaded(ArrayList<Contact> totalAdmins, HashMap<String, ArrayList<Contact>> stationAdmins, HashMap<String, Contact> captains) {
        setHelp(totalAdmins, stationAdmins, captains);
    }

    private void setHelp(ArrayList<Contact> totalAdmins, HashMap<String, ArrayList<Contact>> stationAdmins, HashMap<String, Contact> captains){
        Bundle i = new Bundle();
        i.putSerializable(AdminHelpActivity.ARG_OBJECTADMINS, stationAdmins);
        i.putSerializable(AdminHelpActivity.ARG_TOTALADMINS, totalAdmins);
        i.putSerializable(AdminHelpActivity.ARG_CAPTAINS, captains);
        goToHelp(i);
    }

    private void goToHelp(Bundle helpBundle){
        Intent i = new Intent(AdminTotalMainActivity.this, AdminHelpActivity.class);
        i.putExtra(AdminHelpActivity.ARG_HELPDATA, helpBundle);
        startActivity(i);
    }

    @Override
    public void contactsError(ErrorType type) {
        showSnackBarMessage(type.getDefaultMessage());
    }

    @Override
    public void totalAdminError(ErrorType type) {
        showSnackBarMessage(type.getDefaultMessage());
    }

    @Override
    public void startingStateLoaded() {
        setNotStarted();
    }

    private void setNotStarted(){
        if(uiReady)
            goToNotStarted(db.getLastLoadedLocation(), db.getLastLoadedTime());
        else postLoad = true;
    }

    private void goToNotStarted(Location loc, Date time){
        getSupportFragmentManager().beginTransaction().replace(R.id.adminStationContent,
                AdminRaceStarterFragment.newInstance(loc, time, true)).commit();
    }

    @Override
    public void runningStateLoaded() {
        setRunning();
    }

    private void setRunning(){
        if(uiReady)
            goToRunning(db.getLastLoadedLocation(), db.getLastLoadedTime(), db.getStatistics());
        else postLoad = true;
    }

    private void goToRunning(Location loc, Date time, EvaluationStatistics statistics){
        getSupportFragmentManager().beginTransaction().replace(R.id.adminStationContent,
                AdminRunningFragment.newInstance(loc, time, statistics)).commit();
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
        getSupportFragmentManager().beginTransaction().replace(R.id.adminStationContent, ResultsFragment.newInstance(teamNames, teamPoints)).commit();
    }

    @Override
    public void resultsError(ErrorType type) {
        showSnackBarMessage(type.getDefaultMessage());
    }

    private void startMap(){
        Intent i = new Intent(AdminTotalMainActivity.this, MapsActivity.class);
        i.putExtra(MapsActivity.MARKER_LOCATIONS, MockGenerator.mockMapBigData());
        startActivity(i);
    }

    private void prepareStations(){
        AdminStationEngine.getInstance(this).loadStationDatas(db.getTeamSum());
    }

    @Override
    public void allStationLoadCompleted(ArrayList<StationAdminPerspective> list) {
        setStations(list);
    }

    private void setStations(ArrayList<StationAdminPerspective> list){
        Bundle stationData = new Bundle();
        stationData.putSerializable(AdminStationsActivity.ARGS_STATIONS , list);
        goToStations(stationData);
    }

    private void goToStations(Bundle stationsData){
        Intent i = new Intent(AdminTotalMainActivity.this, AdminStationsActivity.class);
        i.putExtra(AdminStationsActivity.ARGS_STATIONS, stationsData);
        i.putExtra(AdminStationsActivity.ARG_SUMMARY, true);
        startActivity(i);
    }

    private void startTeams(){
        // TODO: ez nem ugyanaz a csapat activity lesz! (vagy más módban fut?)
        Intent i = new Intent(AdminTotalMainActivity.this, AdminTeamsActivity.class);
        i.putExtra(AdminTeamsActivity.ARG_TEAMS, MockGenerator.mockAdminTeamsList());
        i.putExtra(AdminTeamsActivity.ARG_SUMMARY, true);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        showSnackBarMessage(getResources().getString(R.string.refresh));
//        MockGenerator.adminStationCycleStep();
//        if(MockGenerator.adminStationIsResultsActive()) setResults();
//        else{
//            if(state == ContentState.Actual) fragment.refreshAllData();
//            else goToActual();
//        }
        db.loadState();
        return super.onOptionsItemSelected(item);
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
    public void stationSummaryLoaded(String stationId, Location location, ArrayList<Contact> stationAdmins) {

    }

    @Override
    public void allTeamStatusLoaded(ArrayList<Team> teams) {

    }
}
