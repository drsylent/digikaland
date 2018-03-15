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
import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.ui.client.fragments.ClientActualFragment;
import hu.bme.aut.digikaland.ui.client.fragments.ClientObjectiveFragment;
import hu.bme.aut.digikaland.ui.client.fragments.ClientStatusFragment;
import hu.bme.aut.digikaland.ui.common.activities.MapsActivity;
import hu.bme.aut.digikaland.ui.common.activities.SplashActivity;
import hu.bme.aut.digikaland.ui.common.fragments.ResultsFragment;
import hu.bme.aut.digikaland.utility.development.MockGenerator;

public class ClientMainActivity extends AppCompatActivity implements ClientActualFragment.ClientActualMainListener, ClientObjectiveFragment.ClientActiveObjectiveListener,
        ResultsFragment.ResultsFragmentListener{

    private static final String ARG_VIEWSTATE = "state";
    private static final String ARG_ACTUALSTATE = "actuals";

    private NavigationView nav;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private LinearLayout mainLayout;

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
    }

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
        getSupportFragmentManager().beginTransaction().replace(R.id.clientContent, ClientActualFragment.newInstance(MockGenerator.mockActualMain())).commit();
    }

    void setMap(){
        Intent i = new Intent(ClientMainActivity.this, MapsActivity.class);
        i.putExtra(MapsActivity.MARKER_LOCATIONS, MockGenerator.mockMapData());
        startActivity(i);
    }

    void setStations(){
        Intent i = new Intent(ClientMainActivity.this, ClientStationsActivity.class);
        i.putExtra(ClientStationsActivity.ARGS_STATIONS, MockGenerator.mockStationsList());
        startActivity(i);
    }

    void goToObjective(){
        Intent i = new Intent(ClientMainActivity.this, ClientObjectiveActivity.class);
        i.putExtra(ClientObjectiveActivity.ARGS_OBJECTIVES, MockGenerator.mockBigObjectiveList());
        startActivity(i);
    }

    void setStatus(){
        changeState(ViewState.Status);
        toolbar.setTitle(R.string.status);
        getSupportFragmentManager().beginTransaction().replace(R.id.clientContent, ClientStatusFragment.newInstance(MockGenerator.mockStatusData())).commit();
    }

    void setHelp(){
        Intent i = new Intent(ClientMainActivity.this, ClientHelpActivity.class);
        i.putExtra(ClientHelpActivity.ARG_HELPDATA, MockGenerator.mockHelpData());
        startActivity(i);
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

    private enum ActualStatus{
        normal,
        objective,
        results
    }

    private ActualStatus actualStatus = ActualStatus.normal;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        showSnackBarMessage(getResources().getString(R.string.refresh));
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
        getSupportFragmentManager().beginTransaction().replace(R.id.clientContent, ResultsFragment.newInstance(MockGenerator.mockResultNames(), MockGenerator.mockResultPoints())).commit();
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
