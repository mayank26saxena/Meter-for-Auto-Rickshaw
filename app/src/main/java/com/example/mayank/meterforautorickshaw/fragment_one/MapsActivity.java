package com.example.mayank.meterforautorickshaw.fragment_one;

import android.app.AlertDialog;
import android.content.IntentSender;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mayank.meterforautorickshaw.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends Fragment implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    public static final String TAG = MapsActivity.class.getSimpleName();

    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private TextView tvStartRide;
    private TextView tvStopRide;

    private Location startingLocation;
    private Location endingLocation;

    private String origin = null ;
    private String destination = null ;

    private Location location1 ;
    private Location location2 ;

    private LatLng loc1 ;
    private LatLng loc2 ;

    private float distance = 0 ;
    private float fare = 0 ;

    private double startLatitude ;
    private double startLongitude ;
    private double endLatitude ;
    private double endLongitude ;
    private float[] results ;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.activity_maps, container, false);

        setUpMapIfNeeded();

        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds

        tvStartRide = (TextView) view.findViewById(R.id.startRideText);
        tvStopRide = (TextView) view.findViewById(R.id.stopRideText);

        tvStopRide.setVisibility(View.INVISIBLE);

        tvStartRide.setOnClickListener(startRideOnClickListener);
        tvStopRide.setOnClickListener(stopRideOnClickListener);

        return view;


    }

    View.OnClickListener startRideOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            tvStartRide.setVisibility(View.INVISIBLE);
            tvStopRide.setVisibility(View.VISIBLE);

            startingLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            location1 = startingLocation ;

            startLatitude = location1.getLatitude();
            startLongitude = location1.getLongitude();

            loc1 = new LatLng(startLatitude, startLongitude) ;

            try {
                origin = retrieveAddress(startingLocation) ;
                Toast.makeText(getContext(), "Ride starting from: " + origin, Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    };

    View.OnClickListener stopRideOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {

            endingLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            try {
                destination = retrieveAddress(endingLocation) ;
                Toast.makeText(getContext(), "Ride ended at: " + destination, Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                e.printStackTrace();
            }

            fare = calculateFare(distance) ;


        }
    };

    public String retrieveAddress(Location location) throws IOException {

        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        /*String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode(); */
        String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL

        if (address != null)
            return address;
        else if (knownName != null)
            return knownName;
        else
            return null;
    }

    public void drawPolyline(LatLng location1, LatLng location2){

        Polyline line = mMap.addPolyline(new PolylineOptions()
                .add(location1, location2)
                .width(5)
                .color(Color.RED));
    }

    /*@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
    } */

    @Override
    public void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mGoogleApiClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();

        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            //mMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            mMap = ((SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.map)).getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));

    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());

        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        LatLng latLng = new LatLng(currentLatitude, currentLongitude);

        //mMap.addMarker(new MarkerOptions().position(new LatLng(currentLatitude, currentLongitude)).title("Current Location"));
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("I am here!");
        mMap.addMarker(options);
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 14.0f));

        if (location2 == null) {

            location2 = location ;
            loc2 = latLng ;

            endLatitude = loc2.latitude ;
            endLongitude = loc2.longitude ;

            //drawPolyline(loc1, loc2);

            //android.location.Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results);

            //if ( results != null )
            //distance += results[0] ;

            Toast.makeText(getActivity(), "Distance: " + distance, Toast.LENGTH_SHORT).show();

        }

        else {

            loc1 = loc2 ;
            loc2 = latLng ;

            startLatitude = loc1.latitude ;
            startLongitude = loc1.longitude ;

            endLatitude = loc2.latitude ;
            endLongitude = loc2.longitude ;

            drawPolyline(loc1, loc2);

            android.location.Location.distanceBetween(startLatitude, startLongitude, endLatitude, endLongitude, results);

            distance += results[0] ;

            Toast.makeText(getActivity(), "Distance: " + distance, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            handleNewLocation(location);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Oops!");
        builder.setMessage("An error occurred.");
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(getActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("Oops!");
            builder.setMessage("An error occurred.");
            AlertDialog dialog = builder.create();
            dialog.show();

            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        handleNewLocation(location);
    }

    public float calculateFare(float distance) {

        float distanceInkm = distance/1000 ;
        float firstTwokm = 2 ;
        float restOfDistance = distanceInkm - firstTwokm ;

        if (distanceInkm >= firstTwokm)
        {
            fare = 25 ;

            while (restOfDistance>=0) {

                fare += 0.8 ;
                restOfDistance -= 0.1 ;

            }

            return fare ;
        }

        else {

            while (distanceInkm>=0) {

                fare += 1.25 ;
                distanceInkm -= 0.1 ;
            }

            return fare ;
        }
    }

}