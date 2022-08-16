package com.gomap.demo.activity.annotation;

import android.annotation.SuppressLint;
import android.graphics.PointF;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.gomap.demo.R;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.engine.LocationEngineCallback;
import com.mapbox.mapboxsdk.location.engine.LocationEngineResult;
import com.mapbox.mapboxsdk.location.permissions.PermissionsListener;
import com.mapbox.mapboxsdk.location.permissions.PermissionsManager;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.route.DirectionService;
import com.mapbox.mapboxsdk.route.DirectionServiceCallBack;
import com.mapbox.mapboxsdk.route.model.DirectionsResponse;

import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Test activity showcasing to add a Marker on click.
 * <p>
 * Shows how to use a OnMapClickListener and a OnMapLongClickListener
 * </p>
 */
@RequiresApi(api = Build.VERSION_CODES.N)
public class PressForMarkerActivity extends AppCompatActivity implements LocationEngineCallback<LocationEngineResult> {

  private MapView mapView;
  private MapboxMap mapboxMap;
  private ArrayList<MarkerOptions> markerList = new ArrayList<>();

  private static final DecimalFormat LAT_LON_FORMATTER = new DecimalFormat("#.#####");

  private static String STATE_MARKER_LIST = "markerList";

  private  PermissionsManager permissionsManager;

  @Override
  protected void onCreate(@Nullable final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_press_for_marker);
    mapView = (MapView) findViewById(R.id.mapView);

    if (PermissionsManager.areLocationPermissionsGranted(this)) {
      mapView.onCreate(savedInstanceState);
      initMap();
    } else {
      permissionsManager = new PermissionsManager(new PermissionsListener(){
        @Override
        public void onExplanationNeeded(List<String> permissionsToExplain) {
          Toast.makeText(PressForMarkerActivity.this,"You need to accept location permissions.", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPermissionResult(boolean granted) {
          mapView.onCreate(savedInstanceState);
          initMap();
        }
      });
      permissionsManager.requestLocationPermissions(this);
    }


    findViewById(R.id.clear_marker).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        mapboxMap.clearDrawLine();

      }
    });

  }

  private void initMap(){
    mapView.getMapAsync(map -> {
      mapboxMap = map;
      resetMap();

      mapboxMap.setStyle(Style.getPredefinedStyle("Streets"), new Style.OnStyleLoaded() {
        @SuppressLint("MissingPermission")
        @Override
        public void onStyleLoaded(@NonNull @NotNull Style style) {
          LocationComponent component = mapboxMap.getLocationComponent() ;

          component.activateLocationComponent(
                  LocationComponentActivationOptions
                          .builder(PressForMarkerActivity.this, style)
                          .useDefaultLocationEngine(true)
                          .build()
          );

          component.setLocationComponentEnabled(true);
          component.getLocationEngine().getLastLocation(PressForMarkerActivity.this);
        }
      });

      mapboxMap.addOnMapLongClickListener(point -> {
        addMarker(point);
        return false;
      });

      mapboxMap.addOnMapClickListener(point -> {
        addMarker(point);
        return false;
      });

    });
  }

  private void addMarker(LatLng point) {
    final PointF pixel = mapboxMap.getProjection().toScreenLocation(point);

    String title = LAT_LON_FORMATTER.format(point.getLatitude()) + ", "
      + LAT_LON_FORMATTER.format(point.getLongitude());
    String snippet = "X = " + (int) pixel.x + ", Y = " + (int) pixel.y;

    MarkerOptions marker = new MarkerOptions()
      .position(point)
      .title(title)
      .snippet(snippet);

    markerList.add(marker);
    mapboxMap.addMarker(marker);
  }

  private void resetMap() {
    if (mapboxMap == null) {
      return;
    }
    markerList.clear();
    mapboxMap.removeAnnotations();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
//    getMenuInflater().inflate(R.menu.menu_press_for_marker, menu);
    return true;
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);

    mapView.onSaveInstanceState(outState);
    outState.putParcelableArrayList(STATE_MARKER_LIST, markerList);
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
  protected void onDestroy() {
    super.onDestroy();
    mapView.onDestroy();
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    mapView.onLowMemory();
  }

//  @SuppressLint("NonConstantResourceId")
//  @Override
//  public boolean onOptionsItemSelected(MenuItem item) {
//    switch (item.getItemId()) {
//      case R.id.menuItemReset:
//        resetMap();
//        return true;
//      default:
//        return super.onOptionsItemSelected(item);
//    }
//  }

  @Override
  public void onSuccess(LocationEngineResult result) {
    if (!mapView.isDestroyed()) mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(result.getLastLocation()), 12.0));
  }

  @Override
  public void onFailure(@NonNull @NotNull Exception exception) {

  }

}
