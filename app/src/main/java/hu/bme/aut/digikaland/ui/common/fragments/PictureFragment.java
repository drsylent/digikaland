package hu.bme.aut.digikaland.ui.common.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import hu.bme.aut.digikaland.R;

public class PictureFragment extends Fragment {
    private static final String ARG_PICTURE = "picture";
    private static final String ARG_PARENTTAG = "parentTag";
    ImageView imageView;
    private boolean empty = true;
    private String parentTag;
    private static int tagNumber = 0;

    public static String generateTag(){
        String tag = "PictureFragmentTag" + tagNumber;
        tagNumber++;
        return tag;
    }

    // TODO: Kép típusa és kezelése

    private PictureFragmentListener listener;

    public PictureFragment() {
        // Required empty public constructor
    }

    public boolean isEmpty(){
        return empty;
    }

    public static PictureFragment newInstance(String tag) {
        PictureFragment fragment = new PictureFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARENTTAG, tag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            parentTag = getArguments().getString(ARG_PARENTTAG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_picture, container, false);
        imageView = root.findViewById(R.id.picturePlace);
        // TODO: kép beállítása, ha van

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: csak ha van kép
                listener.onExistingPictureClicked(parentTag, getTag());
            }
        });
        return root;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof PictureFragmentListener) {
            listener = (PictureFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement PictureFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public void setPicture(Bitmap bmp){
        empty = false;
        imageView.setImageBitmap(bmp);
    }

    public void deletePicture(){
        empty = true;
        imageView.setImageBitmap(null);
    }

    public void setPicture(String mCurrentPhotoPath) {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
        setPicture(bitmap);
    }

    public interface PictureFragmentListener {
        void onExistingPictureClicked(String parentTag, String tag);
    }
}
