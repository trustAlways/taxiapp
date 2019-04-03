package application.taxiapp.com.taxiapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import application.taxiapp.com.taxiapp.constant.GPSTracker;

import static android.support.constraint.Constraints.TAG;

public class HomePage extends AppCompatActivity {

    ViewPager viewPager;
    LinearLayout indicator_linear;
    private int dotscount;
    private ImageView[] dots;

    private long back_pressed;
    Button btn_sign_in, btn_sign_up;
    double currentlat,currentlong;

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
        setContentView(R.layout.activity_home_page);



        initialization();
        //for check runtime permission for access location.
        CheckForPermissionToUpdateLocation();
        click();

    }

    //all the view are visible here
    private void initialization() {


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


        btn_sign_in = (Button) findViewById(R.id.sign_in_button);
        btn_sign_up = (Button) findViewById(R.id.sign_up_button);


        viewPager = (ViewPager) findViewById(R.id.viewpager);
        indicator_linear = (LinearLayout) findViewById(R.id.linear_indicator);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);

        dotscount = viewPagerAdapter.getCount();
        dots = new ImageView[dotscount];

        for (int i = 0; i < dotscount; i++) {

            dots[i] = new ImageView(this);
            dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dots));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);

            params.gravity = Gravity.CENTER_HORIZONTAL;
            params.setMargins(8, 8, 8, 5);

            indicator_linear.addView(dots[i], params);
        }

        dots[0].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dots));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                for(int i = 0; i< dotscount; i++){
                    dots[i].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.non_active_dots));
                }

                dots[position].setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.active_dots));

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    //all the click events re work ther
    private void click()
    {
        btn_sign_in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomePage.this,LoginActivity.class));
            }
        });

        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomePage.this,SignupActivity.class));
            }
        });
    }

    private void getLocation() {

        if (mCurrentLocation!=null) {

            try {
                currentlat = mCurrentLocation.getLatitude();
                currentlong = mCurrentLocation.getLongitude();
                System.out.println("Current Latitude@@@" + currentlat);
                System.out.println("Current Longitude@@@" + currentlong);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }



    /*GPSTracker gps = new GPSTracker(this);
        if (gps.canGetLocation()) {
           double  currentlat = gps.getLatitude();
           double currentlong = gps.getLongitude();

            System.out.println("current LatLng : -- "+ gps.getLatitude() + "---" + currentlong);

           *//* geocoder = new Geocoder(context(), Locale.getDefault());
            try {
                address  = geocoder.getFromLocation(currentlat,currentlong,1);
                if (address != null && address.size() > 0) {
                    String addresss = address.get(0).getAddressLine(0);
                    String city = address.get(0).getLocality();
                    String country = address.get(0).getCountryName();

                    sessionManager.setData(SessionManager.KEY_USER_CURRENT_ADD, String.valueOf(address));

                    userlocation.setText(addresss + " " + city);
                    located_at.clearAnimation();
                }
                else
                {
                    Toast.makeText(context(), "No Address Found.", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }*//*


            //Toast.makeText(context, currentlat+""+currentlong, Toast.LENGTH_SHORT).show();


            //sessionManager.setData(SessionManager.KEY_LATITUDE, String.valueOf(currentlat));
            //sessionManager.setData(SessionManager.KEY_LONGITUDE, String.valueOf(currentlong));


        }
        else {
            gps.showSettingsAlert();
        }*/
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

    //-------------

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

                                GPSTracker gpsTracker = new GPSTracker(HomePage.this);
                                gpsTracker.showSettingsAlert();

                                Toast.makeText(HomePage.this, errorMessage, Toast.LENGTH_LONG).show();
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


    //back button click event...
    @Override
    public void onBackPressed() {
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

 //---------creating a view pager adapter class
 public  class ViewPagerAdapter extends PagerAdapter
 {
     private Context context;
     private LayoutInflater layoutInflater;
     private Integer [] images = {R.drawable.flag_india,R.drawable.flag_india,R.drawable.flag_india};

     public ViewPagerAdapter(Context context) {
         this.context = context;
     }

     @Override
     public int getCount() {
         return images.length;
     }

     @Override
     public boolean isViewFromObject(View view, Object object) {
         return view == object;
     }

     @Override
     public Object instantiateItem(ViewGroup container, final int position) {

         layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
         View view = layoutInflater.inflate(R.layout.viewpager_custom_layout, null);
         ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
         imageView.setImageResource(images[position]);


         ViewPager vp = (ViewPager) container;
         vp.addView(view, 0);
         return view;

     }

     @Override
     public void destroyItem(ViewGroup container, int position, Object object) {

         ViewPager vp = (ViewPager) container;
         View view = (View) object;
         vp.removeView(view);

     }
 }

}
