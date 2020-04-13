package com.example.covid;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final float DEFAULT_ZOOM = 5f;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;

    private static final String TAG = "MainActivity";
    private static final int ERROR_DIALOG_REQUEST = 9001;

    private Boolean mLocationPermissionsGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseDatabase database;
    private DatabaseReference ref;

    //Markers
    private Marker maharastra, delhi, up, kerela, userLoc;

    Bundle bundle;

    //LatLng
    LatLng Mum, Del, UP, Ker, userLatLng;

    //Variables
    private String name, address, contact, city, state, country, aadhaar, uid, userId;
    private String latitude, longitude;
    private double tempLat, tempLong;

    @Override
    public void onMapReady(GoogleMap googleMap) {
        //Toast.makeText(MainActivity.this,"Map is Ready",Toast.LENGTH_SHORT).show();
        mMap = googleMap;
        //uid = bundle.getString("userId");

        try{
            boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(MainActivity.this,R.raw.style_json));
            if(!success)
                Log.e("MapsActivityRaw","Style parsing failed");
        }catch (Resources.NotFoundException e){
            Log.e("MapsActivityRaw","Can't find style",e);
        }

        final DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference().child("Users").child("Non Suspects");
        final List<String> userList = new ArrayList<>();
        final List<String> latList = new ArrayList<>();
        final List<String> longList = new ArrayList<>();
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot ds: dataSnapshot.getChildren()){
                    NonSuspectUsers nonSuspectUsers = ds.getValue(NonSuspectUsers.class);
                    userList.add(nonSuspectUsers.getUserName());
                    latList.add(nonSuspectUsers.getLatitude());
                    longList.add(nonSuspectUsers.getLongitude());
                    Log.d("NAME OF USERS",""+nonSuspectUsers.getUserName());
                    Log.d("DATA SNAPSHOT",""+dataSnapshot.getChildrenCount());
                }

                for(int i=0;i<latList.size();i++){
                    tempLat = Double.parseDouble(latList.get(i));
                    tempLong = Double.parseDouble(longList.get(i));
                    userLatLng = new LatLng(tempLat, tempLong);
                    userLoc = mMap.addMarker(new MarkerOptions().position(userLatLng).title(userList.get(i)).icon(bitmapDescriptorFromVector(MainActivity.this,R.drawable.ic_person_pin_circle_black_24dp)));
                }
                Log.d("USERS LIST",""+userList);
                Log.d("USERS LIST",""+latList);
                Log.d("USERS LIST",""+longList);
                /*tempLat = Double.parseDouble(dataSnapshot.child("latitude").getValue(String.class));
                //Toast.makeText(MainActivity.this,""+lat,Toast.LENGTH_LONG).show();
                tempLong = Double.parseDouble(dataSnapshot.child("longitude").getValue(String.class));
                Log.d("LATITUDE",""+tempLat);
                Log.d("LONGITUDE",""+tempLong);
                userLatLng = new LatLng(tempLat, tempLong);
                userLoc = mMap.addMarker(new MarkerOptions().position(userLatLng).title("Divyam"));*/

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        reference1.addValueEventListener(postListener);

       // mMap.setOnMarkerClickListener(this);


        Mum = new LatLng(19.7515,75.7139);
        Del = new LatLng(28.7041, 77.1025);
        UP = new LatLng(26.8467, 80.9462);
        Ker = new LatLng(10.8505, 76.2711);

        //userLatLng = new LatLng(tempLat, tempLong);

        //userLoc = mMap.addMarker(new MarkerOptions().position(userLatLng).title("Divyam"));

        maharastra = mMap.addMarker(new MarkerOptions().position(Mum).title("Maharashtra").snippet("Total Cases:1142"+"\n"+"Recovered:125"+"\n"+"Deaths:97").icon(bitmapDescriptorFromVector(MainActivity.this,R.drawable.ic_flag_black_24dp)));
        delhi = mMap.addMarker(new MarkerOptions().position(Del).title("Delhi").snippet("Total Cases:860"+"\n"+"Recovered:25"+"\n"+"Deaths:13").icon(bitmapDescriptorFromVector(MainActivity.this,R.drawable.ic_flag_black_24dp)));
        up = mMap.addMarker(new MarkerOptions().position(UP).title("Uttar Pradesh").snippet("Total Cases:395"+"\n"+"Recovered:32"+"\n"+"Deaths:4").icon(bitmapDescriptorFromVector(MainActivity.this,R.drawable.ic_flag_black_24dp)));
        kerela = mMap.addMarker(new MarkerOptions().position(Ker).title("Kerela").snippet("Total Cases:259"+"\n"+"Recovered:96"+"\n"+"Deaths:2").icon(bitmapDescriptorFromVector(MainActivity.this,R.drawable.ic_flag_black_24dp)));

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                LinearLayout info = new LinearLayout(MainActivity.this);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(MainActivity.this);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(MainActivity.this);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });

        if(mLocationPermissionsGranted){
            getDeviceLocation();
            if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                return;
            }

            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);

            //updateDatabase();
        }
    }

    private void updateDatabase(String latitude, String longitude){
        if(bundle != null){
            name = bundle.getString("name");
            contact = bundle.getString("contact");
            address = bundle.getString("address");
            city = bundle.getString("city");
            state = bundle.getString("state");
            country = bundle.getString("country");
            aadhaar = bundle.getString("aadhaar");
            uid = bundle.getString("userId");


            NonSuspectUsers users = new NonSuspectUsers();
            users.setUserName(name);
            users.setContact(contact);
            users.setAddress(address);
            users.setCity(city);
            users.setState(state);
            users.setCountry(country);
            users.setAadhaar(aadhaar);
            Log.d("LATITUDE",""+latitude);
            Log.d("LONGITUDE",""+longitude);
            users.setLatitude(latitude);
            users.setLongitude(longitude);

            DatabaseReference myRef = ref.child(userId);

            myRef.setValue(users);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if(marker.equals(maharastra)){
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Mum,7f));
            //moveCamera(Mum,7f,"Total Cases: 1142");
            return true;
        }
        return false;
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MainActivity.this);
    }

    private void getDeviceLocation(){
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {
            if (mLocationPermissionsGranted) {
                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(Task task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: found location");
                            Location currentLocation = (Location) task.getResult();
                            LatLng lat = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                            Log.d("COORDINATES",""+lat);
                            latitude = String.valueOf(lat.latitude);
                            longitude = String.valueOf(lat.longitude);
                            updateDatabase(latitude,longitude);
                            //Toast.makeText(MainActivity.this,""+currentLocation,Toast.LENGTH_LONG).show();

                            try {
                                Geocoder geocoder = new Geocoder(MainActivity.this, Locale.getDefault());
                                List<Address> addressList = geocoder.getFromLocation(currentLocation.getLatitude(),currentLocation.getLongitude(),1);
                                //LatLng lat = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
                                //Log.d("COORDINATES",""+lat);
                                //latitude = String.valueOf(lat.latitude);
                                //longitude = String.valueOf(lat.longitude);
                               // Log.d("LATITUDE",""+latitude);
                               // Log.d("LONGITUDE",""+longitude);
                                if(addressList.isEmpty())
                                    Toast.makeText(MainActivity.this,"Waiting for location",Toast.LENGTH_SHORT).show();
                                else{
                                    if(addressList.size()>0){
                                        Log.d(TAG,addressList.get(0).getFeatureName()+" "+addressList.get(0).getLocality()+" "+addressList.get(0).getAdminArea()+" "+addressList.get(0).getCountryName());
                                        Log.d("USER ID",""+userId);
                                    }
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }


                            moveCamera(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),DEFAULT_ZOOM,"My Location");
                        } else {
                            Log.d(TAG, "onComplete: current location is null");
                            Toast.makeText(MainActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }

        } catch (SecurityException e) {
            Log.d(TAG, "getDeviceLocation: SecurityException: " + e.getMessage());
        }

    }
    private void moveCamera(LatLng latLng, float zoom, String title){
        Log.d(TAG,"moveCamera: moving the camera to: lat "+latLng.latitude+", lng: "+latLng.longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,zoom));
        if(!title.equals("My Location")) {
            MarkerOptions options = new MarkerOptions().position(latLng).title(title);
            mMap.addMarker(options);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bundle = getIntent().getExtras();
        userId = user.getUid();
        //Log.d("USER ID",""+userId);

        drawerLayout = findViewById(R.id.drawer);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.Open,R.string.Close);
        actionBarDrawerToggle.setDrawerIndicatorEnabled(true);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        database = FirebaseDatabase.getInstance();
        ref = database.getReference().child("Users").child("Non Suspects");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.map:
                        Toast.makeText(MainActivity.this,"Map",Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.signOut:
                        FirebaseAuth.getInstance().signOut();
                        Intent intent = new Intent(MainActivity.this, UserDetails.class);
                        startActivity(intent);

                }
                return true;
            }
        });

        getLocationPermission();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return actionBarDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }


    private void getLocationPermission(){
        Log.d(TAG,"getLocationPermission: getting location permissions");
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                mLocationPermissionsGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        else{
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG,"onRequestPermissionsResult called");
        mLocationPermissionsGranted = false;

        switch (requestCode){
            case LOCATION_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0){
                    for(int i =0;i<grantResults.length;i++){
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionsGranted = false;
                            Log.d(TAG,"onRequestPermissionsResult: permission failed");
                            return;
                        }
                    }
                    Log.d(TAG,"onRequestPermissionsResult: permission granted");
                    mLocationPermissionsGranted = true;
                    //Initialize our map
                    initMap();

                }
            }
        }
    }
}
