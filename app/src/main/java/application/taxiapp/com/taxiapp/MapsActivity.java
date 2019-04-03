package application.taxiapp.com.taxiapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.teamspaghetti.easyroutecalculation.EasyRouteCalculation;
import com.teamspaghetti.easyroutecalculation.TravelMode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import application.taxiapp.com.taxiapp.constant.GPSTracker;
import application.taxiapp.com.taxiapp.constant.MySharedPref;
import application.taxiapp.com.taxiapp.mapHttpConnection.HttpConnection;
import application.taxiapp.com.taxiapp.mapJsonParser.PathJSONParser;

import static android.support.constraint.Constraints.TAG;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, NavigationView.OnNavigationItemSelectedListener, RoutingListener {

    private GoogleMap mMap;
    View mapView;
    private UiSettings mUiSettings;

    private long back_pressed;
    MySharedPref pref;
    TextView username,useremail;
    //session manager
    String ldata,userName,userEmail;
    NavigationView navigationView;
    //
    TextView TextView_start,TextView_destination;

    double currentlat, currentlong;
    LatLng origin,destination;
    // bunch of location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_maps);

        if (!CheckGooglePlayServices()) {
            Log.d("onCreate", "Finishing test case since Google Play Services are not available");
            //finish();
        }
        else {
            Log.d("onCreate","Google Play Services available.");
        }


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);
        //getSupportActionBar().setDisplayShowTitleEnabled(false);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getBundleData();
        initialization();
        //for check runtime permission for access location.
        CheckForPermissionToUpdateLocation();
        onClickListner();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);
    }



    //----------------------------------------------

    private void getBundleData()
    {
        pref = new MySharedPref(this);
        ldata=pref.getData("ldata");

        //-------------
        try {
            System.out.println(ldata);
            JSONObject jsonObject = new JSONObject(ldata);

            userName = jsonObject.getString("first_name");
            userEmail = jsonObject.getString("email");
            System.out.println(userName);

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    //fo rinitialization view--------------------
    private void initialization()
    {
        //textviews
        View headerView = navigationView.getHeaderView(0);
        username = (TextView)headerView.findViewById(R.id.user_name);
        useremail = (TextView)headerView.findViewById(R.id.user_email);
        //
        username.setText(userName);
        useremail.setText(userEmail);

        //Autocomplete textview initialization----
        TextView_start = (TextView)findViewById(R.id.start_location);
        TextView_destination = (TextView)findViewById(R.id.destination);



        if (!CheckGooglePlayServices()) {
            Log.d("onCreate", "Finishing test case since Google Play Services are not available");
            //finish();
        } else {
            Log.d("onCreate", "Google Play Services available.");

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mSettingsClient = LocationServices.getSettingsClient(this);

            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    // location is received
                    mCurrentLocation = locationResult.getLastLocation();
                    System.out.println("Current Latitude@@@" + mCurrentLocation.getLatitude());
                    System.out.println("Current Longitude@@@" + mCurrentLocation.getLongitude());

                    getLocation();
                }
            };

            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(15000);
            mLocationRequest.setFastestInterval(10000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(mLocationRequest);
            mLocationSettingsRequest = builder.build();
        }

    }

    //---------------------------------------------------------------
    private void onClickListner() {

        TextView_destination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsActivity.this,RideLocationActivity.class));
            }
        });

       TextView_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MapsActivity.this,RideLocationActivity.class));
            }
        });
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

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(currentlat, currentlong);
        // mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));

        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney,15));

        mMap = googleMap;
        mUiSettings = mMap.getUiSettings();
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);
        // Add a marker in Sydney and move the camera
        LatLng latlng = new LatLng(currentlat, currentlong);
        //currentMarker = mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in current city"));
        //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15));

        CameraPosition camPos = new CameraPosition.Builder()
                .target(latlng)
                .zoom(18)
                .tilt(70)
                .build();
        CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
        mMap.animateCamera(camUpd3);

        //CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(latlng);
        // CameraUpdate zoom=CameraUpdateFactory.zoomTo(15);

        // mMap.moveCamera(cameraUpdate);
        // mMap.animateCamera(zoom);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
      if (mapView != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 30);
        }

        DrawPath();

    }

    //--------------------------------------------------
    private void DrawPath()
    {
        if (getIntent().getExtras() != null  && getIntent().getExtras().containsKey("rideDetails")) {
            String intentData = getIntent().getExtras().getString("rideDetails");
            System.out.println("intent data -- "+ intentData);

            double current_lat = Double.parseDouble(pref.getData("latitude"));
            double current_long = Double.parseDouble(pref.getData("longitude"));

            double drop_lat = Double.parseDouble(pref.getData("drop_latitude"));
            double drop_long = Double.parseDouble(pref.getData("drop_longitude"));

            origin = new LatLng(current_lat, current_long);
            destination = new LatLng(drop_lat, drop_long);

            System.out.println("origin === "+ origin);
            System.out.println("destination === "+ destination);

           //EasyRouteCalculation easyRouteCalculation = new EasyRouteCalculation(this,mMap);
            //easyRouteCalculation.setLineColor(Color.RED);
            // For changing width
            // easyRouteCalculation.setLineWidth(7);
            // For changing travel mode
            //easyRouteCalculation.setTravelMode(TravelMode.DRIVING);
            //System.out.println("easy solution initialize" +origin+"---"+ destination);
            //easyRouteCalculation.calculateRouteBetweenTwoPoints(origin,istanbul);
           // mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(istanbul,15));


            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(origin, destination)
                    .key("AIzaSyCq164ffXtxj9n6p10eFM9aZSiLN5lKQzs")
                    .build();
            routing.execute();


           /* MarkerOptions options = new MarkerOptions();
            options.position(origin);
            options.position(istanbul);
            mMap.addMarker(options);

            String url = getMapsApiDirectionsUrl();
            System.out.println("url "+ url);
            ReadTask downloadTask = new ReadTask();
            downloadTask.execute(url);
*/
            mMap.moveCamera(CameraUpdateFactory.newLatLng(origin));
            //mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(origin,16));

            CameraPosition camPos = new CameraPosition.Builder()
                    .target(destination)
                    .zoom(18)
                    .tilt(30)
                    .build();
            CameraUpdate camUpd3 = CameraUpdateFactory.newCameraPosition(camPos);
            mMap.animateCamera(camUpd3);

            addMarkers();

        }
    }

    private String getMapsApiDirectionsUrl() {
        String waypoints = "waypoints=optimize:true|"
                + origin.latitude + "," + origin.longitude
                + "|" + "|" +  destination.latitude + ","
                + destination.longitude;
        String OriDest = "origin="+origin.latitude+","+origin.longitude+"&destination="+destination.latitude+","+destination.longitude;

        String sensor = "sensor=false";
        String key = "key=AIzaSyDKkOzrw9q9-zu0ro99H-0GywgIs-_N_lk";
        String params = OriDest+"&%20"+waypoints + "&" + sensor +"&"+ key;
        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/"
                + output + "?" + params;
        return url;
    }

    private void addMarkers() {
        if (mMap != null) {
            mMap.addMarker(new MarkerOptions().position(origin)
                    .title("First Point"));
            mMap.addMarker(new MarkerOptions().position(destination)
                    .title("Second Point"));
        }
    }

    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }
    }

    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;
            PolylineOptions polyLineOptions = null;

            System.out.println("routes  "+ routes);

            // traversing through routes
            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = routes.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                polyLineOptions.addAll(points);
                polyLineOptions.width(2);
                polyLineOptions.color(Color.BLUE);
            }

            mMap.addPolyline(polyLineOptions);
        }
    }

    //-----------------------------------
    private String getAddressFromCoordinates(Context mapsActivity, double latitude, double longitude)
    {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(this, Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5


            String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();

            return address;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    // for check google play service availablity-------------------------------
    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if(result != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
            return false;
        }
        return true;
    }

    //-----------onNavigationItemSelected--------------------
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.myAccount) {
            // Handle the camera action
            startActivity(new Intent(MapsActivity.this,MyAccountActivity.class));
        }
         else if (id == R.id.payment) {
            // Handle the camera action
            startActivity(new Intent(MapsActivity.this,PaymentActivity.class));
        } else if (id == R.id.trips) {
            startActivity(new Intent(MapsActivity.this,YourTripsActivity.class));

        } else if (id == R.id.settings) {
            startActivity(new Intent(MapsActivity.this,SettingActivity.class));

        } else if (id == R.id.help) {
            startActivity(new Intent(MapsActivity.this,HelpActivity.class));

        } else if (id == R.id.invite_friend) {
            shareTextUrl();
        } else if (id == R.id.logout) {
            onnBackPressed();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    // ----------------------------Method to share either text or URL.
    private void shareTextUrl() {
        Intent share = new Intent(android.content.Intent.ACTION_SEND);
        share.setType("text/plain");
        share.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);

        // Add data to the intent, the receiving app will decide
        // what to do with it.
        share.putExtra(Intent.EXTRA_SUBJECT, "TaxiApp for go anywhere");
        share.putExtra(Intent.EXTRA_TEXT, "http://www.google.com");

        startActivity(Intent.createChooser(share, "Share link!"));
    }

    //--------------------------on back button click
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else {
            if (back_pressed + 1000 > System.currentTimeMillis()){
                super.onBackPressed();
                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
            else{
                Toast.makeText(getBaseContext(),
                        R.string.exit_press_back_twice_message, Toast.LENGTH_SHORT)
                        .show();
            }
            back_pressed = System.currentTimeMillis();
        }
    }

    //--------------------------------confirming sure for back
    public void onnBackPressed() {
        AlertDialog.Builder alertdialog=new AlertDialog.Builder(this);
        alertdialog.setMessage("Are you sure you Want to logout?");
        alertdialog.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pref.setData("ldata","");
                startActivity(new Intent(MapsActivity.this,LoginActivity.class));
            }
        });

        alertdialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        AlertDialog alert=alertdialog.create();
        alertdialog.show();
    }
   //--------------------------------
    @Override
    protected void onResume() {
        super.onResume();
        // .... other stuff in my onResume ....
    }
    //-------...............GET CURRENT LOCATION
    private void getLocation() {

        if (mCurrentLocation != null) {

            try {
                currentlat = mCurrentLocation.getLatitude();
                currentlong = mCurrentLocation.getLongitude();
                System.out.println("Current Latitude@@@" + currentlat);
                System.out.println("Current Longitude@@@" + currentlong);

                pref.setData("latitude", String.valueOf(currentlat));
                pref.setData("longitude", String.valueOf(currentlong));

                LatLng sydney = new LatLng(currentlat, currentlong);
                //currentMarker = mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in current city"));
               // mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney,15));

                String address = getAddressFromCoordinates(MapsActivity.this,currentlat,currentlong);
                Log.i("address", address);
                TextView_start.setText(address);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    //---------------------------------------Requesting for runtime permission
    private void CheckForPermissionToUpdateLocation() {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            // do you work now
                            startLocationUpdates();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // permission is denied permenantly, navigate user to app settings
                            showSettingsDialog() ;

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }

    //-----------------------------------------------------------------------------------------
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.fromParts("package", getPackageName(), null)));
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }


    /**
     * Starting location updates
     * Check whether location settings are satisfied and then
     * location updates will be requested
     */
    private void startLocationUpdates() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener((Activity) this, new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

                        //Toast.makeText(context(), "Started location updates!", Toast.LENGTH_SHORT).show();

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());

                        getLocation();
                    }
                })
                .addOnFailureListener((Activity) this, new OnFailureListener() {
                    @SuppressLint("RestrictedApi")
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    // rae.startResolutionForResult(context(), 100);
                                    startIntentSenderForResult(rae.getResolution().getIntentSender(), 100, null, 0, 0, 0, null);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                GPSTracker gpsTracker = new GPSTracker(MapsActivity.this);
                                gpsTracker.showSettingsAlert();

                                Toast.makeText(MapsActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }

                        // updateLocationUI();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("onActivityResult()", Integer.toString(resultCode));

        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case 100:
                Log.e(TAG, "User agreed to make required location settings changes." + requestCode);
                switch (resultCode) {
                    case Activity.RESULT_OK:
                    {
                        Log.e(TAG, "User agreed to make required location settings changes." + requestCode);
                        Toast.makeText(this, "Started location updates!", Toast.LENGTH_SHORT).show();

                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        CheckForPermissionToUpdateLocation();
                        break;
                    }

                    case Activity.RESULT_CANCELED:
                    {
                        Log.e(TAG, "User chose not to make required location settings changes.");
                        CheckForPermissionToUpdateLocation();
                        break;
                    }
                    default:
                    {
                        break;
                    }

                }
                break;
        }
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        if(e != null) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            System.out.println("--"+e.getMessage());
        }else {
            Toast.makeText(this, "Something went wrong, Try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> arrayList, int i) {

    }

    @Override
    public void onRoutingCancelled() {

    }
}
