package hu.bme.aut.digikaland.ui.client.activities;

import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;

import java.util.List;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.ui.common.fragments.ContactFragment;
import hu.bme.aut.digikaland.utility.PhoneDial;

public class ClientHelpActivity extends AppCompatActivity implements ContactFragment.ClientHelpListener {
    public final static String ARGS_OBJADMINS = "objadmins";
    public final static String ARGS_OBJADMINSPHONE = "objadminsphone";
    public final static String ARGS_TOTADMINS = "totadmins";
    public final static String ARGS_TOTADMINSPHONE = "totadminsphone";

    LinearLayout mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_help);
        mainLayout = findViewById(R.id.clientHelp);
        ActionBar toolbar = getSupportActionBar();
        if(toolbar != null){
            toolbar.setDisplayHomeAsUpEnabled(true);
            toolbar.setTitle("Segítség");
        }
        List<String> objectAdminNames = getIntent().getStringArrayListExtra(ARGS_OBJADMINS);
        List<String> objectAdminPhones = getIntent().getStringArrayListExtra(ARGS_OBJADMINSPHONE);
        List<String> totalAdminNames = getIntent().getStringArrayListExtra(ARGS_TOTADMINS);
        List<String> totalAdminPhones = getIntent().getStringArrayListExtra(ARGS_TOTADMINSPHONE);

        for(int i = 0; i < objectAdminNames.size(); i++){
            getSupportFragmentManager().beginTransaction().add(R.id.clientHelpObjectiveAdminContent, ContactFragment.newInstance(objectAdminNames.get(i), objectAdminPhones.get(i), false)).commit();
        }

        for(int i = 0; i < totalAdminNames.size(); i++){
            getSupportFragmentManager().beginTransaction().add(R.id.clientHelpTotalAdminContent, ContactFragment.newInstance(totalAdminNames.get(i), totalAdminPhones.get(i), false)).commit();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_refresh, menu);
        return true;
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

    @Override
    public void phoneDial(String phoneNumber) {
        startActivity(PhoneDial.dial(phoneNumber));
    }

    // TODO: jelenleg csak placeholder megjelenítésre
    private void showSnackBarMessage(String message) {
        Snackbar.make(mainLayout, message, Snackbar.LENGTH_LONG).show();
    }
}
