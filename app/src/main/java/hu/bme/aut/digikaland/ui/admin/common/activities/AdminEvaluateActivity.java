package hu.bme.aut.digikaland.ui.admin.common.activities;

import android.app.DialogFragment;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.entities.objectives.solutions.Solution;
import hu.bme.aut.digikaland.ui.common.fragments.NumberPickerDialogFragment;
import hu.bme.aut.digikaland.ui.common.fragments.PictureFragment;
import hu.bme.aut.digikaland.ui.common.objectives.solutions.PointDisplayFragment;
import hu.bme.aut.digikaland.ui.common.objectives.solutions.SolutionFragment;

public class AdminEvaluateActivity extends AppCompatActivity implements PictureFragment.PictureFragmentListener, PointDisplayFragment.PointHandleActivity,
        NumberPickerDialogFragment.PointSettingInterface{
    public final static String ARG_STATION = "stat";
    public final static String ARG_TEAM = "team";
    public final static String ARG_TIME = "time";
    public final static String ARG_PENALTY = "penalty";
    public final static String ARG_SOLUTIONS = "solut";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_evaluate);
        ActionBar toolbar = getSupportActionBar();
        if(toolbar != null){
            toolbar.setDisplayHomeAsUpEnabled(true);
            toolbar.setTitle(R.string.evaluate);
        }
        TextView textView = findViewById(R.id.adminEvaluateStation);
        int number = getIntent().getIntExtra(ARG_STATION, -1);
        textView.setText(getResources().getString(R.string.evaluate_station, number));
        textView = findViewById(R.id.adminEvaluatePenalty);
        number = getIntent().getIntExtra(ARG_PENALTY, -1);
        textView.setText(getResources().getString(R.string.evaluate_penalty_value, number));
        textView = findViewById(R.id.adminEvaluateTeam);
        textView.setText(getResources().getString(R.string.evaluate_team, getIntent().getStringExtra(ARG_TEAM)));
        textView = findViewById(R.id.adminEvaluateTime);
        Date date = new Date(getIntent().getLongExtra(ARG_TIME, 0));
        textView.setText(getResources().getString(R.string.evaluate_date, date));
        ArrayList<Solution> solutions = (ArrayList<Solution>) getIntent().getSerializableExtra(ARG_SOLUTIONS);
        for(Solution o : solutions){
            getSupportFragmentManager().beginTransaction().add(R.id.adminEvaluateContent, o.createFragment(), SolutionFragment.generateTag()).commit();
        }
        Button sendButton = findViewById(R.id.adminEvaluateSend);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: elküldés
            }
        });
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

    private PictureFragment pictureFragmentSearch(String parentTag, String tag){
        return (PictureFragment) getSupportFragmentManager().findFragmentByTag(parentTag).getChildFragmentManager().findFragmentByTag(tag);
    }

    private void showGallery(Uri path){
        // TODO: galériából törölhető a kép!!!!
        // fájl megnyitó uri: content://com.android.providers.media.documents/document/image%3A30449
        // galéria uri: content://media/external/images/media/30094
        Intent showPicture = new Intent(Intent.ACTION_VIEW);
        showPicture.setDataAndType(path, "image/jpeg");
        showPicture.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(showPicture);
    }

    @Override
    public void onExistingPictureClicked(String parentTag, String tag) {
        PictureFragment pf = pictureFragmentSearch(parentTag, tag);
        Uri path = pf.getPictureUri();
        if(path == null) return;
        showGallery(path);
    }

    // TODO: még nem tudjuk, hogy lesz a képletöltés
    // TODO: ha nem lesz megnyitható a uri, ilyet kell alkalmazni
    // teszteléshez egyelőre ezt kell használni
    @Override
    public void onExistingPictureClicked(PictureFragment frag) {
        Uri path = frag.getPictureUri();
        if(path == null) return;
        File file = new File(path.getSchemeSpecificPart());
        Uri photoUri = FileProvider.getUriForFile(this,
                "hu.bme.aut.digikaland.fileprovider",
                file);
        showGallery(photoUri);
    }

    @Override
    public void onExistingPictureLongClicked(String parentTag, String tag) {
    }

    @Override
    public void settingPoint(String hostTag, int current, int max) {
        DialogFragment pointSet = NumberPickerDialogFragment.newInstance(hostTag, current, max);
        pointSet.show(getFragmentManager(), null);
    }

    @Override
    public void pointSet(String fragmentTag, int point) {
        SolutionFragment frag = (SolutionFragment) getSupportFragmentManager().findFragmentByTag(fragmentTag);
        frag.setPoint(point);
    }
}
