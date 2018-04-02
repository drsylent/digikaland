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

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.entities.Contact;
import hu.bme.aut.digikaland.ui.admin.common.AdminHelpActivity;
import hu.bme.aut.digikaland.ui.admin.station.fragments.AdminStationActualFragment;
import hu.bme.aut.digikaland.ui.client.activities.ClientObjectiveActivity;
import hu.bme.aut.digikaland.utility.development.MockGenerator;

public class AdminStationMainActivity extends AppCompatActivity implements AdminStationActualFragment.AdminActivityInterface {

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
                        showSnackBarMessage("Map");
                        //setMap();
                        break;
                    case R.id.adminStations:
                        showSnackBarMessage("Station");
                        //setStations();
                        break;
                    case R.id.adminTeams:
                        showSnackBarMessage("Teams");
                        //setTeams();
                        break;
                }
                invalidateOptionsMenu();
                return false;
            }
        });
        setupToolbar();
        toolbar.setTitle(R.string.actual);
        nav.getMenu().getItem(0).setChecked(true);
        fragment = AdminStationActualFragment.newInstance("Ez egy állomás", "Ez meg a pontosítás");
        getSupportFragmentManager().beginTransaction().add(R.id.adminStationContent, fragment).commit();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        showSnackBarMessage(getResources().getString(R.string.refresh));
        MockGenerator.adminStationCycleStep();
        fragment.refreshAllData();
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
            showSnackBarMessage("Evaluate");
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
