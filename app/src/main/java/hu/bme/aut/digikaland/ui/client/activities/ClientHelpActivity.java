package hu.bme.aut.digikaland.ui.client.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;
import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.entities.Contact;
import hu.bme.aut.digikaland.ui.common.fragments.ContactFragment;
import hu.bme.aut.digikaland.ui.common.fragments.TextFragment;
import hu.bme.aut.digikaland.utility.PhoneDial;

public class ClientHelpActivity extends AppCompatActivity{
    public final static String ARG_HELPDATA = "help";
    public final static String ARG_OBJECTADMINS = "objadmins";
    public final static String ARG_OBJECTADMINPHONES = "objadminsphone";
    public final static String ARG_TOTALADMINS = "totadmins";
    public final static String ARG_TOTALADMINPHONES = "totadminsphone";
    public final static String ARG_STATIONADMINS = "stationadmins";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_help);
        ActionBar toolbar = getSupportActionBar();
        if(toolbar != null){
            toolbar.setDisplayHomeAsUpEnabled(true);
            toolbar.setTitle(R.string.help);
        }
        if(savedInstanceState == null){
//            Bundle bundle = getIntent().getBundleExtra(ARG_HELPDATA);
//            List<String> objectAdminNames = bundle.getStringArrayList(ARG_OBJECTADMINS);
//            List<String> objectAdminPhones = bundle.getStringArrayList(ARG_OBJECTADMINPHONES);
//            List<String> totalAdminNames = bundle.getStringArrayList(ARG_TOTALADMINS);
//            List<String> totalAdminPhones = bundle.getStringArrayList(ARG_TOTALADMINPHONES);
//            if(objectAdminNames != null && objectAdminPhones != null)
//            for(int i = 0; i < objectAdminNames.size(); i++){
//                getSupportFragmentManager().beginTransaction().add(R.oldId.clientHelpObjectiveAdminContent, ContactFragment.newInstance(objectAdminNames.get(i), objectAdminPhones.get(i))).commit();
//            }
//            if(totalAdminNames != null && totalAdminPhones != null)
//            for(int i = 0; i < totalAdminNames.size(); i++){
//                getSupportFragmentManager().beginTransaction().add(R.oldId.clientHelpTotalAdminContent, ContactFragment.newInstance(totalAdminNames.get(i), totalAdminPhones.get(i))).commit();
//            }
            List<Contact> stationAdmins = (ArrayList<Contact>) getIntent().getSerializableExtra(ARG_STATIONADMINS);
            List<Contact> totalAdmins = (ArrayList<Contact>) getIntent().getSerializableExtra(ARG_TOTALADMINS);
            if(stationAdmins != null)
                for(Contact c : stationAdmins)
                    getSupportFragmentManager().beginTransaction().add(R.id.clientHelpObjectiveAdminContent, ContactFragment.newInstance(c)).commit();
            else getSupportFragmentManager().beginTransaction().add(R.id.clientHelpObjectiveAdminContent,
                    TextFragment.newInstance("Verseny közben itt jelennek majd meg a felügyelők", false)).commit();
            if(totalAdmins != null)
                for (Contact c : totalAdmins)
                    getSupportFragmentManager().beginTransaction().add(R.id.clientHelpTotalAdminContent, ContactFragment.newInstance(c)).commit();
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
