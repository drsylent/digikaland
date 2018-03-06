package hu.bme.aut.digikaland.entities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by Sylent on 2018. 03. 06..
 */

public class Picture {
    // TODO: URI tárolás? FileProvider Authority tárolás?
    private String pathName;

    public Picture(String path){
        pathName = path;
    }

    public String getPath(){
        return pathName;
    }

    public Bitmap openFull(){
        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(pathName, bmOptions);
    }

    // TODO: Ha urit kap, nem tud dekódolni
    public Bitmap openSmall(int width, int height){
        // Get the dimensions of the View
        int targetW = width;
        int targetH = height;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        return BitmapFactory.decodeFile(pathName, bmOptions);
    }
}
