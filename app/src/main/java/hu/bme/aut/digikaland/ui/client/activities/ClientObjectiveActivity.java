package hu.bme.aut.digikaland.ui.client.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.entities.objectives.Objective;
import hu.bme.aut.digikaland.entities.objectives.PictureObjective;
import hu.bme.aut.digikaland.ui.common.fragments.ContactFragment;
import hu.bme.aut.digikaland.ui.common.fragments.PictureFragment;
import hu.bme.aut.digikaland.ui.common.objectives.ObjectiveFragment;
import hu.bme.aut.digikaland.ui.common.objectives.PictureObjectiveFragment;

public class ClientObjectiveActivity extends AppCompatActivity implements PictureObjectiveFragment.PictureObjectiveListener, PictureFragment.PictureFragmentListener {
    public final static String ARGS_OBJECTIVES = "objectives";
    ArrayList<ObjectiveFragment> fragments = new ArrayList<>();
    LinearLayout mainLayout;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_objective);
        ArrayList<Objective> objectives = (ArrayList<Objective>) getIntent().getSerializableExtra(ARGS_OBJECTIVES);
        ActionBar toolbar = getSupportActionBar();
        if(toolbar != null){
            toolbar.setDisplayHomeAsUpEnabled(true);
            toolbar.setTitle("Feladat");
        }
        for(Objective o : objectives){
            ObjectiveFragment fragment = o.createFragment();
            fragments.add(fragment);
            getSupportFragmentManager().beginTransaction().add(R.id.clientQuestionContent, fragment, ObjectiveFragment.generateTag()).commit();
        }
        Button send = findViewById(R.id.clientQuestionSend);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendSolution();
            }
        });
        mainLayout = findViewById(R.id.clientObjectiveMain);
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

    PictureObjectiveFragment givePicture = null;

    @Override
    public void activateCamera(String tag) {
        givePicture = (PictureObjectiveFragment) getSupportFragmentManager().findFragmentByTag(tag);
        if(givePicture.isFreePicture())
            dispatchTakePictureIntent();
        else{
            givePicture = null;
            showSnackBarMessage("Előbb törölnöd kell egy már meglévő képet.");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            givePicture.givePicture(data.getExtras());
        }
    }

    @Override
    public void activateGallery(String tag) {
        showSnackBarMessage("Galéria");
    }

    public void sendSolution(){
        // itt küldi el a megoldást majd
        showSnackBarMessage("Elküldés");
    }

    // TODO: jelenleg csak placeholder megjelenítésre
    private void showSnackBarMessage(String message) {
        Snackbar.make(mainLayout, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onExistingPictureClicked(String parentTag, String tag) {
        PictureFragment pf = (PictureFragment) getSupportFragmentManager().findFragmentByTag(parentTag).getChildFragmentManager().findFragmentByTag(tag);
        // TODO: ne töröljünk egyből, hanem egy preview-t jelenítsünk meg a képről, és itt lehessen törölni, ha nem kérjük
        pf.deletePicture();
    }
}
