package hu.bme.aut.digikaland.utility;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import java.util.ArrayList;

/**
 * Ennek az osztálynak a segítségével gyorsan el lehet érni helyreállás után a csatolt
 * fragmenteket.
 * @param <T> A Fragment pontos típusa.
 */
public class FragmentListSaver<T extends Fragment> {

    /**
     * A fragmentek tagjeit eltárolja egy listába.
     * @param fragments Az elmentendő Fragmentek.
     * @return Az elkészített lista a tagekből.
     */
    public ArrayList<String> fragmentTagSave(ArrayList<T> fragments){
        ArrayList<String> frags = new ArrayList<>();
        for(Fragment f : fragments){
            frags.add(f.getTag());
        }
        return frags;
    }

    /**
     * A megadott tagekből előállít egy megfelelő listát a fragmentekből.
     * @param tags A tagek listája.
     * @param fm A FragmentManager, akitől el kell kérni a fragmenteket.
     * @param target A Fragmentek pontos osztályára vissza lehet castolni.
     * @return A Fragmenteket tartalmazó lista.
     */
    public ArrayList<T> fragmentTagLoad(ArrayList<String> tags, FragmentManager fm, Class<T> target){
        ArrayList<T> fragments = new ArrayList<>();
        for(String tag : tags){
            fragments.add(target.cast(fm.findFragmentByTag(tag)));
        }
        return fragments;
    }
}
