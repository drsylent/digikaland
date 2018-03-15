package hu.bme.aut.digikaland.ui.common.activities;

import android.Manifest;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import java.util.ArrayList;
import hu.bme.aut.digikaland.R;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final String ARGS_LATITUDE = "latitude";
    public static final String ARGS_LONGITUDE = "longitude";
    public static final String MARKER_LOCATIONS = "markerlocations";

    private GoogleMap mMap;
    private final LatLng bme = new LatLng(47.473372, 19.059731);
    private ArrayList<LatLng> coordinates = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle starter = getIntent().getBundleExtra(MARKER_LOCATIONS);
        double latitudes[] = starter.getDoubleArray(ARGS_LATITUDE);
        double longitudes[] = starter.getDoubleArray(ARGS_LONGITUDE);
        if(latitudes != null && longitudes != null)
        for(int i = 0; i < latitudes.length; i++){
            coordinates.add(new LatLng(latitudes[i], longitudes[i]));
        }
        setContentView(R.layout.activity_maps);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        for(LatLng l : coordinates){
            mMap.addMarker(new MarkerOptions().position(l).title("Bundle Test"));
        }
        mMap.setMinZoomPreference(13.0f);
        MapsActivityPermissionsDispatcher.startupWithPermissionCheck(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        MapsActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    // példakódból https://developers.google.com/maps/documentation/android-api/current-place-tutorial

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private static final String TAG = MapsActivity.class.getSimpleName();
    private Location mLastKnownLocation;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            Task locationResult = mFusedLocationProviderClient.getLastLocation();
            locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = task.getResult();
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(mLastKnownLocation.getLatitude(),
                                        mLastKnownLocation.getLongitude()), 16.0f));
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, "Exception: %s", task.getException());
                        mMap.moveCamera(CameraUpdateFactory
                                .newLatLngZoom(bme, 16.0f));
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                }
            });
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @NeedsPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    public void startup(){
        updateLocationUI();
        getDeviceLocation();
    }

    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION)
    public void moveToDefaultLocation(){
        mMap.moveCamera(CameraUpdateFactory
                .newLatLngZoom(bme, 16.0f));
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

}
