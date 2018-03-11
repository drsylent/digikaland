package hu.bme.aut.digikaland.utility;

import android.content.Intent;
import android.net.Uri;

/**
 * Created by Sylent on 2018. 03. 11..
 */

public class PhoneDial {
    public static Intent dial(String phoneNumber){
        return new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+phoneNumber));
    }
}
