package hu.bme.aut.digikaland.ui.admin.common.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import java.util.List;
import java.util.Map;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.entities.Contact;
import hu.bme.aut.digikaland.ui.common.fragments.ContactFragment;
import hu.bme.aut.digikaland.ui.common.fragments.TitleContactFragment;

public class AdminHelpActivity extends AppCompatActivity {
    public final static String ARG_HELPDATA = "help";
    public final static String ARG_OBJECTADMINS = "objadmins";
    public final static String ARG_TOTALADMINS = "totadmins";
    public final static String ARG_CAPTAINS = "captains";

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
            Map<String, List<Contact>> objectiveAdmins = (Map<String, List<Contact>>) bundle.getSerializable(ARG_OBJECTADMINS);
            List<Contact> totalAdmins = (List<Contact>) bundle.getSerializable(ARG_TOTALADMINS);
            Map<String, Contact> captains = (Map<String, Contact>) bundle.getSerializable(ARG_CAPTAINS);
            if(totalAdmins != null)
                for(Contact c : totalAdmins){
                    getSupportFragmentManager().beginTransaction().add(R.id.adminHelpTotalAdminContent,
                            ContactFragment.newInstance(c)).commit();
                }
            if(objectiveAdmins != null) {
                for (String objectiveName : objectiveAdmins.keySet()) {
                    List<Contact> contacts = objectiveAdmins.get(objectiveName);
                    for (Contact contact : contacts) {
                        getSupportFragmentManager().beginTransaction().add(R.id.adminHelpStationAdminContent,
                                TitleContactFragment.newInstance(objectiveName, contact, false)).commit();
                    }
                }
            }
            if(captains != null){
                for(String teamName : captains.keySet()){
                    getSupportFragmentManager().beginTransaction().add(R.id.adminHelpTeamsContent,
                            TitleContactFragment.newInstance(teamName, captains.get(teamName), false)).commit();
                }
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
