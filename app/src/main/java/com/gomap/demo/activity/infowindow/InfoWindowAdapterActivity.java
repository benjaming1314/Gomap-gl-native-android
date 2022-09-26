package com.gomap.demo.activity.infowindow;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.gomap.demo.R;
import com.gomap.demo.model.annotations.CityStateMarker;
import com.gomap.demo.model.annotations.CityStateMarkerOptions;
import com.gomap.demo.utils.IconUtils;
import com.gomap.sdk.annotations.Icon;
import com.gomap.sdk.annotations.Marker;
import com.gomap.sdk.geometry.LatLng;
import com.gomap.sdk.maps.MapView;
import com.gomap.sdk.maps.MapboxMap;
import com.gomap.sdk.maps.Style;

/**
 * Test activity showcasing using an InfoWindowAdapter to provide a custom InfoWindow content.
 */
public class InfoWindowAdapterActivity extends AppCompatActivity {

  private MapView mapView;
  private MapboxMap mapboxMap;

  private View view ;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_infowindow_adapter);

    mapView = findViewById(R.id.mapView);
    mapView.onCreate(savedInstanceState);
    mapView.getMapAsync(map -> {
      mapboxMap = map;
      map.setStyle(Style.getPredefinedStyle("Streets"), style -> {
        addMarkers();
        addCustomInfoWindowAdapter();

        mapboxMap.setOnInfoWindowClickListener(new MapboxMap.OnInfoWindowClickListener(){

          @Override
          public boolean onInfoWindowClick(String s, @NonNull Marker marker) {
            return false;
          }

          @Override
          public boolean onInfoWindowClick(String s, @NonNull LatLng latLng) {
            Log.i("lxm test,",s +" "+latLng.getLatitude() + " " +latLng.getLongitude());
            return false;
          }

        });
      });
    });

    findViewById(R.id.test).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        view = LayoutInflater.from(InfoWindowAdapterActivity.this).inflate(R.layout.activity_infowindow_adapter_test_latlng,mapView,false);
        mapboxMap.showInfoWindow(new LatLng(42.505777, 1.52529),view,"tag1");


        TextView textView = new TextView(InfoWindowAdapterActivity.this);
        textView.setText("tesxtdsad");
        mapboxMap.showInfoWindow(new LatLng(43.505777, 1.52529),textView,"tag2");
      }
    });
    findViewById(R.id.test1).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        TextView textView = view.findViewById(R.id.test_edit);
        textView.setText("sadsadsd");

        mapboxMap.updateInfoWindow("tag1");

      }
    });
    findViewById(R.id.test2).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {

        TextView textView = view.findViewById(R.id.test_edit);
        textView.setText("1312323");
        mapboxMap.updateInfoWindow("tag1",new LatLng(43.738418, 7.424616));
      }
    });
  }

  private void addMarkers() {
    mapboxMap.addMarker(generateCityStateMarker("Andorra", 42.505777, 1.52529, "#F44336"));
    mapboxMap.addMarker(generateCityStateMarker("Luxembourg", 49.815273, 6.129583, "#3F51B5"));
    mapboxMap.addMarker(generateCityStateMarker("Monaco", 43.738418, 7.424616, "#673AB7"));
    mapboxMap.addMarker(generateCityStateMarker("Vatican City", 41.902916, 12.453389, "#009688"));
    mapboxMap.addMarker(generateCityStateMarker("San Marino", 43.942360, 12.457777, "#795548"));
    mapboxMap.addMarker(generateCityStateMarker("Liechtenstein", 47.166000, 9.555373, "#FF5722"));
  }

  private CityStateMarkerOptions generateCityStateMarker(String title, double lat, double lng, String color) {
    CityStateMarkerOptions marker = new CityStateMarkerOptions();
    marker.title(title);
    marker.position(new LatLng(lat, lng));
    marker.infoWindowBackground(color);

    Icon icon = IconUtils.drawableToIcon(this, R.drawable.ic_location_city, Color.parseColor(color));
    marker.icon(icon);
    return marker;
  }

  private void addCustomInfoWindowAdapter() {
    mapboxMap.setInfoWindowAdapter(new MapboxMap.InfoWindowAdapter() {

      private int tenDp = (int) getResources().getDimension(R.dimen.attr_margin);

      @Override
      public View getInfoWindow(@NonNull Marker marker) {
        TextView textView = new TextView(InfoWindowAdapterActivity.this);
        textView.setText(marker.getTitle());
        textView.setTextColor(Color.WHITE);

        if (marker instanceof CityStateMarker) {
          CityStateMarker cityStateMarker = (CityStateMarker) marker;
          textView.setBackgroundColor(Color.parseColor(cityStateMarker.getInfoWindowBackgroundColor()));
        }

        textView.setPadding(tenDp, tenDp, tenDp, tenDp);
        return textView;
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
    mapView.onDestroy();
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    mapView.onLowMemory();
  }
}
