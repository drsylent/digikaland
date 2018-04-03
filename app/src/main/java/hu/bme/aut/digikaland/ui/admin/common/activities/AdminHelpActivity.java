package hu.bme.aut.digikaland.ui.admin.common.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.entities.Contact;
import hu.bme.aut.digikaland.ui.common.fragments.ContactFragment;
import hu.bme.aut.digikaland.ui.common.fragments.TitleContactFragment;

public class AdminHelpActivity extends AppCompatActivity {
    public final static String ARG_HELPDATA = "help";
    public final static String ARG_OBJECTADMINS = "objadmins";
    public final static String ARG_OBJECTADMINPHONES = "objadminsphone";
    public final static String ARG_OBJECTIVENAMES = "objs";
    public final static String ARG_TEAMNAMES = "teams";
    public final static String ARG_TOTALADMINS = "totadmins";
    public final static String ARG_TOTALADMINPHONES = "totadminsphone";
    public final static String ARG_CAPTAINS = "captains";
    public final static String ARG_CAPTAINPHONES = "captainphone";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_help);
        ActionBar toolbar = getSupportActionBar();
        if(toolbar != null){
            toolbar.setDisplayHomeAsUpEnabled(true);
            toolbar.setTitle(R.string.help);
        }
        if(savedInstanceState == null){
            Bundle bundle = getIntent().getBundleExtra(ARG_HELPDATA);
            List<String> objectAdminNames = bundle.getStringArrayList(ARG_OBJECTADMINS);
            List<String> objectAdminPhones = bundle.getStringArrayList(ARG_OBJECTADMINPHONES);
            List<String> totalAdminNames = bundle.getStringArrayList(ARG_TOTALADMINS);
            List<String> totalAdminPhones = bundle.getStringArrayList(ARG_TOTALADMINPHONES);
            List<String> objectives = bundle.getStringArrayList(ARG_OBJECTIVENAMES);
            List<String> teams = bundle.getStringArrayList(ARG_TEAMNAMES);
            List<String> captainNames = bundle.getStringArrayList(ARG_CAPTAINS);
            List<String> captainPhones = bundle.getStringArrayList(ARG_CAPTAINPHONES);
            if(objectives != null && objectAdminNames != null && objectAdminPhones != null)
                for(int i = 0; i < objectAdminNames.size(); i++){
                    getSupportFragmentManager().beginTransaction().add(R.id.adminHelpStationAdminContent,
                            TitleContactFragment.newInstance(objectives.get(i), new Contact(objectAdminNames.get(i), objectAdminPhones.get(i)), false)).commit();
                }
            if(totalAdminNames != null && totalAdminPhones != null)
                for(int i = 0; i < totalAdminNames.size(); i++){
                    getSupportFragmentManager().beginTransaction().add(R.id.adminHelpTotalAdminContent,
                            ContactFragment.newInstance(totalAdminNames.get(i), totalAdminPhones.get(i))).commit();
                }
            if(teams != null && captainNames != null && captainPhones != null)
                for(int i = 0; i < captainNames.size(); i++){
                    getSupportFragmentManager().beginTransaction().add(R.id.adminHelpTeamsContent,
                            TitleContactFragment.newInstance(teams.get(i), new Contact(captainNames.get(i), captainPhones.get(i)), false)).commit();
                }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
