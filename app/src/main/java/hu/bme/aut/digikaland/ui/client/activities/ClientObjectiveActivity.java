package hu.bme.aut.digikaland.ui.client.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.DialogInterface;
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
        setContentView(R.layout.activity_client_objective);
        ArrayList<Objective> objectives = (ArrayList<Objective>) getIntent().getSerializableExtra(ARGS_OBJECTIVES);
        ActionBar toolbar = getSupportActionBar();
        if(toolbar != null){
            toolbar.setDisplayHomeAsUpEnabled(true);
            toolbar.setTitle("Feladat");
        }
        if(savedInstanceState == null)
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

    private PictureObjectiveFragment pictureObjectiveFragmentSearch(String tag){
        return (PictureObjectiveFragment) getSupportFragmentManager().findFragmentByTag(tag);
    }

    private PictureFragment pictureFragmentSearch(String parentTag, String tag){
        return (PictureFragment) getSupportFragmentManager().findFragmentByTag(parentTag).getChildFragmentManager().findFragmentByTag(tag);
    }

    PictureObjectiveFragment pictureObjective = null;

    @Override
    public void activateCamera(String tag) {
        pictureObjective = pictureObjectiveFragmentSearch(tag);
        if(pictureObjective.isFreePicture())
            ClientObjectiveActivityPermissionsDispatcher.dispatchTakePictureIntentWithPermissionCheck(this);
        else{
            pictureObjective = null;
            showSnackBarMessage("Előbb törölnöd kell egy már meglévő képet.");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        ClientObjectiveActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_MULTIPLE_IMAGES_FROM_GALLERY = 3;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO)
            if(resultCode == RESULT_OK) {
                galleryAddPic();
                pictureObjective.givePicture(photoUri);
            }
            // ha visszavonták a képcsinálást, az ideiglenes fájlt törölni kell, hogy ne maradjon szemét
            else{
                File toDelete = new File(photoPath);
                toDelete.delete();
            }
        if(requestCode == REQUEST_MULTIPLE_IMAGES_FROM_GALLERY)
            if(resultCode == RESULT_OK){
                ClipData datas = data.getClipData();
                if(datas == null){
                    pictureObjective.givePicture(data.getData());
                }
                else{
                    int number = pictureObjective.getRemainingNumberOfPictures();
                    if(datas.getItemCount() > number) showSnackBarMessage("Túl sok képet jelöltél ki");
                    else{
                        for(int i = 0; i < datas.getItemCount(); i++){
                            pictureObjective.givePicture(datas.getItemAt(i).getUri());
                        }
                    }
                }
            }
        photoPath = null;
        photoUri = null;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(photoUri);
        this.sendBroadcast(mediaScanIntent);
    }

    @Override
    public void activateGallery(String tag) {
        pictureObjective = pictureObjectiveFragmentSearch(tag);
        if(!pictureObjective.isFreePicture()){
            showSnackBarMessage("Előbb törölnöd kell képet, hogy újat nyithass meg");
            return;
        }
        Intent getPicture = new Intent(Intent.ACTION_GET_CONTENT,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        getPicture.setType("image/jpeg");
        getPicture.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        startActivityForResult(getPicture, REQUEST_MULTIPLE_IMAGES_FROM_GALLERY);

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
        PictureFragment pf = pictureFragmentSearch(parentTag, tag);
        Uri path = pf.getPictureUri();
        if(path == null) return;
        showGallery(path);
    }

    @Override
    public void onExistingPictureLongClicked(String parentTag, String tag) {
        final PictureFragment pf = pictureFragmentSearch(parentTag, tag);
        new AlertDialog.Builder(this).setTitle("Kép törlése").setMessage("Biztosan törölni szeretnéd ezt a képet?")
                .setNegativeButton("Mégse", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Törlés", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        pf.deletePicture();
                    }
                }).create().show();
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

    Uri photoUri;
    String photoPath;

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        String pathName = getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).getAbsolutePath() + "/Digikaland";
        File storageDir = new File(pathName);
        if(!storageDir.exists()) storageDir.mkdir();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        photoPath = image.getAbsolutePath();
        // Save a file: path for use with ACTION_VIEW intents
        photoUri = FileProvider.getUriForFile(this,
                "hu.bme.aut.digikaland.fileprovider",
                image);
        return image;
    }

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
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }


}
