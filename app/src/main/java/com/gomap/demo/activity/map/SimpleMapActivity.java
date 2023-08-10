package com.gomap.demo.activity.map;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.gomap.demo.R;
import com.gomap.demo.utils.DeviceUtils;
import com.gomap.demo.utils.ScreenUtil;
import com.gomap.sdk.camera.CameraPosition;
import com.gomap.sdk.camera.CameraUpdateFactory;
import com.gomap.sdk.geometry.LatLng;
import com.gomap.sdk.location.LocationComponent;
import com.gomap.sdk.location.LocationComponentActivationOptions;
import com.gomap.sdk.maps.MapView;
import com.gomap.sdk.maps.Style;
import com.gomap.sdk.util.DefaultStyle;

/**
 * Test activity showcasing a simple MapView without any MapboxMap interaction.
 */
public class SimpleMapActivity extends AppCompatActivity {

  private final LatLng CENTER = new LatLng(24.4628, 54.3697);

  private MapView mapView;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_map_simple);
    mapView = findViewById(R.id.mapView);
    mapView.onCreate(savedInstanceState);

    if (!DeviceUtils.isLocServiceEnable(this)){
      Toast.makeText(
              this,
      "You need open mobile location service",
              Toast.LENGTH_SHORT
            ).show();
      finish();
    }
    mapView.getMapAsync(mapboxMap -> {
      mapboxMap.setStyle(Style.BASE_DEFAULT, new Style.OnStyleLoaded() {
        @Override
        public void onStyleLoaded(@NonNull Style style) {

          if (ActivityCompat.checkSelfPermission(SimpleMapActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(SimpleMapActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(
                    SimpleMapActivity.this,
            "You need to accept location permissions.",
                    Toast.LENGTH_SHORT
                    ).show();
            finish();
            return;
          }
          LocationComponent locationComponent = mapboxMap.getLocationComponent();
          locationComponent.activateLocationComponent(
                  LocationComponentActivationOptions
                          .builder(SimpleMapActivity.this, style)
                          .useDefaultLocationEngine(true)
                          .build()
          );
          locationComponent.setLocationComponentEnabled(true);
        }
      });

      CameraPosition cameraPosition = new CameraPosition.Builder()
              .target(CENTER)
              .zoom(10)
              .tilt(30)
              .tilt(0)
              .build();
      mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

      mapboxMap.getUiSettings().setLogoMargins(ScreenUtil.dp2px(20),ScreenUtil.dp2px(50),ScreenUtil.dp2px(50),ScreenUtil.dp2px(60));
      mapboxMap.getUiSettings().setMapScaleViewMargins(ScreenUtil.dp2px(20),ScreenUtil.dp2px(50),ScreenUtil.dp2px(50),ScreenUtil.dp2px(80));

      mapboxMap.getUiSettings().setCompassMargins(ScreenUtil.dp2px(20),ScreenUtil.dp2px(50),ScreenUtil.dp2px(10),ScreenUtil.dp2px(50));
      mapboxMap.getUiSettings().setChangeStyleViewMargins(ScreenUtil.dp2px(20),ScreenUtil.dp2px(50),ScreenUtil.dp2px(10),ScreenUtil.dp2px(120));
      mapboxMap.getUiSettings().setVoiceSwitchViewMargins(ScreenUtil.dp2px(20),ScreenUtil.dp2px(50),ScreenUtil.dp2px(10),ScreenUtil.dp2px(190));
      mapboxMap.getUiSettings().setFindMeViewMargins(ScreenUtil.dp2px(20),ScreenUtil.dp2px(50),ScreenUtil.dp2px(10),ScreenUtil.dp2px(60));

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
  public void onLowMemory() {
    super.onLowMemory();
    mapView.onLowMemory();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
    mapView.onDestroy();
  }

  @Override
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    mapView.onSaveInstanceState(outState);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case android.R.id.home:
        // activity uses singleInstance for testing purposes
        // code below provides a default navigation when using the app
        onBackPressed();
        return true;
    }
    return super.onOptionsItemSelected(item);
  }

  @Override
  public void onBackPressed() {
    // activity uses singleInstance for testing purposes
    // code below provides a default navigation when using the app
    finish();
  }
}
