package hu.bme.aut.digikaland.utility;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;

/**
 * Created by Sylent on 2018. 03. 11..
 */

public class FragmentListSaver<T extends Fragment> {

    public ArrayList<String> fragmentTagSave(ArrayList<T> fragments){
        ArrayList<String> frags = new ArrayList<>();
        for(Fragment f : fragments){
            frags.add(f.getTag());
        }
        return frags;
    }

    public ArrayList<T> fragmentTagLoad(ArrayList<String> tags, FragmentManager fm, Class<T> target){
        ArrayList<T> fragments = new ArrayList<>();
        for(String tag : tags){
            fragments.add(target.cast(fm.findFragmentByTag(tag)));
        }
        return fragments;
    }
}
