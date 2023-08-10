package com.gomap.demo.activity.annotation;

import static com.gomap.sdk.style.layers.Property.ICON_ANCHOR_BOTTOM;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.gomap.demo.GomapApplication;
import com.gomap.demo.R;
import com.gomap.demo.animation.PuckPulsingAnimator;
import com.gomap.demo.utils.Utils;
import com.gomap.sdk.camera.CameraPosition;
import com.gomap.sdk.camera.CameraUpdateFactory;
import com.gomap.sdk.location.engine.LocationEngineCallback;
import com.gomap.sdk.location.engine.LocationEngineResult;
import com.gomap.geojson.Point;
import com.gomap.sdk.annotation.SymbolManager;
import com.gomap.sdk.annotation.SymbolOptions;
import com.gomap.sdk.geometry.LatLng;
import com.gomap.sdk.location.LocationComponent;
import com.gomap.sdk.location.LocationComponentActivationOptions;
import com.gomap.sdk.location.LocationIndicatorLayer;
import com.gomap.sdk.location.LocationPropertyFactory;
import com.gomap.sdk.location.modes.RenderMode;
import com.gomap.sdk.location.permissions.PermissionsListener;
import com.gomap.sdk.location.permissions.PermissionsManager;
import com.gomap.sdk.maps.MapView;
import com.gomap.sdk.maps.MapboxMap;
import com.gomap.sdk.maps.OnMapReadyCallback;
import com.gomap.sdk.maps.Style;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class LocationIndicatorLayerActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView mapView;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;

    private SymbolManager vehicleAnnotationManager;
    private PuckPulsingAnimator puckPulsingAnimator1;
    private PuckPulsingAnimator puckPulsingAnimator2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_layer_map_change);

        mapView = findViewById(R.id.mapView);
        FloatingActionButton stylesFab = findViewById(R.id.fabStyles);

        stylesFab.setOnClickListener(v -> {
            if (mapboxMap != null) {
                mapboxMap.setStyle(new Style.Builder().fromUri(Utils.INSTANCE.getNextStyle()));
            }
        });

        mapView.onCreate(savedInstanceState);

        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            mapView.getMapAsync(this);
        } else {
            permissionsManager = new PermissionsManager(new PermissionsListener() {
                @Override
                public void onExplanationNeeded(List<String> permissionsToExplain) {
                    Toast.makeText(LocationIndicatorLayerActivity.this, "You need to accept location permissions.",
                            Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPermissionResult(boolean granted) {
                    if (granted) {
                        mapView.getMapAsync(LocationIndicatorLayerActivity.this);
                    } else {
                        finish();
                    }
                }
            });
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        ringPoint();
        mapboxMap.setStyle(new Style.Builder().fromUri(Utils.INSTANCE.getNextStyle()),
                style -> {
                    activateLocationComponent(style);
                });
    }


    private void ringPoint() {

        mapView.addOnDidFinishLoadingStyleListener(new MapView.OnDidFinishLoadingStyleListener() {
            @Override
            public void onDidFinishLoadingStyle() {
                mapboxMap.getStyle(new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        initVehicleLayer(style);
                        initLocationIndicator(style);
                        setData(style);
                    }
                });
            }
        });

    }

    private void setData(Style style) {
        Point point1 = Point.fromLngLat(54.3397, 24.4628);
        Point point2 = Point.fromLngLat(54.3397, 24.4428);
        //添加数据
        vehicleAnnotationManager.create(new SymbolOptions()
                .withLatLng(new LatLng(point1.latitude(), point1.longitude()))
                .withIconImage("vehicle-image-0")
                .withIconAnchor(ICON_ANCHOR_BOTTOM)
        );
        LocationIndicatorLayer locationIndicatorLayer = (LocationIndicatorLayer) style.getLayer("layer-circle");

        locationIndicatorLayer.withProperties(
                LocationPropertyFactory.location(new Double[]{point1.latitude(),
                        point1.longitude(), 0d}));
        if (puckPulsingAnimator1 == null) {
            puckPulsingAnimator1 = new PuckPulsingAnimator(mapboxMap);
        }else {
            puckPulsingAnimator1.cancelRunning();
        }
        puckPulsingAnimator1.setLocationRenderer(locationIndicatorLayer);
        puckPulsingAnimator1.animateInfinite();

        LocationIndicatorLayer locationIndicatorLayer1 = (LocationIndicatorLayer) style.getLayer("layer-circle1");

        locationIndicatorLayer1.withProperties(
                LocationPropertyFactory.location(new Double[]{point2.latitude(),
                        point2.longitude(), 0d}));

        if (puckPulsingAnimator2 == null){
            puckPulsingAnimator2 = new PuckPulsingAnimator(mapboxMap);
        }else {
            puckPulsingAnimator2.cancelRunning();
        }
        puckPulsingAnimator2.setLocationRenderer(locationIndicatorLayer1);
        puckPulsingAnimator2.animateInfinite();

    }


    private void initLocationIndicator(Style style) {

        if (style.getLayer("layer-circle") == null) {
            LocationIndicatorLayer locationIndicatorLayer = new LocationIndicatorLayer("layer-circle");
            locationIndicatorLayer.withProperties(
                    LocationPropertyFactory.topImageSize(1.5f)
            );
            style.addLayerBelow(
                    locationIndicatorLayer, "vehicle-layer");

        }

        if (style.getLayer("layer-circle1") == null) {
            LocationIndicatorLayer locationIndicatorLayer2 = new LocationIndicatorLayer("layer-circle1");
            locationIndicatorLayer2.withProperties(
                    LocationPropertyFactory.topImageSize(1.5f)
            );
            style.addLayerBelow(
                    locationIndicatorLayer2, "vehicle-layer");

        }

    }

    private void initVehicleLayer(Style style) {
        if (style.getLayer("vehicle-layer") == null) {
            style.addImage("vehicle-image-0",
                    BitmapFactory.decodeResource(
                            GomapApplication.getInstance().getResources(),
                            R.mipmap.ic_map_car
                    )
            );
        }
        if (vehicleAnnotationManager == null) {
            vehicleAnnotationManager = new SymbolManager(mapView, mapboxMap, style, "site-layer");
            vehicleAnnotationManager.setIconAllowOverlap(true);
            vehicleAnnotationManager.setTextAllowOverlap(true);
        }
    }


    @SuppressLint("MissingPermission")
    private void activateLocationComponent(@NonNull Style style) {
        LocationComponent locationComponent = mapboxMap.getLocationComponent();

        locationComponent.activateLocationComponent(
                LocationComponentActivationOptions
                        .builder(this, style)
                        .useDefaultLocationEngine(true)
                        .build());

        locationComponent.setLocationComponentEnabled(true);
        locationComponent.setRenderMode(RenderMode.COMPASS);

        locationComponent.addOnLocationClickListener(
                () -> Toast.makeText(this, "Location clicked", Toast.LENGTH_SHORT).show());

        locationComponent.addOnLocationLongClickListener(
                () -> Toast.makeText(this, "Location long clicked", Toast.LENGTH_SHORT).show());


        locationComponent.getLocationEngine().getLastLocation(new LocationEngineCallback<LocationEngineResult>() {
            @Override
            public void onSuccess(LocationEngineResult result) {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .zoom(12)
                        .target(new LatLng(result.getLastLocation()))
                        .tilt(30)
                        .tilt(0)
                        .build();
                mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }

            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        puckPulsingAnimator1.cancelRunning();
        puckPulsingAnimator2.cancelRunning();
        mapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
}