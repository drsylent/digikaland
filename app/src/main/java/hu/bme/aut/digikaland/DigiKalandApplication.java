package hu.bme.aut.digikaland;

import android.app.Application;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class DigiKalandApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // letiltjuk, hogy a korábban eltárolt lekérdezési eredményeket újrahasználja
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build();
        FirebaseFirestore.getInstance().setFirestoreSettings(settings);
    }
}
