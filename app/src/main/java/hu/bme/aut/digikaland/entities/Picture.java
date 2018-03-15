package hu.bme.aut.digikaland.entities;

import android.content.ContentResolver;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;


public class Picture {
    private Uri uri;

    public Picture(Uri u){uri = u;}

    public Uri getUri(){
        return uri;
    }

    public Bitmap openSmall(int width, int height, ContentResolver resolver){
        InputStream stream;
        try{
            stream = resolver.openInputStream(uri);
            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(stream, null, bmOptions);
            stream.close();

            // vissza kell ugrani a stream elejére
            stream = resolver.openInputStream(uri);
            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;
            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW/width, photoH/height);
            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;
            Bitmap result = BitmapFactory.decodeStream(stream, null, bmOptions);
            stream.close();
            return result;
        }catch (FileNotFoundException e){
            Log.e("Uri open", "File not found");
        }catch (IOException e){
            Log.e("Stream close", "I/O error");
        }
        return null;
    }
}
