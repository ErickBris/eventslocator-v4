package com.aleskovacic.eventslocator4.activities;

import android.Manifest;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.aleskovacic.eventslocator4.App;
import com.aleskovacic.eventslocator4.R;
import com.aleskovacic.eventslocator4.network.DataService;
import com.aleskovacic.eventslocator4.pojo.DataObject;
import com.aleskovacic.eventslocator4.pojo.DataResponse;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import java.util.ArrayList;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        View.OnClickListener, GoogleMap.OnMyLocationChangeListener {

    private static final LatLng mMapStartLocation = new LatLng(46.056947, 14.505751);
    private static final int PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 101;
    private boolean isInfoWindowOpen;
    private boolean isSubscribedToMapLocationUpdates;
    private GoogleMap mMap;
    private ClusterManager<DataObject> mClusterManager;
    private Cluster<DataObject> clickedCluster;
    private DataObject clickedClusterItem;
    private CoordinatorLayout mCoordinatorLayout;
    private AppBarLayout mAppBarLayout;
    private FloatingActionButton mFabMyLocation;
    private DataResponse mTemp;
    private Snackbar mSnackbar;

    private Retrofit retrofit;
    private DataService dataService;

    private Location myLocation;
    private SupportMapFragment mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);
        mAppBarLayout = (AppBarLayout) findViewById(R.id.app_bar_layout);
        mFabMyLocation = (FloatingActionButton) findViewById(R.id.btn_fab_my_location);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // top padding for appBarLayout
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                mAppBarLayout.setPadding(0, getResources().getDimensionPixelSize(resourceId), 0, 0);
            }
        }

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Today"));
        TabLayout.Tab defTab = tabLayout.newTab().setText("This week");
        tabLayout.addTab(defTab);
        defTab.select();
        tabLayout.addTab(tabLayout.newTab().setText("This month"));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int time = 7;
                switch (tab.getPosition()) {
                    case 0:
                        time = 1;
                        break;
                    case 1:
                        time = 7;
                        break;
                    case 2:
                        time = 30;
                }
                fetchEvents(time);

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        retrofit = new Retrofit.Builder()
                .baseUrl(App.SERVER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        dataService = retrofit.create(DataService.class);
        fetchEvents(7);

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION)) {
                permissionExplanation();
            } else {
                requestLocationPermission();
            }
        } else {
            mapFragment.getMapAsync(this);
        }
    }

    private void permissionExplanation() {
        Snackbar.make(findViewById(R.id.coordinator_layout),
                R.string.snackbar_permission_location_explanation, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.snackbar_permission_location_explanation_action,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                requestLocationPermission();
                            }
                        })
                .show();
    }

    private void requestLocationPermission() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (mapFragment != null) {
                        mapFragment.getMapAsync(this);
                    }
                } else {
                    permissionExplanation();
                }
            }
        }
    }

    private void fetchEvents(int time) {
        Call<DataResponse> m = dataService.getEvents(time);
        m.enqueue(new Callback<DataResponse>() {
            @Override
            public void onResponse(Response<DataResponse> response, Retrofit retrofit) {
                mTemp = response.body();
                addMarkers();

                if (mSnackbar != null && mSnackbar.isShown()) {
                    mSnackbar.dismiss();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                if (mSnackbar == null || !mSnackbar.isShown()) {
                    mSnackbar = Snackbar.make(mCoordinatorLayout,
                            R.string.snackbar_connection_failed, Snackbar.LENGTH_INDEFINITE);
                    mSnackbar.setAction(R.string.snackbar_connection_failed_action, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Settings.ACTION_SETTINGS));
                        }
                    })
                            .show();
                }
            }
        });
    }

    private void addMarkers() {
        if (mTemp != null && mMap != null && mClusterManager != null && mTemp.isSuccess()) {
            mClusterManager.clearItems();
            mClusterManager.addItems(mTemp.getData());
            mClusterManager.cluster();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                startActivity(new Intent(this, SearchActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMyLocationEnabled(true);
        mMap.setOnMyLocationChangeListener(this);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(false);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                mClusterManager.onCameraChange(cameraPosition);
                Location camLoc = new Location("");
                camLoc.setLatitude(cameraPosition.target.latitude);
                camLoc.setLongitude(cameraPosition.target.longitude);
                if (myLocation != null && myLocation.distanceTo(camLoc) > 1) {
                    isSubscribedToMapLocationUpdates = false;
                    mFabMyLocation.setImageDrawable(getResources().getDrawable(
                            R.drawable.ic_my_location_black_24dp));
                } else {
                    isSubscribedToMapLocationUpdates = true;
                }
            }
        });
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (!isInfoWindowOpen) {
                    ValueAnimator animation;
                    if (mAppBarLayout.getTranslationY() == 0.0) {
                        animation = ValueAnimator.ofFloat(0f, 1f);
                    } else {
                        animation = ValueAnimator.ofFloat(1f, 0f);
                    }

                    if (animation != null) {
                        animation.setDuration(200);
                        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                mAppBarLayout.setTranslationY(-mAppBarLayout.getBottom() *
                                        (float) animation.getAnimatedValue());
                                mFabMyLocation.setAlpha((1 - (float) animation.getAnimatedValue()));
                            }
                        });
                        animation.start();
                    }
                }
                isInfoWindowOpen = false;
            }
        });

        mClusterManager = new ClusterManager<>(this, mMap);
        mClusterManager.getClusterMarkerCollection().setOnInfoWindowAdapter(
                new MapClusterAdapter());
        mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(new MapItemAdapter());
        mClusterManager.getClusterMarkerCollection().setOnInfoWindowClickListener(
                new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        ArrayList<DataObject> list = new ArrayList<>();
                        list.addAll(clickedCluster.getItems());

                        Intent intent = new Intent(getApplicationContext(), ListActivity.class);
                        intent.putParcelableArrayListExtra(DataObject.EXTRA_LIST_ARRAY, list);
                        startActivity(intent);
                    }
                });
        mClusterManager.getMarkerCollection().setOnInfoWindowClickListener(
                new GoogleMap.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick(Marker marker) {
                        Intent intent = new Intent(getApplicationContext(), EventActivity.class);
                        intent.putExtra(DataObject.EXTRA_OBJECT, clickedClusterItem);
                        startActivity(intent);
                    }
                });
        mClusterManager.setOnClusterClickListener(
                new ClusterManager.OnClusterClickListener<DataObject>() {
                    @Override
                    public boolean onClusterClick(Cluster<DataObject> cluster) {
                        isInfoWindowOpen = true;
                        clickedCluster = cluster;
                        return false;
                    }
                });
        mClusterManager.setOnClusterItemClickListener(
                new ClusterManager.OnClusterItemClickListener<DataObject>() {
                    @Override
                    public boolean onClusterItemClick(DataObject item) {
                        isInfoWindowOpen = true;
                        clickedClusterItem = item;
                        return false;
                    }
                });

        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);
        mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mMapStartLocation, 6));
        addMarkers();
    }

    @Override
    public void onMyLocationChange(Location location) {
        myLocation = location;
        if (!mFabMyLocation.isShown()) {
            mFabMyLocation.setVisibility(View.VISIBLE);
        } else if (isSubscribedToMapLocationUpdates) {
            updateMapLocation(location);
        }
    }

    private void updateMapLocation(Location location) {
        CameraUpdate cm = CameraUpdateFactory.newLatLngZoom(new LatLng(myLocation.getLatitude(),
                myLocation.getLongitude()), 12);
        mMap.animateCamera(cm);
        Drawable drawable = getResources().getDrawable(R.drawable.ic_my_location_white_24dp);
        drawable.setColorFilter(getResources().getColor(R.color.primary), PorterDuff.Mode.MULTIPLY);
        mFabMyLocation.setImageDrawable(drawable);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_fab_my_location:
                updateMapLocation(myLocation);
                break;
        }
    }

    class MapItemAdapter implements GoogleMap.InfoWindowAdapter {
        private final View myContentsView;

        MapItemAdapter() {
            myContentsView = getLayoutInflater().inflate(
                    R.layout.map_item_info_window, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {

            ((TextView) myContentsView.findViewById(R.id.title))
                    .setText(clickedClusterItem.getTitle());
            ((TextView) myContentsView.findViewById(R.id.start_time))
                    .setText(clickedClusterItem.getStartDate());
            ((TextView) myContentsView.findViewById(R.id.place))
                    .setText(clickedClusterItem.getPlaceName());

            return myContentsView;
        }
    }

    class MapClusterAdapter implements GoogleMap.InfoWindowAdapter {
        private final View myContentsView;

        MapClusterAdapter() {
            myContentsView = getLayoutInflater().inflate(
                    R.layout.map_cluster_info_window, null);
        }

        @Override
        public View getInfoWindow(Marker marker) {
            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {

            if (clickedCluster != null) {
                for (DataObject item : clickedCluster.getItems()) {
                    ((TextView) myContentsView.findViewById(R.id.title))
                            .setText(item.getTitle());
                    ((TextView) myContentsView.findViewById(R.id.start_time))
                            .setText(item.getStartDate());
                    ((TextView) myContentsView.findViewById(R.id.place))
                            .setText(item.getPlaceName());
                    ((TextView) myContentsView.findViewById(R.id.no_event))
                            .setText(String.format(getResources()
                                            .getString(R.string.no_events),
                                    clickedCluster.getSize() - 1));
                    break;
                }
            }
            return myContentsView;
        }
    }
}
