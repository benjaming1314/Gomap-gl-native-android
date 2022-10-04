package com.gomap.demo.activity.snapshot;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;

import com.gomap.demo.R;
import com.gomap.sdk.camera.CameraPosition;
import com.gomap.sdk.geometry.LatLng;
import com.gomap.sdk.maps.Style;
import com.gomap.sdk.snapshotter.MapSnapshot;
import com.gomap.sdk.snapshotter.MapSnapshotter;

import timber.log.Timber;

/**
 * Test activity showing how to use a the {@link MapSnapshotter} and overlay
 * {@link Bitmap}s on top.
 */
public class MapSnapshotterBitMapOverlayActivity extends AppCompatActivity
  implements MapSnapshotter.SnapshotReadyCallback {

  private MapSnapshotter mapSnapshotter;
  private MapSnapshot mapSnapshot;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_map_snapshotter_marker);

    final View container = findViewById(R.id.container);
    container.getViewTreeObserver()
      .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
          //noinspection deprecation
          container.getViewTreeObserver().removeGlobalOnLayoutListener(this);

          Timber.i("Starting snapshot");

          mapSnapshotter = new MapSnapshotter(
            getApplicationContext(),
            new MapSnapshotter
              .Options(Math.min(container.getMeasuredWidth(), 1024), Math.min(container.getMeasuredHeight(), 1024))
              .withStyleBuilder(new Style.Builder().fromJson(Style.getLocalStyleJson("Streets")))
              .withCameraPosition(new CameraPosition.Builder().target(new LatLng(24.44077, 54.41179)).zoom(15).build())
          );
          mapSnapshotter.start(MapSnapshotterBitMapOverlayActivity.this);
        }
      });
  }

  @Override
  protected void onStop() {
    super.onStop();
    mapSnapshotter.cancel();
  }

  @SuppressLint("ClickableViewAccessibility")
  @Override
  public void onSnapshotReady(MapSnapshot snapshot) {
    this.mapSnapshot = snapshot;
    Timber.i("Snapshot ready");
    ImageView imageView = (ImageView) findViewById(R.id.snapshot_image);
    Bitmap image = addMarker(snapshot);
    imageView.setImageBitmap(image);
    imageView.setOnTouchListener((v, event) -> {
      if (event.getAction() == MotionEvent.ACTION_DOWN) {
        LatLng latLng = snapshot.latLngForPixel(new PointF(event.getX(), event.getY()));
        Timber.e("Clicked LatLng is %s", latLng);
        return true;
      }
      return false;
    });
  }

  private Bitmap addMarker(MapSnapshot snapshot) {
    Canvas canvas = new Canvas(snapshot.getBitmap());
    Bitmap marker = BitmapFactory.decodeResource(getResources(), R.drawable.mapbox_marker_icon_default, null);
    // Dom toren
    PointF markerLocation = snapshot.pixelForLatLng(new LatLng(24.44077, 54.41179));
    canvas.drawBitmap(marker,
      /* Subtract half of the width so we center the bitmap correctly */
      markerLocation.x - marker.getWidth() / 2,
      /* Subtract half of the height so we align the bitmap bottom correctly */
      markerLocation.y - marker.getHeight() / 2,
      null
    );
    return snapshot.getBitmap();
  }

  @VisibleForTesting
  @Nullable
  public MapSnapshot getMapSnapshot() {
    return mapSnapshot;
  }

}
