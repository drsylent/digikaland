package hu.bme.aut.digikaland.ui.client.activities;

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

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.dblogic.ClientEngine;
import hu.bme.aut.digikaland.dblogic.CodeHandler;
import hu.bme.aut.digikaland.dblogic.ContactsEngine;
import hu.bme.aut.digikaland.dblogic.enumeration.ErrorType;
import hu.bme.aut.digikaland.dblogic.ObjectiveEngine;
import hu.bme.aut.digikaland.dblogic.RacePermissionHandler;
import hu.bme.aut.digikaland.dblogic.ResultsEngine;
import hu.bme.aut.digikaland.dblogic.StationsEngine;
import hu.bme.aut.digikaland.entities.Contact;
import hu.bme.aut.digikaland.entities.EvaluationStatistics;
import hu.bme.aut.digikaland.entities.objectives.Objective;
import hu.bme.aut.digikaland.entities.station.Station;
import hu.bme.aut.digikaland.entities.station.StationAdminPerspective;
import hu.bme.aut.digikaland.entities.station.StationAdminPerspectiveSummary;
import hu.bme.aut.digikaland.entities.station.StationClientPerspective;
import hu.bme.aut.digikaland.entities.station.StationMapData;
import hu.bme.aut.digikaland.ui.admin.total.activities.AdminTotalMainActivity;
import hu.bme.aut.digikaland.ui.client.fragments.ClientActualFragment;
import hu.bme.aut.digikaland.ui.client.fragments.ClientObjectiveFragment;
import hu.bme.aut.digikaland.ui.client.fragments.ClientStatusFragment;
import hu.bme.aut.digikaland.ui.common.activities.MapsActivity;
import hu.bme.aut.digikaland.ui.common.activities.SplashActivity;
import hu.bme.aut.digikaland.ui.common.activities.StartupActivity;
import hu.bme.aut.digikaland.ui.common.fragments.NewRaceStarter;
import hu.bme.aut.digikaland.ui.common.fragments.ResultsFragment;

public class ClientMainActivity extends AppCompatActivity implements ClientActualFragment.ClientActualMainListener, ClientObjectiveFragment.ClientActiveObjectiveListener,
        ResultsFragment.ResultsFragmentListener, ClientEngine.CommunicationInterface, ResultsEngine.CommunicationInterface, ContactsEngine.CommunicationInterface,
        ObjectiveEngine.CommunicationInterface, StationsEngine.CommunicationInterface{

    private static final String ARG_VIEWSTATE = "state";

    private NavigationView nav;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private LinearLayout mainLayout;
    private ClientEngine db;

    @Override
    public void mapActivation() {
        goToMap(db.getLastLoadedGeoPoint());
    }

    @Override
    public void helpActivation() {
        prepareHelp();
    }

    private int oneHelpLoaded = 0;
    private final int oneHelpLoadedSum = 2;

    private void prepareHelp(){
        oneHelpLoaded = 0;
        ContactsEngine.getInstance(this).loadTotalAdmins();
        if(db.getLastLoadedStationNumber() == -1 || db.getLastLoadedStationNumber() > db.getStationSum()) oneHelpLoaded++;
        else ContactsEngine.getInstance(this).loadStationAdmins(db.getLastLoadedStationId());
    }

    @Override
    public void totalAdminsLoaded() {
        setHelp();
    }

    @Override
    public void stationAdminsLoaded() {
        setHelp();
    }

    private void setHelp(){
        oneHelpLoaded++;
        if(oneHelpLoaded == oneHelpLoadedSum) goToHelp(ContactsEngine.getInstance(this).getTotalAdmins(), ContactsEngine.getInstance(this).getStationAdmins(db.getLastLoadedStationId()));
    }

    private void goToHelp(ArrayList<Contact> totalAdmins, ArrayList<Contact> stationAdmins){
        Intent i = new Intent(ClientMainActivity.this, ClientHelpActivity.class);
        i.putExtra(ClientHelpActivity.ARG_STATIONADMINS, stationAdmins);
        i.putExtra(ClientHelpActivity.ARG_TOTALADMINS, totalAdmins);
        startActivity(i);
    }

    private int oneStatusLoaded = 0;
    private int oneStatusLoadedSum = 3;

    private void prepareStatus(){
        oneStatusLoaded = 0;
        db.loadTeamName();
        db.loadCompletedStations();
        ContactsEngine.getInstance(this).loadCaptain(db.getTeamId());
    }

    @Override
    public void captainLoaded() {
        setStatus();
    }

    @Override
    public void teamNameLoaded() {
        setStatus();
    }

    @Override
    public void completedStationsLoaded() {
        setStatus();
    }

    private void setStatus(){
        oneStatusLoaded++;
        if(oneStatusLoaded == oneStatusLoadedSum){
            Bundle bundle = new Bundle();
            bundle.putString(ClientStatusFragment.ARG_RACENAME, db.getRaceName());
            bundle.putString(ClientStatusFragment.ARG_TEAMNAME, db.getTeamName());
            bundle.putSerializable(ClientStatusFragment.ARG_CAPTAIN, ContactsEngine.getInstance(this).getCaptain(db.getTeamId()));
            bundle.putInt(ClientStatusFragment.ARG_STATIONSUM, db.getStationSum());
            bundle.putInt(ClientStatusFragment.ARG_STATION_NUMBER, db.getCompletedStations());
            goToStatus(bundle);
        }
    }

    private void goToStatus(Bundle bundle){
        changeState(ViewState.Status);
        toolbar.setTitle(R.string.status);
        getSupportFragmentManager().beginTransaction().replace(R.id.clientContent, ClientStatusFragment.newInstance(bundle)).commit();
    }

    private void setActual(){
        changeState(ViewState.Actual);
        executePostLoad();
    }

    boolean objectiveCausedLoad = false;

    // ha egy ideig nem frissít, elvileg beléphet úgy, hogy már kész a feladat!
    // ezt elkerülendő mindig lesz egy frissítés ekkor
    @Override
    public void onActiveObjectiveOpen() {
        prepareObjectives();
    }

    private void prepareObjectives(){
        objectiveCausedLoad = true;
        refresh();
    }

    private void prepareObjectivesAfterRefresh(){
        ObjectiveEngine.getInstance(this).loadObjectives(db.getLastLoadedStationId());
    }

    @Override
    public void objectivesLoaded(ArrayList<Objective> objectives) {
        goToObjectives(objectives);
    }

    @Override
    public void objectiveLoadError(ErrorType type) {
        showSnackBarMessage("ObjectiveEngine: " + type.getDefaultMessage());
    }

    private void goToObjectives(ArrayList<Objective> objectives){
        Intent i = new Intent(ClientMainActivity.this, ClientObjectiveActivity.class);
        i.putExtra(ClientObjectiveActivity.ARGS_OBJECTIVES, objectives);
        i.putExtra(ClientObjectiveActivity.ARG_SEND, RacePermissionHandler.getInstance().getClientMode() == RacePermissionHandler.ClientMode.Captain);
        startActivity(i);
    }

    @Override
    public void onNewRaceStart() {
        prepareNewRace();
    }

    @Override
    public void clientError(ErrorType type) {
        showSnackBarMessage("ClientEngine: " + type.getDefaultMessage());
    }

    private boolean uiReady = false;

    @Override
    public void resultsLoaded(ArrayList<String> teamNames, ArrayList<Integer> teamPoints) {
        setResults(teamNames, teamPoints);
    }

    @Override
    public void resultsError(ErrorType type) {
        showSnackBarMessage("ResultsEngine: " + type.getDefaultMessage());
    }

    @Override
    public void contactsError(ErrorType type) {
        showSnackBarMessage("ContactsEngine: " + type.getDefaultMessage());
    }

    private boolean postLoad = false;

    @Override
    public void startingStateLoaded() {
        if(uiReady) {
            Bundle startingBundle = new Bundle();
            startingBundle.putInt(ClientActualFragment.ARG_STATION_NUMBER, -1);
            startingBundle.putSerializable(ClientActualFragment.ARG_LOCATION, db.getLastLoadedLocation());
            startingBundle.putSerializable(ClientActualFragment.ARG_TIME, db.getLastLoadedStartingTime());
            goToActual(startingBundle);
        }
        else postLoad = true;
    }

    @Override
    public void runningStateLoaded() {
        if(uiReady) {
            Bundle runningBundle = new Bundle();
            runningBundle.putInt(ClientActualFragment.ARG_STATION_NUMBER, db.getLastLoadedStationNumber());
            runningBundle.putInt(ClientActualFragment.ARG_STATIONS, db.getStationSum());
            runningBundle.putSerializable(ClientActualFragment.ARG_LOCATION, db.getLastLoadedLocation());
            runningBundle.putSerializable(ClientActualFragment.ARG_TIME, db.getLastLoadedStartingTime());
            goToActual(runningBundle);
        }
        else postLoad = true;
    }

    @Override
    public void stationStateLoaded() {
        if(objectiveCausedLoad){
            prepareObjectivesAfterRefresh();
        }
        else if(uiReady) {
            goToObjective(db.getLastLoadedStationNumber(), db.getStationSum(), db.getLastLoadedEndingTime());
        }
        else postLoad = true;
        objectiveCausedLoad = false;
    }

    @Override
    public void endingStateLoaded() {
        if(uiReady) {
            ResultsEngine.getInstance(this).loadResults();
        }
        else postLoad = true;
    }

    private enum ViewState{
        Actual,
        Status
    }

    private ViewState state;

    private MenuItem getActiveItem(){
        switch (state){
            case Actual:
                return nav.getMenu().getItem(0);
            case Status:
                return nav.getMenu().getItem(3);
        }
        return nav.getMenu().getItem(0);
    }

    private void changeState(ViewState to){
        getActiveItem().setChecked(false);
        state = to;
        getActiveItem().setChecked(true);
        switch (to){
            case Actual: toolbar.setTitle(R.string.actual); break;
            case Status: toolbar.setTitle(R.string.status); break;
        }
    }

    private void prepareStations(){
        StationsEngine.getInstance(this).loadStationList();
    }

    @Override
    public void stationListLoaded(ArrayList<StationClientPerspective> stations) {
        goToStations(stations);
    }

    @Override
    public void stationLoadingError(ErrorType type) {
        showSnackBarMessage("StationsEngine: " + type.getDefaultMessage());
    }


    private void goToStations(ArrayList<StationClientPerspective> stations){
        Intent i = new Intent(ClientMainActivity.this, ClientStationsActivity.class);
        i.putExtra(ClientStationsActivity.ARGS_STATIONS, stations);
        startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // elindítjuk itt, hogy amíg a felület beállítódik, stb, addig is menjen a letöltés
        // persze ha előbb végez, akkor gáz van, ezért figyelünk
        db = ClientEngine.getInstance(this);
        db.loadState();
        setContentView(R.layout.activity_client_main);
        mainLayout = findViewById(R.id.clientContent);
        drawerLayout = findViewById(R.id.clientDrawer);
        nav = findViewById(R.id.clientNavigation);
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();
                if(getActiveItem() == item) return false;
                switch(item.getItemId()){
                    case R.id.clientActual:
                        setActual();
                        break;
                    case R.id.clientMap:
                        goToMap(db.getLastLoadedGeoPoint());
                        break;
                    case R.id.clientStations:
                        prepareStations();
                        break;
                    case R.id.clientStatus:
                        prepareStatus();
                        break;
                    case R.id.clientNewRace:
                        prepareNewRace();
                        break;
                }
                invalidateOptionsMenu();
                return false;
            }
        });
        setupToolbar();
        if(savedInstanceState == null) {
            state = ViewState.Actual;
            changeState(ViewState.Actual);
//            actualStatus = ActualStatus.normal;
//            setActualMock();
        }
        uiReady = true;
        if(postLoad) executePostLoad();

//        else{
//            state = ViewState.valueOf(savedInstanceState.getString(ARG_VIEWSTATE));
//            actualStatus = ActualStatus.valueOf(savedInstanceState.getString(ARG_ACTUALSTATE));
//            getActiveItem().setChecked(true);
//        }
    }

    private void executePostLoad(){
        switch (db.getLoadResult()){
            case Starting: startingStateLoaded(); break;
            case Running: runningStateLoaded(); break;
            case Station: stationStateLoaded(); break;
            case Ending: endingStateLoaded(); break;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_VIEWSTATE, state.name());
    }

    void setActualMain(){
        changeState(ViewState.Actual);
    }

    private void goToActual(Bundle bundle){
        toolbar.setTitle(R.string.actual);
        state = ViewState.Actual;
        getSupportFragmentManager().beginTransaction().replace(R.id.clientContent, ClientActualFragment.newInstance(bundle)).commit();
    }

    private void setResults(ArrayList<String> teams, ArrayList<Integer> points){
        toolbar.setTitle(R.string.actual);
        getSupportFragmentManager().beginTransaction().replace(R.id.clientContent, ResultsFragment.newInstance(teams, points)).commit();
    }

    private void goToObjective(int stationNumber, int stationSum, Date endingTime){
        Date now = Calendar.getInstance().getTime();
        long timeLeft = 0;
        if(now.before(endingTime)) timeLeft = endingTime.getTime()-now.getTime();
        getSupportFragmentManager().beginTransaction().replace(R.id.clientContent, ClientObjectiveFragment.newInstance(stationNumber, stationSum, timeLeft/1000)).commit();
    }

    void goToMap(GeoPoint geo){
        Intent i = new Intent(ClientMainActivity.this, MapsActivity.class);
        ArrayList<StationMapData> stations = new ArrayList<>();
        StationMapData data = new StationMapData(new Station(0,0), geo.getLatitude(), geo.getLongitude(), new EvaluationStatistics(0,0,0));
        data.setSpecialName("Következő állomás");
        stations.add(data);
        Bundle locationData = new Bundle();
        locationData.putSerializable(MapsActivity.MARKER_LOCATIONS, stations);
        locationData.putBoolean(MapsActivity.MARKER_INTERACTIVITY, false);
        locationData.putInt(MapsActivity.MARKER_SPECIAL, 0);
        i.putExtra(MapsActivity.MARKER_LOCATIONS, locationData);
        startActivity(i);
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(findViewById(R.id.clientNavigation))) drawerLayout.closeDrawers();
        else if(state == ViewState.Status){
            //setActualMock();
            setActual();
        }
        else super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return true;
    }

    private void refresh(){
        showSnackBarMessage(getResources().getString(R.string.refresh));
        db.loadState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        objectiveCausedLoad = false;
        refresh();
        return super.onOptionsItemSelected(item);
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

    private void prepareNewRace(){
        NewRaceStarter.getNewRaceDialog(this).show();
    }
}
