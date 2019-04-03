package application.taxiapp.com.taxiapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import application.taxiapp.com.taxiapp.constant.MySharedPref;

public class RideLocationActivity extends FragmentActivity implements OnMapReadyCallback {

    ImageView img_back,img_cross;
    Context context;
    MySharedPref pref;
    TextView txt_view_whereTo,getTxt_view_Pickup,txt_done;
    private Marker currentMarker = null;
    View mapView;
    private GoogleMap mMap;
    private UiSettings mUiSettings;
    private CameraUpdate mapCenter, mapZoom;

    int count = 0;

    double currentlat, currentlong;

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
        setContentView(R.layout.activity_ride_location);

        getBundleData();
        initialize();
        //for check runtime permission for access location.
        //CheckForPermissionToUpdateLocation();

        clickEvents();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);
    }

    //--------------------------------------------------
    private void getBundleData() {
        getTxt_view_Pickup = (TextView) findViewById(R.id.txtview_pickup_location);

        pref = new MySharedPref(this);
        currentlat = Double.parseDouble(pref.getData("latitude"));
        currentlong = Double.parseDouble(pref.getData("longitude"));

        String address = getAddressFromCoordinates(RideLocationActivity.this,currentlat,currentlong);
        getTxt_view_Pickup.setText(address);

        System.out.println("current lat : - " + currentlat + currentlong);
       // Log.i("address", address);

    }

    //=----------------------------------------------------------------------------------------------
    private void initialize() {
        context = this;
        img_back = (ImageView) findViewById(R.id.instant_ride_back);
        img_cross = (ImageView) findViewById(R.id.cross);

        txt_view_whereTo = (TextView) findViewById(R.id.txtview_where_to);
        txt_done = (TextView) findViewById(R.id.done);

    }

    ///--------All click events
    private void clickEvents() {
        img_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        img_cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txt_view_whereTo.setText("");
            }
        });

        txt_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!txt_view_whereTo.getText().toString().equalsIgnoreCase(""));
                {
                    Intent intent =  new Intent(RideLocationActivity.this,MapsActivity.class);
                    intent.putExtra("rideDetails","1");
                    startActivity(intent);
                }
            }
        });

    }

    //---------


    //-------------------------------------------
    // for check google play service availablity
    private boolean CheckGooglePlayServices() {
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int result = googleAPI.isGooglePlayServicesAvailable(this);
        if (result != ConnectionResult.SUCCESS) {
            if (googleAPI.isUserResolvableError(result)) {
                googleAPI.getErrorDialog(this, result,
                        0).show();
            }
            return false;
        }
        return true;
    }

    //-------------------------------------------------------------------------------------------
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mUiSettings = mMap.getUiSettings();
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);

        /*Set center of Tehran location*/
        LatLng latLng = new LatLng(currentlat, currentlong);
       // mMap.addMarker(new MarkerOptions().position(latLng));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18.0f));

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


            mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition cameraPosition) {
                    Log.i("centerLat", String.valueOf(cameraPosition.target.latitude));
                    Log.i("centerLong", String.valueOf(cameraPosition.target.longitude));
                    if (count!=0) {
                        String address = getAddressFromCoordinates(RideLocationActivity.this, cameraPosition.target.latitude, cameraPosition.target.longitude);
                        Log.i("address", address);
                        txt_view_whereTo.setText(address);

                        pref.setData("drop_latitude", String.valueOf(cameraPosition.target.latitude));
                        pref.setData("drop_longitude", String.valueOf(cameraPosition.target.longitude));
                        pref.setData("drop_Address",address);

                    }

                    count = 1;
                }
            });



       /* mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                System.out.println("draging start");

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                System.out.println("latitude ---  "+marker.getPosition().latitude);
                System.out.println("longitude ---  "+marker.getPosition().longitude);
                //mMap.addMarker(new MarkerOptions().position(marker.getPosition()).icon(BitmapDescriptorFactory.fromResource(R.drawable.map)).draggable(true));
               // mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
            }
        });*/
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


}
