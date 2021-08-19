package com.example.garagesale;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.garagesale.models.Product;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;


import static androidx.activity.result.contract.ActivityResultContracts.*;

public class HomeActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraMoveStartedListener {

    private FloatingActionButton mFabLocation;

    private GoogleMap mGoogleMap;
    private LocationRequest locationRequest;
    private FusedLocationProviderClient fusedLocationClient;
    private final FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();

    private final List<Product> mProductList = new ArrayList<>();

    //Harsh
    // permission checking when user request
    private final ActivityResultLauncher<String[]> requestPermissionLauncher = registerForActivityResult(new RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
        @Override
        public void onActivityResult(Map<String, Boolean> result) {
            for (Map.Entry<String, Boolean> entry : result.entrySet()) {
                if (entry.getKey().equals(permissionsForLocation[0])) {
                    if (!entry.getValue()) {
                        Toast.makeText(HomeActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                    } else {
                        checkGps();
                    }
                } else if (entry.getKey().equals(permissionsForLocation[1])) {
                    if (!entry.getValue()) {
                        Toast.makeText(HomeActivity.this, "Permission denied", Toast.LENGTH_SHORT).show();
                    } else {
                        checkGps();
                    }
                }
            }
        }
    });

    //Harsh
    // default lat lng
    private double currentLat = 0.0;
    private double currentLng = 0.0;

    //Harsh
    // require permission for getting location
    private final String[] permissionsForLocation = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};

    //Harsh
    // getting updated location if user request by location request
    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull @Nonnull LocationResult locationResult) {
            currentLng = locationResult.getLastLocation().getLongitude();
            currentLat = locationResult.getLastLocation().getLatitude();
            mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLat, currentLng), 11.0f));
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Harsh: setup map initially
        setupMap();

        //Harsh: getting data of user's last location if exists
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //Harsh: creating new location request
        createLocationRequest();
        //Harsh: Allow-Deny location permission popup
        requestPermissionLauncher.launch(permissionsForLocation);

        //Harsh:
        mFabLocation = findViewById(R.id.fab_location);
        BottomNavigationView navView = findViewById(R.id.nav_view);

        //Harsh:
        checkLocation();

        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_products:
                        startActivity(new Intent(HomeActivity.this, AllProductActivity.class));
                        break;
                    case R.id.navigation_upload:
                        if (FirebaseAuth.getInstance().getCurrentUser() != null && currentLat != 0.0 && currentLng != 0.0) {
                            Intent intent = new Intent(HomeActivity.this, AddProductActivity.class);
                            intent.putExtra("lat", currentLat);
                            intent.putExtra("lng", currentLng);
                            startActivity(intent);
                            finish();
                        } else if (currentLat == 0.0 && currentLng == 0.0) {
                            Toast.makeText(HomeActivity.this, "Unable find your current location", Toast.LENGTH_SHORT).show();
                            requestPermissionLauncher.launch(permissionsForLocation);
                        } else {
                            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                        }
                        break;

                    //Krina, Mehul & Falak individual modules
                    case R.id.navigation_settings:
                        checkSettings();
                        break;
                }
                return true;
            }
        });
    }

    //Harsh:
    private void setupMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    //Harsh:
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        mGoogleMap = googleMap;
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnCameraMoveStartedListener(this);
        //Harsh: For getting products
        getAllProduct();
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker) {
        for (int i = 0; i <= mProductList.size(); i++){
            if (mProductList.get(i).getProductKey().equals(marker.getTag())){
                Intent intent = new Intent(this, ProductDetailActivity.class);
                intent.putExtra("Product", mProductList.get(i));
                startActivity(intent);
                break;
            }
        }
        return true;
    }

    //Harsh:
    private void getAllProduct() {
        mProductList.clear();
        mGoogleMap.clear();
        FirebaseFirestore.getInstance().collection("Products").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {

                            int index = 0;

                            for (QueryDocumentSnapshot document : task.getResult()) {

                                Product products = document.toObject(Product.class);
                                mProductList.add(products);
                                mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(mProductList.get(index).getProductLat(), mProductList.get(index).getProductLng())).title(mProductList.get(index).getProductName()));

                                index++;

                            }
                        } else {
                            Toast.makeText(getBaseContext(), "Getting error while fetching product", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //Harsh: get initial & timelapse location requests every 5 seconds
    protected void createLocationRequest() {
        if (locationRequest == null) {
            locationRequest = LocationRequest.create();
        }
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    //Harsh:
    private void checkLocation() {
        mFabLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissionLauncher.launch(permissionsForLocation);
            }
        });
    }

    //Harsh:
    //Get all gps related links
    private void checkGps() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        SettingsClient client = LocationServices.getSettingsClient(HomeActivity.this);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());
        task.addOnSuccessListener(HomeActivity.this, new OnSuccessListener<LocationSettingsResponse>() {
            @SuppressLint("MissingPermission")
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                if (locationSettingsResponse.getLocationSettingsStates().isGpsPresent()) {
                    fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
                    mGoogleMap.setMyLocationEnabled(true);
                }
            }
        });

        task.addOnFailureListener(HomeActivity.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    try {
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(HomeActivity.this, 111);
                    } catch (IntentSender.SendIntentException sendEx) {
                        Log.e("locationSettingsExp: ", sendEx.getMessage());
                    }
                }
            }
        });
    }

    //Mehul
    private void checkSettings() {
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
            startActivity(new Intent(HomeActivity.this, SettingActivity.class));
        else {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        }

    }

    //Harsh: permission of different versions
    private boolean checkPermission(String permissionName) {
        if (Build.VERSION.SDK_INT >= 23) {
            return ContextCompat.checkSelfPermission(this, permissionName) == PackageManager.PERMISSION_GRANTED;
        } else {
            return PermissionChecker.checkSelfPermission(this, permissionName) == PermissionChecker.PERMISSION_GRANTED;
        }
    }

    //Harsh:
    @Override
    public void onResume() {
        super.onResume();
        if (checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION)) {
            if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
                //Update current location if available
                fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
            }
        }
    }

    //Harsh:
    @Override
    public void onPause() {
        super.onPause();
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }
    @Override
    public void onCameraMoveStarted(int i) {
        if (i == REASON_GESTURE){
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }
}