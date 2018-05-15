package hu.bme.aut.digikaland.ui.common.activities;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import java.util.ArrayList;
import java.util.List;

import hu.bme.aut.digikaland.R;
import hu.bme.aut.digikaland.dblogic.AdminStationEngine;
import hu.bme.aut.digikaland.dblogic.enumeration.ErrorType;
import hu.bme.aut.digikaland.entities.Contact;
import hu.bme.aut.digikaland.entities.EvaluationStatistics;
import hu.bme.aut.digikaland.entities.Team;
import hu.bme.aut.digikaland.entities.station.StationAdminPerspective;
import hu.bme.aut.digikaland.entities.station.StationAdminPerspectiveSummary;
import hu.bme.aut.digikaland.entities.station.StationMapData;
import hu.bme.aut.digikaland.ui.admin.common.activities.AdminStationSummaryActivity;
import hu.bme.aut.digikaland.ui.admin.common.activities.AdminStationsActivity;
import hu.bme.aut.digikaland.utility.development.MockGenerator;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.OnPermissionDenied;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, AdminStationEngine.CommunicationInterface {

    public static final String MARKER_LOCATIONS = "markerlocations";
    public static final String MARKER_SPECIAL = "indexofspecial";
    public static final String MARKER_INTERACTIVITY = "interactivity";

    private GoogleMap mMap;
    private final LatLng bme = new LatLng(47.473372, 19.059731);
    private ArrayList<StationMapData> stations;
    private int specialindex;
    private boolean interactive = false;
    private boolean newactivity;
    private View mainLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        newactivity = savedInstanceState == null;
        Bundle starter = getIntent().getBundleExtra(MARKER_LOCATIONS);
        stations = (ArrayList<StationMapData>) starter.getSerializable(MARKER_LOCATIONS);
        interactive = starter.getBoolean(MARKER_INTERACTIVITY, false);
        specialindex = starter.getInt(MARKER_SPECIAL, -1);
        setContentView(R.layout.activity_maps);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mainLayout = findViewById(R.id.map);
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
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(interactive){
                    prepareStationSummary((StationMapData) marker.getTag());
                }
                return false;
            }
        });
        for(int i = 0; i < stations.size(); i++){
            StationMapData station = stations.get(i);
            MarkerOptions options = new MarkerOptions();
            options.position(station.getLocation()).title(station.getStationName());
            if(i == specialindex) {
                options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                options.zIndex(1.0f);
            }
            mMap.addMarker(options).setTag(station);
        }
        mMap.setMinZoomPreference(13.0f);
        MapsActivityPermissionsDispatcher.startupWithPermissionCheck(this);
    }

    private EvaluationStatistics statistics;
    private LatLng loc;

    private void prepareStationSummary(StationMapData item){
        statistics = item.statistics;
        loc = item.getLocation();
        AdminStationEngine.getInstance(this).loadStationData(item.station.id);
    }

    @Override
    public void stationSummaryLoaded(String stationId, hu.bme.aut.digikaland.entities.Location location, ArrayList<Contact> stationAdmins) {
        setStationSummary(stationId, location, stationAdmins);
    }

    @Override
    public void adminStationError(ErrorType type) {
        showSnackBarMessage(type.getDefaultMessage());
    }

    private void setStationSummary(String stationId, hu.bme.aut.digikaland.entities.Location location, ArrayList<Contact> stationAdmins){
        Intent placeData = new Intent(MapsActivity.this, AdminStationSummaryActivity.class);
        placeData.putExtra(AdminStationSummaryActivity.ARG_STATUS, statistics);
        placeData.putExtra(AdminStationSummaryActivity.ARG_LOCATION, location);
        placeData.putExtra(AdminStationSummaryActivity.ARG_STATIONID, Integer.parseInt(stationId));
        placeData.putExtra(AdminStationSummaryActivity.ARG_CONTACT, stationAdmins);
        placeData.putExtra(AdminStationSummaryActivity.ARG_LATITUDE, loc.latitude);
        placeData.putExtra(AdminStationSummaryActivity.ARG_LONGITUDE, loc.longitude);
        goToStationSummary(placeData);
    }

    private void goToStationSummary(Intent i){
        startActivity(i);
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
        if(newactivity) getDeviceLocation();
    }

    @OnPermissionDenied(Manifest.permission.ACCESS_FINE_LOCATION)
    public void moveToDefaultLocation(){
        mMap.moveCamera(CameraUpdateFactory
                .newLatLngZoom(bme, 16.0f));
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
    }

    // TODO: jelenleg csak placeholder megjelenítésre
    private void showSnackBarMessage(String message) {
        Snackbar.make(mainLayout, message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void stationTeamDataLoaded(ArrayList<StationAdminPerspective> stations) {

    }

    @Override
    public void allStationLoadCompleted(ArrayList<StationAdminPerspective> list) {

    }

    @Override
    public void allTeamStatusLoaded(ArrayList<Team> teams) {

    }
}
