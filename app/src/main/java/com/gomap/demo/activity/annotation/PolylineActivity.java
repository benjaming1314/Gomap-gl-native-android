package com.gomap.demo.activity.annotation;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.gomap.demo.R;
import com.gomap.sdk.annotations.Polyline;
import com.gomap.sdk.annotations.PolylineOptions;
import com.gomap.sdk.geometry.LatLng;
import com.gomap.sdk.maps.MapView;
import com.gomap.sdk.maps.MapboxMap;
import com.gomap.sdk.maps.Style;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Test activity showcasing the Polyline annotations API.
 * <p>
 * Shows how to add and remove polylines.
 * </p>
 */
public class PolylineActivity extends AppCompatActivity {

  private static final String STATE_POLYLINE_OPTIONS = "polylineOptions";

  private static final LatLng ANDORRA = new LatLng(42.505777, 1.52529);
  private static final LatLng LUXEMBOURG = new LatLng(49.815273, 6.129583);
  private static final LatLng MONACO = new LatLng(43.738418, 7.424616);
  private static final LatLng VATICAN_CITY = new LatLng(41.902916, 12.453389);
  private static final LatLng SAN_MARINO = new LatLng(43.942360, 12.457777);
  private static final LatLng LIECHTENSTEIN = new LatLng(47.166000, 9.555373);

  private static final float FULL_ALPHA = 1.0f;
  private static final float PARTIAL_ALPHA = 0.5f;
  private static final float NO_ALPHA = 0.0f;

  private List<Polyline> polylines;
  private ArrayList<PolylineOptions> polylineOptions = new ArrayList<>();
  private MapView mapView;
  private MapboxMap mapboxMap;

  private boolean fullAlpha = true;
  private boolean visible = true;
  private boolean width = true;
  private boolean color = true;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_polyline);

    if (savedInstanceState != null) {
      polylineOptions = savedInstanceState.getParcelableArrayList(STATE_POLYLINE_OPTIONS);
    } else {
      polylineOptions.addAll(getAllPolylines());
    }

    mapView = findViewById(R.id.mapView);
    mapView.onCreate(savedInstanceState);
    mapView.getMapAsync(mapboxMap -> {
      PolylineActivity.this.mapboxMap = mapboxMap;
      mapboxMap.setStyle(Style.BASE_DEFAULT);
      mapboxMap.setOnPolylineClickListener(new MapboxMap.OnPolylineClickListener() {
        @Override
        public boolean onPolylineClick(@NonNull Polyline polyline) {
          Toast.makeText(
                  PolylineActivity.this,
                  "You clicked on polyline with id = " + polyline.getId(),
                  Toast.LENGTH_SHORT
          ).show();
          return true;
        }
      });

      polylines = mapboxMap.addPolylines(polylineOptions);
    });

    View fab = findViewById(R.id.fab);
    if (fab != null) {
      fab.setOnClickListener(view -> {
        if (mapboxMap != null) {
          if (polylines != null && polylines.size() > 0) {
            if (polylines.size() == 1) {
              // test for removing annotation
              mapboxMap.removeAnnotation(polylines.get(0));
            } else {
              // test for removing annotations
              mapboxMap.removeAnnotations(polylines);
            }
          }
          polylineOptions.clear();
          polylineOptions.addAll(getRandomLine());
          polylines = mapboxMap.addPolylines(polylineOptions);

        }
      });
    }
  }

  private List<PolylineOptions> getAllPolylines() {
    List<PolylineOptions> options = new ArrayList<>();
    options.add(generatePolyline(ANDORRA, LUXEMBOURG, "#F44336"));
    options.add(generatePolyline(ANDORRA, MONACO, "#FF5722"));
    options.add(generatePolyline(MONACO, VATICAN_CITY, "#673AB7"));
    options.add(generatePolyline(VATICAN_CITY, SAN_MARINO, "#009688"));
    options.add(generatePolyline(SAN_MARINO, LIECHTENSTEIN, "#795548"));
    options.add(generatePolyline(LIECHTENSTEIN, LUXEMBOURG, "#3F51B5"));
    return options;
  }

  private PolylineOptions generatePolyline(LatLng start, LatLng end, String color) {
    PolylineOptions line = new PolylineOptions();
    line.add(start);
    line.add(end);
    line.color(Color.parseColor(color));
    return line;
  }

  public List<PolylineOptions> getRandomLine() {
    final List<PolylineOptions> randomLines = getAllPolylines();
    Collections.shuffle(randomLines);
    return new ArrayList<PolylineOptions>() {
      {
        add(randomLines.get(0));
      }
    };
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
    outState.putParcelableArrayList(STATE_POLYLINE_OPTIONS, polylineOptions);
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
