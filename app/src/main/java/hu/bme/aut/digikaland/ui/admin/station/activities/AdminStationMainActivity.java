package hu.bme.aut.digikaland.ui.admin.station.activities;

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

import java.util.Date;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.entities.Contact;
import hu.bme.aut.digikaland.ui.admin.common.activities.AdminEvaluateActivity;
import hu.bme.aut.digikaland.ui.admin.common.activities.AdminHelpActivity;
import hu.bme.aut.digikaland.ui.admin.common.activities.AdminStationsActivity;
import hu.bme.aut.digikaland.ui.admin.common.activities.AdminTeamsActivity;
import hu.bme.aut.digikaland.ui.admin.station.fragments.AdminStationActualFragment;
import hu.bme.aut.digikaland.ui.client.activities.ClientObjectiveActivity;
import hu.bme.aut.digikaland.ui.common.activities.MapsActivity;
import hu.bme.aut.digikaland.ui.common.activities.SplashActivity;
import hu.bme.aut.digikaland.ui.common.fragments.ResultsFragment;
import hu.bme.aut.digikaland.utility.development.MockGenerator;

public class AdminStationMainActivity extends AppCompatActivity implements AdminStationActualFragment.AdminActivityInterface, ResultsFragment.ResultsFragmentListener {

    private NavigationView nav;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private LinearLayout mainLayout;
    private AdminStationActualFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_station_main);
        mainLayout = findViewById(R.id.adminStationContent);
        drawerLayout = findViewById(R.id.adminDrawer);
        nav = findViewById(R.id.adminNavigation);
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();
                switch(item.getItemId()){
                    case R.id.adminMap:
                        startMap();
                        break;
                    case R.id.adminStations:
                        startStations();
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
        setActual();
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
        startActivity(new Intent(AdminStationMainActivity.this, SplashActivity.class));
    }

    private enum ContentState{
        Actual,
        Results
    }

    private ContentState state;

    private void setActual(){
        state = ContentState.Actual;
        fragment = AdminStationActualFragment.newInstance("Ez egy állomás", "Ez meg a pontosítás");
        getSupportFragmentManager().beginTransaction().replace(R.id.adminStationContent, fragment).commit();
    }

    private void setResults(){
        state = ContentState.Results;
        getSupportFragmentManager().beginTransaction().replace(R.id.adminStationContent, ResultsFragment.newInstance(MockGenerator.mockResultNames(), MockGenerator.mockResultPoints())).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        showSnackBarMessage(getResources().getString(R.string.refresh));
        MockGenerator.adminStationCycleStep();
        if(MockGenerator.adminStationIsResultsActive()) setResults();
        else{
            if(state == ContentState.Actual) fragment.refreshAllData();
            else setActual();
        }
//        if(state == ClientMainActivity.ViewState.Actual)
//            switch(item.getItemId()) {
//                case R.id.menu_refresh:
//                    switch(actualStatus) {
//                        case normal:
//                            setObjective();
//                            break;
//                        case objective:
//                            setResults();
//                            break;
//                        case results:
//                            setActual();
//                            break;
//                    }
//            }
        return super.onOptionsItemSelected(item);
    }

    private void startMap(){
        Intent i = new Intent(AdminStationMainActivity.this, MapsActivity.class);
        i.putExtra(MapsActivity.MARKER_LOCATIONS, MockGenerator.mockMapBigData());
        startActivity(i);
    }

    private void startStations(){
        Intent i = new Intent(AdminStationMainActivity.this, AdminStationsActivity.class);
        i.putExtra(AdminStationsActivity.ARGS_STATIONS, MockGenerator.mockAdminStationsSummaryList());
        i.putExtra(AdminStationsActivity.ARG_SUMMARY, true);
        startActivity(i);
    }

    private void startTeams(){
        Intent i = new Intent(AdminStationMainActivity.this, AdminTeamsActivity.class);
        i.putExtra(AdminTeamsActivity.ARG_TEAMS, MockGenerator.mockAdminTeamsList());
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

    @Override
    public int getEvaluated() {
        return 1;
    }

    @Override
    public int getDone() {
        return 2;
    }

    @Override
    public int getSum() {
        return 3;
    }

    @Override
    public boolean isEnding() {
        return MockGenerator.adminStationIsEnding();
    }

    @Override
    public String getNextTeamName() {
        return MockGenerator.adminStationGetNextTeamName();
    }

    @Override
    public Contact getNextTeamContact() {
        return MockGenerator.adminStationGetNextContact();
    }

    @Override
    public void onEvaluateActivation() {
        if(isToEvaluate()) {
            Intent i = new Intent(AdminStationMainActivity.this, AdminEvaluateActivity.class);
            i.putExtra(AdminEvaluateActivity.ARG_SOLUTIONS, MockGenerator.mockSolutionList());
            i.putExtra(AdminEvaluateActivity.ARG_STATION, 2);
            i.putExtra(AdminEvaluateActivity.ARG_TIME, new Date(118, 3, 3).getTime());
            i.putExtra(AdminEvaluateActivity.ARG_TEAM, "Narancs csapat");
            i.putExtra(AdminEvaluateActivity.ARG_PENALTY, 23);
            i.putExtra(AdminEvaluateActivity.ARG_SEND, true);
            startActivity(i);
        }
        else showSnackBarMessage("Nincs kiértékelésre váró csapat!");
    }

    @Override
    public void onObjectivesActivation() {
        // TODO: egy egyszerűsített nézet lenne ide jobb, nem a teljes
        Intent i = new Intent(AdminStationMainActivity.this, ClientObjectiveActivity.class);
        i.putExtra(ClientObjectiveActivity.ARGS_OBJECTIVES, MockGenerator.mockBigObjectiveList());
        startActivity(i);
    }

    @Override
    public void onHelpActivation() {
        Intent i = new Intent(AdminStationMainActivity.this, AdminHelpActivity.class);
        i.putExtra(AdminHelpActivity.ARG_HELPDATA, MockGenerator.mockAdminHelpData());
        startActivity(i);
    }

    @Override
    public boolean isToEvaluate() {
        return MockGenerator.adminStationIsToEvaluate();
    }
}
