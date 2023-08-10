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


  private static GomapApplication application ;

  @Override
  public void onCreate() {
    super.onCreate();
    application = this;
    initializeMap();
  }

  public static GomapApplication getInstance(){
    return application;
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

  private void initializeMap() {

    Mapbox.init(getApplicationContext());
    MapStrictMode.setStrictModeEnabled(true);
  }


}
