package com.gomap.demo;

import android.app.Application;
import android.os.StrictMode;

import com.gomap.sdk.MapStrictMode;
import com.gomap.sdk.Mapbox;
import com.gomap.sdk.WellKnownTileServer;


/**
 * Application class of the test application.
 * <p>
 * Initialises components as LeakCanary, Strictmode, Timber and Mapbox
 * </p>
 */
public class GomapApplication extends Application {

  public static final WellKnownTileServer TILE_SERVER = WellKnownTileServer.MapTiler;

  private static final String DEFAULT_API_KEY = "Z7E7B1Ba8vOFikFSFebS";
  @Override
  public void onCreate() {
    super.onCreate();

//    initializeStrictMode();
    initializeMapbox();
  }


  private void initializeStrictMode() {
    StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
      .detectDiskReads()
      .detectDiskWrites()
      .detectNetwork()
      .penaltyLog()
      .build());
    StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
      .detectLeakedSqlLiteObjects()
      .penaltyLog()
      .penaltyDeath()
      .build());
  }

  private void initializeMapbox() {

    Mapbox.getInstance(getApplicationContext(), DEFAULT_API_KEY, TILE_SERVER);
    MapStrictMode.setStrictModeEnabled(true);
  }


}
