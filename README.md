# Gomap-gl-native-android

1. Open your project in Android Studio.
2. Create a new diretory named “libs” under the project
3. Copy the aar file to the “libs” diretory
4. add the code to your *module-level* `build.gradle` file.
    
    ```groovy
    android {
    	...
    	dependencies {
    		implementation fileTree(dir: 'libs',includes: ['*.aar'])
    	}
    }
    ```
    
5. Make sure that your project's `minSdkVersion` is at API 21 or higher.
    
    ```jsx
    android {
      ...
      defaultConfig {
          minSdkVersion 21
      }
    }
    ```
    
    
    # Add Map

- Open the application init map:

```jsx
Mapbox.init(getApplicationContext());
```

- Open the activity’s XML layout file and add the following:
    
    ```kotlin
    <com.gomap.sdk.maps.MapView
            android:id="@id/mapView"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_below="@id/toolbar"
            />
    ```
    
- Open the activity you’d like to add a map to and use the code below.
    
    ```kotlin
    mapView.getMapAsync(map -> {
    //initialization finish
          mapboxMap = map;
        });
    ```
    
- Map Lifecycle
    
    ```kotlin
    public class SimpleMapActivity extends AppCompatActivity {
    
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
    }
    ```
    
    ## **Add proguard-rules**

```groovy
-keepattributes Signature, *Annotation*, EnclosingMethod

# Reflection on classes from native code
-keep class com.google.gson.JsonArray { *; }
-keep class com.google.gson.JsonElement { *; }
-keep class com.google.gson.JsonObject { *; }
-keep class com.google.gson.JsonPrimitive { *; }
-dontnote com.google.gson.**

# dontnote for keeps the entry point x but not the descriptor class y
-dontnote com.gomap.sdk.maps.MapboxMap$OnFpsChangedListener
-dontnote com.gomap.sdk.style.layers.PropertyValue
-dontnote com.gomap.sdk.maps.MapboxMap
-dontnote com.gomap.sdk.maps.MapboxMapOptions
-dontnote com.gomap.sdk.log.LoggerDefinition
-dontnote com.gomap.sdk.location.engine.LocationEnginePriority

# config for okhttp 3.11.0, https://github.com/square/okhttp/pull/3354
-dontwarn javax.annotation.**
-dontnote okhttp3.internal.**
-dontwarn org.codehaus.**

-keep class com.gomap.geojson.** { *; }
-dontwarn com.google.auto.value.**

# config for additional notes
-dontnote org.robolectric.Robolectric
-dontnote libcore.io.Memory
-dontnote com.google.protobuf.**
-dontnote android.net.**
-dontnote org.apache.http.**

-dontwarn com.sun.xml.internal.ws.spi.db.*
```
    
