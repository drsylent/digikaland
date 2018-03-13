package hu.bme.aut.digikaland.ui.client.activities;

import android.content.Intent;
import android.net.Uri;
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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.entities.Station;
import hu.bme.aut.digikaland.entities.objectives.CustomAnswerObjective;
import hu.bme.aut.digikaland.entities.objectives.MultipleChoiceObjective;
import hu.bme.aut.digikaland.entities.objectives.Objective;
import hu.bme.aut.digikaland.entities.objectives.PhysicalObjective;
import hu.bme.aut.digikaland.entities.objectives.PictureObjective;
import hu.bme.aut.digikaland.entities.objectives.TrueFalseObjective;
import hu.bme.aut.digikaland.ui.client.fragments.ClientActualFragment;
import hu.bme.aut.digikaland.ui.client.fragments.ClientObjectiveFragment;
import hu.bme.aut.digikaland.ui.client.fragments.ClientStatusFragment;
import hu.bme.aut.digikaland.ui.common.activities.MapsActivity;
import hu.bme.aut.digikaland.ui.common.activities.SplashActivity;
import hu.bme.aut.digikaland.ui.common.fragments.ContactFragment;
import hu.bme.aut.digikaland.ui.common.fragments.ResultsFragment;
import hu.bme.aut.digikaland.utility.PhoneDial;

import static hu.bme.aut.digikaland.R.color.colorBlack;

public class ClientMainActivity extends AppCompatActivity implements ClientActualFragment.ClientActualMainListener, ClientObjectiveFragment.ClientActiveObjectiveListener,
        ResultsFragment.ResultsFragmentListener, ContactFragment.ClientHelpListener {

    NavigationView nav;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    LinearLayout mainLayout;

    @Override
    public void mapActivation() {
        setMap();
    }

    @Override
    public void helpActivation() {
        setHelp();
    }

    @Override
    public void onActiveObjectiveOpen() {
        goToObjective();
    }

    @Override
    public void onNewRaceStart() {
        startActivity(new Intent(ClientMainActivity.this, SplashActivity.class));
    }

    @Override
    public void phoneDial(String phoneNumber) {
        startActivity(PhoneDial.dial(phoneNumber));
    }

    private enum ViewState{
        Actual,
        Status
    }

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
    }

    ViewState state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
                        setMap();
                        break;
                    case R.id.clientStations:
                        setStations();
                        break;
                    case R.id.clientStatus:
                        setStatus();
                        break;
                }
                invalidateOptionsMenu();
                return false;
            }
        });
        setupToolbar();

        if(savedInstanceState == null) {
            state = ViewState.Actual;
            actualStatus = ActualStatus.normal;
            setActual();
        }
        else{
            state = ViewState.valueOf(savedInstanceState.getString(ARG_VIEWSTATE));
            actualStatus = ActualStatus.valueOf(savedInstanceState.getString(ARG_ACTUALSTATE));
            getActiveItem().setChecked(true);
        }
    }

    private static final String ARG_VIEWSTATE = "state";
    private static final String ARG_ACTUALSTATE = "actuals";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ARG_VIEWSTATE, state.name());
        outState.putString(ARG_ACTUALSTATE, actualStatus.name());
    }

    void setActualMain(){
        changeState(ViewState.Actual);
    }

    void setActual(){
        setActualMain();
        actualStatus = ActualStatus.normal;
        toolbar.setTitle(R.string.actual);
        Bundle bundle = new Bundle();
        bundle.putString(ClientActualFragment.ARG_LOCATION, "Ez itt egy cím lesz");
        bundle.putString(ClientActualFragment.ARG_SUBLOCATION, "Ez itt a cím pontosítása lesz");
        bundle.putInt(ClientActualFragment.ARG_STATIONS, 10);
        bundle.putInt(ClientActualFragment.ARG_STATION_NUMBER, 7);
        Calendar c = new GregorianCalendar();
        c.set(2018,2,26,19,48);
        Date testTime = c.getTime();
        bundle.putLong(ClientActualFragment.ARG_TIME, testTime.getTime());
        getSupportFragmentManager().beginTransaction().replace(R.id.clientContent, ClientActualFragment.newInstance(bundle)).commit();
    }

    void setMap(){
        Intent i = new Intent(ClientMainActivity.this, MapsActivity.class);
        Bundle locationData = new Bundle();
        double latitudes[] = {47.473372 };
        double longitudes[] = {19.059731};
        locationData.putDoubleArray(MapsActivity.ARGS_LATITUDE, latitudes);
        locationData.putDoubleArray(MapsActivity.ARGS_LONGITUDE, longitudes);
        i.putExtra(MapsActivity.MARKER_LOCATIONS, locationData);
        startActivity(i);
    }

    void setStations(){
        Intent i = new Intent(ClientMainActivity.this, ClientStationsActivity.class);
        Bundle stationData = new Bundle();
        stationData.putSerializable(ClientStationsActivity.ARGS_STATIONS , tempCreator());
        i.putExtra(ClientStationsActivity.ARGS_STATIONS, stationData);
        startActivity(i);
    }

    void goToObjective(){
        Intent i = new Intent(ClientMainActivity.this, ClientObjectiveActivity.class);
        i.putExtra(ClientObjectiveActivity.ARGS_OBJECTIVES, mockObjectiveGenerator());
        startActivity(i);
    }

    public ArrayList<Objective> mockObjectiveGenerator(){
        ArrayList<Objective> objectives = new ArrayList<>();
        objectives.add(new TrueFalseObjective("A BME-t 1782-ben alapították. Igaz vagy hamis?"));
        String answers[] = {"6", "7", "8", "9"};
        objectives.add(new MultipleChoiceObjective("Hány kar található a BME-n?", answers));
        objectives.add(new CustomAnswerObjective("Mikor alapították a VIK-et?"));
        objectives.add(new PictureObjective("Készítsetek egy szelfit és egy képet a környezetről!", 2));
        objectives.add(new PhysicalObjective("Fogj kezet a feladat felügyelőjével!"));
        return objectives;
    }

    public ArrayList<Objective> miniMockObjectiveGenerator(){
        ArrayList<Objective> objectives = new ArrayList<>();
        objectives.add(new PhysicalObjective("Ez csak egy picit kérdéssorozat."));
        return objectives;
    }

    private ArrayList<Station> tempCreator(){
        ArrayList<Station> list = new ArrayList<>();
        list.add(new Station(0, 0, Station.Status.Started, miniMockObjectiveGenerator()));
        list.add(new Station(2, 1, Station.Status.Done));
        list.add(new Station(4, 2, Station.Status.Done));
        list.add(new Station(1, 3, Station.Status.Started, mockObjectiveGenerator()));
        list.add(new Station(3, 4, Station.Status.NotStarted));
        return list;
    }

    void setStatus(){
        changeState(ViewState.Status);
        toolbar.setTitle(R.string.status);
        Bundle bundle = new Bundle();
        bundle.putString(ClientStatusFragment.ARG_RACENAME, "Ez a verseny neve");
        bundle.putString(ClientStatusFragment.ARG_TEAMNAME, "Ez a csapat neve");
        bundle.putString(ClientStatusFragment.ARG_CAPTAIN, "Ez a kapitány neve");
        bundle.putString(ClientStatusFragment.ARG_STATIONS, "Állomás: 5/7");
        bundle.putString(ClientStatusFragment.ARG_PHONE, "+36 30 371 7378");
        getSupportFragmentManager().beginTransaction().replace(R.id.clientContent, ClientStatusFragment.newInstance(bundle)).commit();
    }

    void setHelp(){
        Intent i = new Intent(ClientMainActivity.this, ClientHelpActivity.class);
        i.putStringArrayListExtra(ClientHelpActivity.ARGS_OBJADMINS, objectiveAdminNameGenerator());
        i.putStringArrayListExtra(ClientHelpActivity.ARGS_TOTADMINS, totalAdminNameGenerator());
        i.putStringArrayListExtra(ClientHelpActivity.ARGS_OBJADMINSPHONE, objectiveAdminPhoneGenerator());
        i.putStringArrayListExtra(ClientHelpActivity.ARGS_TOTADMINSPHONE, totalAdminPhoneGenerator());
        startActivity(i);
    }

    ArrayList<String> objectiveAdminNameGenerator(){
        ArrayList<String> list = new ArrayList<>();
        list.add("Alice Aladár");
        list.add("Bob Béla");
        return list;
    }

    ArrayList<String> totalAdminNameGenerator(){
        ArrayList<String> list = new ArrayList<>();
        list.add("Cinege Cecil");
        list.add("Dínom Dánom");
        list.add("Erik Elemér");
        return list;
    }

    ArrayList<String> objectiveAdminPhoneGenerator(){
        ArrayList<String> list = new ArrayList<>();
        list.add("+01 11 111 1111");
        list.add("+12 22 222 2222");
        return list;
    }

    ArrayList<String> totalAdminPhoneGenerator(){
        ArrayList<String> list = new ArrayList<>();
        list.add("+33 33 333 3333");
        list.add("+44 44 444 4444");
        list.add("+55 55 555 5555");
        return list;
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(findViewById(R.id.clientNavigation))) drawerLayout.closeDrawers();
        else if(state == ViewState.Status) setActual();
        else super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return true;
    }

    enum ActualStatus{
        normal,
        objective,
        results
    }

    ActualStatus actualStatus = ActualStatus.normal;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        showSnackBarMessage("REFRESH");
        if(state == ViewState.Actual)
        switch(item.getItemId()) {
            case R.id.menu_refresh:
                switch(actualStatus) {
                    case normal:
                        setObjective();
                        break;
                    case objective:
                        setResults();
                        break;
                    case results:
                        setActual();
                        break;
                }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setObjective(){
        setActualMain();
        actualStatus = ActualStatus.objective;
        // feladatot itt nem kell majd atadni, mert azt callbackkel intezzuk
        getSupportFragmentManager().beginTransaction().replace(R.id.clientContent, ClientObjectiveFragment.newInstance(2, 6, 3723)).commit();
    }

    private void setResults(){
        setActualMain();
        actualStatus = ActualStatus.results;
        String[] teams = {"Narancs csapat", "Zöld csapat", "Piros csapat", "Kék csapat", "Sárga csapat", "Hupikék csapat"};
        int[] points = {64, 23, 18, 12, 6, 2};
        getSupportFragmentManager().beginTransaction().replace(R.id.clientContent, ResultsFragment.newInstance(teams, points)).commit();
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
}
