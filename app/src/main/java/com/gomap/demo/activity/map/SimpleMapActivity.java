package com.gomap.demo.activity.map;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.gomap.demo.R;
import com.gomap.sdk.camera.CameraPosition;
import com.gomap.sdk.camera.CameraUpdateFactory;
import com.gomap.sdk.geometry.LatLng;
import com.gomap.sdk.maps.MapView;
import com.gomap.sdk.maps.MapboxMap;
import com.gomap.sdk.maps.Style;
import com.gomap.sdk.tangram.LabelPickResult;

/**
 * Test activity showcasing a simple MapView without any MapboxMap interaction.
 */
public class SimpleMapActivity extends AppCompatActivity {

  private MapView mapView;

  private final LatLng CENTER = new LatLng(24.4628,54.3697);

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_map_simple);
    mapView = findViewById(R.id.mapView);
    mapView.onCreate(savedInstanceState);
    mapView.getMapAsync(mapboxMap -> {
      mapboxMap.setStyle(Style.BASE_DEFAULT, new Style.OnStyleLoaded() {
        @Override
        public void onStyleLoaded(@NonNull Style style) {

          CameraPosition cameraPosition = new CameraPosition.Builder()
                  .target(CENTER)
                  .zoom(12)
                  .tilt(30)
                  .tilt(0)
                  .build();
          mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

          mapboxMap.setOnLabelPickListener(new MapboxMap.OnLabelPickListener() {
            @Override
            public boolean onLabelPickComplete(@Nullable LabelPickResult result) {

              if (result != null){
                ToastUtils.showLong(result.toString());
              }
              LogUtils.i("gomap,test",result.toString());

              return false;
            }
          });
          //全景模式下 点的切换
          mapboxMap.setOnPanoramaClickListener(new MapboxMap.OnPanoramaClickListener() {
            @Override
            public boolean onPanoramaPointClick(@NonNull LatLng point) {

              if (point != null){
                ToastUtils.showLong(point.toString());
              }
              LogUtils.i("gomap,test",point.toString());
              return false;
            }
          });
        }
      });
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
