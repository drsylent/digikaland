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
import hu.bme.aut.digikaland.ui.client.fragments.ClientStatusFragment;
import hu.bme.aut.digikaland.ui.common.activities.MapsActivity;

import static hu.bme.aut.digikaland.R.color.colorBlack;

public class ClientMainActivity extends AppCompatActivity implements ClientActualFragment.ClientActualMainListener {

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
        showSnackBarMessage("Help");
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
        if(item.getItemId() == R.id.clientMap)
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        showSnackBarMessage("REFRESH");
//        switch(item.getItemId()){
//            case R.id.add_subject:
//                new SubjectCreatorDialogFragment().show(getSupportFragmentManager(), getString(R.string.tag_subject_create));
//                break;
//            case R.id.add_objective:
//                if(subjectsAvailable()){
//                    new ObjectiveCreatorDialogFragment().show(getSupportFragmentManager(), getString(R.string.tag_objective_create));
//                }
//                break;
//            case R.id.add_lesson:
//                if(subjectsAvailable()){
//                    new LessonCreatorDialogFragment().show(getSupportFragmentManager(), getString(R.string.tag_lesson_create));
//                }
//                break;
//            case R.id.remove_objectives:
//                RemoveDoneDialogFragment.newInstance().show(getSupportFragmentManager(), getString(R.string.tag_objective_done_remove));
//                break;
//            case R.id.export_button:
//                getExportFragment().sendToCalendar(getContentResolver());
//                break;
//            case R.id.filter_objectives:
//                FilterDialogFragment.newInstance(getObjectiveFragment().getFilter()).show(getSupportFragmentManager(), getString(R.string.tag_objective_filter));
//        }
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
}
