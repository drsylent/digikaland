package hu.bme.aut.digikaland.utility;

import android.content.Intent;
import android.net.Uri;

/**
 * Ezen az osztályon keresztül lehet a tárcsázót gyorsan elérni.
 */
public class PhoneDial {
    /**
     * A tárcsázó megnyitása egy telefonszámmal.
     * @param phoneNumber A telefonszám, melyet tárcsáznia kell a telefonnak.
     * @return Az intent, melyet elindítva a tárcsázóba lehet kerülni a megfelelő telefonszámmal.
     */
    public static Intent dial(String phoneNumber){
        return new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+phoneNumber));
    }
}
