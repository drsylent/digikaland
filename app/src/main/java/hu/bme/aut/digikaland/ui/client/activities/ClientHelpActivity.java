package hu.bme.aut.digikaland.ui.client.activities;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;
import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.entities.Contact;
import hu.bme.aut.digikaland.ui.common.fragments.ContactFragment;
import hu.bme.aut.digikaland.ui.common.fragments.TextFragment;

public class ClientHelpActivity extends AppCompatActivity{
    public final static String ARG_TOTALADMINS = "totadmins";
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
            List<Contact> stationAdmins = (ArrayList<Contact>) getIntent().getSerializableExtra(ARG_STATIONADMINS);
            List<Contact> totalAdmins = (ArrayList<Contact>) getIntent().getSerializableExtra(ARG_TOTALADMINS);
            if(stationAdmins != null)
                for(Contact c : stationAdmins)
                    getSupportFragmentManager().beginTransaction().add(R.id.clientHelpObjectiveAdminContent, ContactFragment.newInstance(c)).commit();
            else getSupportFragmentManager().beginTransaction().add(R.id.clientHelpObjectiveAdminContent,
                    TextFragment.newInstance(getString(R.string.admins_go_here), false)).commit();
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
