package hu.bme.aut.digikaland.ui.client;

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
import hu.bme.aut.digikaland.ui.client.fragments.ClientStatusFragment;

public class ClientMainActivity extends AppCompatActivity {

    Toolbar toolbar;
    DrawerLayout drawerLayout;
    MenuItem activeItem;
    LinearLayout mainLayout;

    private enum ViewState{
        Actual,
        Map,
        Status
    }

    ViewState state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_main);
        mainLayout = findViewById(R.id.clientContent);
        drawerLayout = findViewById(R.id.clientDrawer);
        NavigationView nav = findViewById(R.id.clientNavigation);
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                drawerLayout.closeDrawers();
                if(activeItem == item) return false;
                activeItem.setChecked(false);
                activeItem = item;
                activeItem.setChecked(true);
                switch(item.getItemId()){
                    case R.id.clientActual:
                        setActual();
                        break;
                    case R.id.clientMap:
                        setMap();
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
        activeItem = nav.getMenu().getItem(0);
        activeItem.setChecked(true);

    }

    void setActual(){
        state = ViewState.Actual;
        toolbar.setTitle(R.string.actual);
        getSupportFragmentManager().beginTransaction().replace(R.id.clientContent, ClientActualFragment.newInstance("", "")).commit();
    }

    void setMap(){
        showSnackBarMessage("Map Active");
    }

    void setStatus(){
        state = ViewState.Status;
        toolbar.setTitle(R.string.status);
        getSupportFragmentManager().beginTransaction().replace(R.id.clientContent, ClientStatusFragment.newInstance("", "")).commit();
    }

    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(findViewById(R.id.clientNavigation))) drawerLayout.closeDrawers();
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

    private void showSnackBarMessage(String message) {
        Snackbar.make(mainLayout, message, Snackbar.LENGTH_LONG).show();
    }
}
