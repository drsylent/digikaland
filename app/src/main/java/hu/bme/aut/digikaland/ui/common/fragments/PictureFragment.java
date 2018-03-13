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
import hu.bme.aut.digikaland.entities.Picture;

public class PictureFragment extends Fragment {
    private static final String ARG_PICTURE = "picture";
    private static final String ARG_PARENTTAG = "parentTag";
    private static final String ARG_HEIGHT = "he";
    private static final String ARG_WIDTH = "wi";
    ImageView imageView;
    private boolean empty = true;
    private String parentTag;
    private static int tagNumber = 0;
    private Picture picture = null;

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
        if(savedInstanceState != null && savedInstanceState.getString(ARG_PICTURE) != null){
            setPicture(Uri.parse(savedInstanceState.getString(ARG_PICTURE)), savedInstanceState.getInt(ARG_WIDTH), savedInstanceState.getInt(ARG_HEIGHT));
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!empty)
                    listener.onExistingPictureClicked(parentTag, getTag());
            }
        });
        imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if(!empty)
                    listener.onExistingPictureLongClicked(parentTag, getTag());
                return true;
            }
        });
        return root;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(picture != null){
            outState.putString(ARG_PICTURE, picture.getUri().toString());
            outState.putInt(ARG_HEIGHT, imageView.getHeight());
            outState.putInt(ARG_WIDTH, imageView.getWidth());
        }
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
        picture = null;
        imageView.setImageBitmap(null);
    }

    public Uri getPictureUri(){
        if(picture != null)
            return picture.getUri();
        else return null;
    }

    public void setPicture(Uri uri){
        setPicture(uri, imageView.getWidth(), imageView.getHeight());
    }

    public void setPicture(Uri uri, int width, int height){
        picture = new Picture(uri);
        Bitmap bmp = picture.openSmall(width, height, getActivity().getContentResolver());
        setPicture(bmp);
    }

    public interface PictureFragmentListener {
        void onExistingPictureClicked(String parentTag, String tag);
        void onExistingPictureLongClicked(String parentTag, String tag);
    }
}
