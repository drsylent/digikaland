package hu.bme.aut.digikaland.ui.client.activities;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.entities.objectives.Objective;
import hu.bme.aut.digikaland.ui.common.fragments.PictureFragment;
import hu.bme.aut.digikaland.ui.common.objectives.ObjectiveFragment;
import hu.bme.aut.digikaland.ui.common.objectives.PictureObjectiveFragment;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

import static android.os.Environment.getExternalStoragePublicDirectory;

@RuntimePermissions
public class ClientObjectiveActivity extends AppCompatActivity implements PictureObjectiveFragment.PictureObjectiveListener, PictureFragment.PictureFragmentListener {
    public final static String ARGS_OBJECTIVES = "objectives";
    ArrayList<ObjectiveFragment> fragments = new ArrayList<>();
    LinearLayout mainLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: elfektetéskor rengeteg újracsatolás van... ha sIS null, akkor kell csak
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
            //dispatchTakePictureIntent();
            ClientObjectiveActivityPermissionsDispatcher.dispatchTakePictureIntentWithPermissionCheck(this);
        else{
            givePicture = null;
            showSnackBarMessage("Előbb törölnöd kell egy már meglévő képet.");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        ClientObjectiveActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE)
            if(resultCode == RESULT_OK) {
                givePicture.givePicture(mCurrentPhotoPath);
            }
            // ha visszavonták a képcsinálást, az ideiglenes fájlt törölni kell, hogy ne maradjon szemét
            else{
                File toDelete = new File(mCurrentPhotoPath);
                toDelete.delete();
            }
        if(requestCode == REQUEST_IMAGE_FROM_GALLERY)
            if(resultCode == RESULT_OK){
                givePicture.givePicture(data.getData());
            }
    }

    static final int REQUEST_IMAGE_FROM_GALLERY = 2;

    @Override
    public void activateGallery(String tag) {
        givePicture = (PictureObjectiveFragment) getSupportFragmentManager().findFragmentByTag(tag);
        int number = givePicture.getRemainingNumberOfPictures();
        if(number == 0){
            showSnackBarMessage("Előbb törölnöd kell képet, hogy újat nyithass meg");
            return;
        }
        Intent getPicture = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        getPicture.setType("image/*");
        // TODO: allow multiple? SDK emelés?
        //if(number > 1) getPicture.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(getPicture, REQUEST_IMAGE_FROM_GALLERY);
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
        String path = pf.getPicturePath();
        if(path == null) return;
        showGallery(path);
    }

    @Override
    public void onExistingPictureLongClicked(String parentTag, String tag) {
        // TODO: dialógus feldobása, hogy biztos törölni akarod-e
        PictureFragment pf = (PictureFragment) getSupportFragmentManager().findFragmentByTag(parentTag).getChildFragmentManager().findFragmentByTag(tag);
        pf.deletePicture();
    }

    private void showGallery(String path){
        Intent showPicture = new Intent(Intent.ACTION_VIEW);
        Uri photoURI = FileProvider.getUriForFile(this,
                "hu.bme.aut.digikaland.fileprovider",
                new File(path));
        showPicture.setDataAndType(photoURI, "image/jpeg");
        showPicture.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//        showPicture.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(showPicture);
    }

    String mCurrentPhotoPath;


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        String pathName = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/Digikaland";
        // TODO: ennél specifikusabb helyre menjen...
        File storageDir = new File(pathName);
        if(!storageDir.exists()) storageDir.mkdir();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    static final int REQUEST_IMAGE_CAPTURE = 1;

//    private void dispatchTakePictureIntent() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//        }
//    }

    static final int REQUEST_TAKE_PHOTO = 1;

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e("IO EXCEPTION: ", ex.getMessage());
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "hu.bme.aut.digikaland.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


}
