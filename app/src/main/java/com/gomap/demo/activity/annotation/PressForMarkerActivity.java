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
import com.gomap.sdk.camera.CameraPosition;
import com.mapbox.geojson.Point;
import com.gomap.sdk.annotations.MarkerOptions;
import com.gomap.sdk.camera.CameraUpdateFactory;
import com.gomap.sdk.geometry.LatLng;
import com.gomap.sdk.location.LocationComponent;
import com.gomap.sdk.location.LocationComponentActivationOptions;
import com.gomap.sdk.location.engine.LocationEngineCallback;
import com.gomap.sdk.location.engine.LocationEngineResult;
import com.gomap.sdk.location.permissions.PermissionsListener;
import com.gomap.sdk.location.permissions.PermissionsManager;
import com.gomap.sdk.maps.MapView;
import com.gomap.sdk.maps.MapboxMap;
import com.gomap.sdk.maps.Style;
import com.gomap.sdk.route.DirectionService;
import com.gomap.sdk.route.DirectionServiceCallBack;
import com.gomap.sdk.route.model.DirectionsResponse;

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
public class PressForMarkerActivity extends AppCompatActivity {

  private MapView mapView;
  private MapboxMap mapboxMap;
  private ArrayList<MarkerOptions> markerList = new ArrayList<>();

  private static final DecimalFormat LAT_LON_FORMATTER = new DecimalFormat("#.#####");
  private final LatLng CENTER = new LatLng(24.4628,54.3697);

  private static String STATE_MARKER_LIST = "markerList";

  @Override
  protected void onCreate(@Nullable final Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_press_for_marker);
    mapView = (MapView) findViewById(R.id.mapView);

    mapView.onCreate(savedInstanceState);
    initMap();

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

      mapboxMap.setStyle(Style.getPredefinedStyle("Streets"));

      CameraPosition cameraPosition = new CameraPosition.Builder()
              .target(CENTER)
              .zoom(12)
              .tilt(30)
              .tilt(0)
              .build();
      mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

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


}
