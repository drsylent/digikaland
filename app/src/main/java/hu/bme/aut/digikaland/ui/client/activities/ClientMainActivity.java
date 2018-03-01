package hu.bme.aut.digikaland.ui.client.activities;

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
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.entities.Station;
import hu.bme.aut.digikaland.ui.client.fragments.ClientActualFragment;
import hu.bme.aut.digikaland.ui.client.fragments.ClientObjectiveFragment;
import hu.bme.aut.digikaland.ui.client.fragments.ClientStatusFragment;
import hu.bme.aut.digikaland.ui.common.activities.MapsActivity;
import hu.bme.aut.digikaland.ui.common.fragments.ContactFragment;

import static hu.bme.aut.digikaland.R.color.colorBlack;

public class ClientMainActivity extends AppCompatActivity implements ClientActualFragment.ClientActualMainListener, ClientObjectiveFragment.ClientActiveObjectiveListener {

    NavigationView nav;
    Toolbar toolbar;
    DrawerLayout drawerLayout;
    MenuItem activeItem;
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
        showSnackBarMessage("Objective show");
    }

    private enum ViewState{
        Actual,
        Map,
        Stations,
        Status
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
                if(activeItem == item) return false;
                setActiveItem(item);
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
        activeItem = nav.getMenu().getItem(0);
        setActual();
//        int active;
//        if(subjectsAvailable()){
//            setTimeTableMode();
//            active = 0;
//        }
//        else {
//            setSubjectMode();
//            active = 2;
//        }
    }

    void setActiveItem(MenuItem item){
        if(item.getItemId() == R.id.clientMap || item.getItemId() == R.id.clientStations)
            return;
        activeItem.setChecked(false);
        activeItem = item;
        activeItem.setChecked(true);
    }

    void setActual(){
        setActiveItem(nav.getMenu().getItem(0));
        state = ViewState.Actual;
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
        //state = ViewState.Stations;
        Intent i = new Intent(ClientMainActivity.this, ClientStationsActivity.class);
        Bundle stationData = new Bundle();
        stationData.putSerializable(ClientStationsActivity.ARGS_STATIONS , tempCreator());
        i.putExtra(ClientStationsActivity.ARGS_STATIONS, stationData);
        startActivity(i);
    }

    private ArrayList<Station> tempCreator(){
        ArrayList<Station> list = new ArrayList<>();
        list.add(new Station(0, 0, Station.Status.Started));
        list.add(new Station(2, 1, Station.Status.Done));
        list.add(new Station(4, 2, Station.Status.Done));
        list.add(new Station(1, 3, Station.Status.Started));
        list.add(new Station(3, 4, Station.Status.NotStarted));
        return list;
    }

    void setStatus(){
        state = ViewState.Status;
        toolbar.setTitle(R.string.status);
        Bundle bundle = new Bundle();
        bundle.putString(ClientStatusFragment.ARG_RACENAME, "Ez a verseny neve");
        bundle.putString(ClientStatusFragment.ARG_TEAMNAME, "Ez a csapat neve");
        bundle.putString(ClientStatusFragment.ARG_CAPTAIN, "Ez a kapitány neve");
        bundle.putString(ClientStatusFragment.ARG_STATIONS, "Állomás: 5/7");
        bundle.putString(ClientStatusFragment.ARG_PHONE, "+50 50 505 5555");
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

    boolean objectiveOn = false;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        showSnackBarMessage("REFRESH");
        switch(item.getItemId()) {
            case R.id.menu_refresh:
                // TODO: ne itt tortenjen meg, Actualra valtaskor latszodjon!
                if (state == ViewState.Actual) {
                    objectiveOn = !objectiveOn;
                    if (objectiveOn) setObjective();
                    else setActual();
                }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setObjective(){
        getSupportFragmentManager().beginTransaction().replace(R.id.clientContent, ClientObjectiveFragment.newInstance(2, 6, 3723)).commit();
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
