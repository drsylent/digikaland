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
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.ui.admin.common.activities.AdminHelpActivity;
import hu.bme.aut.digikaland.ui.admin.common.activities.AdminStationsActivity;
import hu.bme.aut.digikaland.ui.admin.common.activities.AdminTeamsActivity;
import hu.bme.aut.digikaland.ui.admin.station.activities.AdminStationMainActivity;
import hu.bme.aut.digikaland.ui.admin.station.fragments.AdminStationActualFragment;
import hu.bme.aut.digikaland.ui.admin.total.fragments.AdminRaceStarterFragment;
import hu.bme.aut.digikaland.ui.admin.total.fragments.AdminRunningFragment;
import hu.bme.aut.digikaland.ui.common.activities.MapsActivity;
import hu.bme.aut.digikaland.ui.common.activities.SplashActivity;
import hu.bme.aut.digikaland.ui.common.fragments.ResultsFragment;
import hu.bme.aut.digikaland.utility.development.MockGenerator;

public class AdminTotalMainActivity extends AppCompatActivity implements ResultsFragment.ResultsFragmentListener,
        AdminRunningFragment.AdminRunningListener, AdminRaceStarterFragment.AdminStarterListener {

    private NavigationView nav;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private LinearLayout mainLayout;

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
        setNotStarted();
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
        setRunning();
    }

    @Override
    public void onEndPressed() {
        setFinished();
    }

    @Override
    public void onHelpPressed() {
        Intent i = new Intent(AdminTotalMainActivity.this, AdminHelpActivity.class);
        i.putExtra(AdminHelpActivity.ARG_HELPDATA, MockGenerator.mockAdminHelpData());
        startActivity(i);
    }

    private enum RaceState{
        NotStarted,
        Running,
        Finished
    }

    private RaceState state;

    private void setNotStarted(){
        state = RaceState.NotStarted;
        getSupportFragmentManager().beginTransaction().replace(R.id.adminStationContent,
                AdminRaceStarterFragment.newInstance(MockGenerator.mockALocation(), MockGenerator.mockATime())).commit();
    }

    private void setRunning(){
        state = RaceState.Running;
        getSupportFragmentManager().beginTransaction().replace(R.id.adminStationContent,
                AdminRunningFragment.newInstance(MockGenerator.mockALocation(), MockGenerator.mockATime(), MockGenerator.mockStatistics())).commit();
    }

    private void setFinished(){
        state = RaceState.Finished;
        getSupportFragmentManager().beginTransaction().replace(R.id.adminStationContent,
                ResultsFragment.newInstance(MockGenerator.mockResultNames(), MockGenerator.mockResultPoints())).commit();
    }

    private void startMap(){
        Intent i = new Intent(AdminTotalMainActivity.this, MapsActivity.class);
        i.putExtra(MapsActivity.MARKER_LOCATIONS, MockGenerator.mockMapBigData());
        startActivity(i);
    }

    private void startStations(){
        Intent i = new Intent(AdminTotalMainActivity.this, AdminStationsActivity.class);
        i.putExtra(AdminStationsActivity.ARGS_STATIONS, MockGenerator.mockAdminStationsList());
        startActivity(i);
    }

    private void startTeams(){
        // TODO: ez nem ugyanaz a csapat activity lesz! (vagy más módban fut?)
        Intent i = new Intent(AdminTotalMainActivity.this, AdminTeamsActivity.class);
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
}
