package com.gomap.demo.activity.route;

import android.graphics.PointF;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.gomap.demo.R;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.annotations.MarkerOptions;
import com.mapbox.mapboxsdk.camera.CameraPosition;
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.route.DirectionService;
import com.mapbox.mapboxsdk.route.DirectionServiceCallBack;
import com.mapbox.mapboxsdk.route.model.DirectionsResponse;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Test activity showcasing the Polyline annotations API.
 * <p>
 * Shows how to add and remove polylines.
 * </p>
 */
public class PolylineActivity extends AppCompatActivity {

  private static final String STATE_POLYLINE_OPTIONS = "polylineOptions";

  private ArrayList<LatLng> markerList = new ArrayList<>();

  /**
   * 54.354361579778214,24.46360532793986;
   * 54.373281211451086,24.474072448295033;
   * 54.383570835678114,24.462445651735095
   */
  private final LatLng START = new LatLng(24.46360532793986,54.354361579778214);
  private final LatLng END = new LatLng(24.474072448295033,54.373281211451086);
  private final LatLng CENTER = new LatLng(24.4628,54.3697);

  private static final DecimalFormat LAT_LON_FORMATTER = new DecimalFormat("#.#####");

  private MapView mapView;
  private MapboxMap mapboxMap;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_polyline);

    mapView = findViewById(R.id.mapView);
    mapView.onCreate(savedInstanceState);


    markerList.add(START);
    markerList.add(END);

    mapView.getMapAsync(mapboxMap -> {
      PolylineActivity.this.mapboxMap = mapboxMap;
      mapboxMap.setStyle(Style.getPredefinedStyle("Streets"));
      CameraPosition cameraPosition = new CameraPosition.Builder()
              .target(CENTER)
              .zoom(12)
              .tilt(30)
              .tilt(0)
              .build();
      mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
      for (LatLng marker:
           markerList) {
        addMarker(marker);
      }
    });

    findViewById(R.id.route).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        requestDirectionRoute();
      }
    });

    findViewById(R.id.clear_route).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        markerList.clear();
        mapboxMap.clearDrawLine();
      }
    });

  }

  private void requestDirectionRoute(){
    List<Point> pointList = new ArrayList<>();

    for (LatLng marker:
            markerList) {
      pointList.add(Point.fromLngLat(marker.getLongitude(),marker.getLatitude()));
    }

    DirectionService.getInstance().requestRouteDirection(pointList, new DirectionServiceCallBack() {
      @Override
      public void onCallBack(DirectionsResponse directionsResponse) {
        drawLine(directionsResponse);
      }
    });

  }
  //
  private void drawLine(DirectionsResponse directionsResponse){

    mapboxMap.drawRouteLine(directionsResponse.getRoutes().get(0).getLegs().get(0));

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

    mapboxMap.addMarker(marker);
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
    mapView.onDestroy();
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    mapView.onLowMemory();
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    return super.onCreateOptionsMenu(menu);
  }

}
