package com.gomap.demo.activity.poi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.PointF;
import android.location.Location;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.gomap.demo.R;
import com.gomap.demo.model.HttpResponse;
import com.gomap.demo.model.MoreResponse;
import com.gomap.demo.model.PoiModel;
import com.gomap.demo.utils.DeviceUtils;
import com.gomap.demo.utils.ScreenUtils;
import com.gomap.geojson.Point;
import com.gomap.geojson.Polygon;
import com.gomap.sdk.annotations.Marker;
import com.gomap.sdk.annotations.MarkerOptions;
import com.gomap.sdk.camera.CameraPosition;
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
import com.gomap.sdk.poi.PoiService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SearchPoiActivity extends AppCompatActivity {

    private PermissionsManager permissionsManager;

    private MapView mapView;
    private MapboxMap mapboxMap;
    private ArrayList<MarkerOptions> markerList = new ArrayList<>();

    private static String STATE_MARKER_LIST = "markerList";

    private EditText edtPoi;

    private Spinner spinner;

    private int searchType = 0;//0 是 keyword ,1 radius 2 type

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_poi);
        edtPoi = findViewById(R.id.edt_search_poi);

        spinner = findViewById(R.id.spinner);

        ArrayAdapter<String> adapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.search_type));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(1);

        mapView = (MapView) findViewById(R.id.mapView);

        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            mapView.onCreate(savedInstanceState);
            initMap();
        } else {
            permissionsManager = new PermissionsManager(new PermissionsListener() {

                @Override
                public void onExplanationNeeded(List<String> list) {
                }

                @Override
                public void onPermissionResult(boolean granted) {
                    if (granted) {
                        mapView.onCreate(savedInstanceState);
                        initMap();
                    } else {
                        finish();
                    }
                }
            });
            permissionsManager.requestLocationPermissions(this);
        }

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String[] stringArray = getResources().getStringArray(R.array.search_type);
                searchType = i ;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });


        findViewById(R.id.txt_search_poi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String keyword = edtPoi.getText().toString();

                if (keyword.trim().length() == 0){
                    Toast.makeText(SearchPoiActivity.this,"please input keyword",Toast.LENGTH_SHORT).show();
                    return;
                }

                Location location = mapboxMap.getLocationComponent().getLastKnownLocation();


                if (searchType == 0){
                    PoiService.getInstance().requestPoiAsInputKeyword(keyword,DeviceUtils.getAndroidId(SearchPoiActivity.this),new LatLng(location),new PoiService.NetCallBack(){

                        @Override
                        public void onCallBack(String response) {
                            handlePoiResult(response);
                        }
                    });
                }else if (searchType == 1){
                    PoiService.getInstance().requestPoiAsRadius(keyword,DeviceUtils.getAndroidId(SearchPoiActivity.this),new LatLng(location),findCenter(),15000,new PoiService.NetCallBack(){

                        @Override
                        public void onCallBack(String response) {
                            handlePoiResult(response);
                        }
                    });
                }else {
                    String[] fields = new String[]{"synonym","bank"};
                    PoiService.getInstance().requestPoiAsType(keyword,DeviceUtils.getAndroidId(SearchPoiActivity.this),new LatLng(location),fields,new PoiService.NetCallBack(){

                        @Override
                        public void onCallBack(String response) {
                            handlePoiResult(response);
                        }
                    });
                }

            }
        });

    }

    private LatLng findCenter() {
        int height = ScreenUtils.getScreenHeight(SearchPoiActivity.this);
        int width = ScreenUtils.getScreenWidth(SearchPoiActivity.this);
        return mapboxMap.getProjection().fromScreenLocation(new PointF(width / 2, height / 2));
    }

    private void handlePoiResult(String response) {
        Type type = new TypeToken<HttpResponse<MoreResponse<PoiModel>>>() {
        }.getType();
        HttpResponse<MoreResponse<PoiModel>> httpResponse = new Gson().fromJson(response, type);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mapboxMap.clear();
                List<PoiModel> list = httpResponse.getData().getList();

                if (list != null && !list.isEmpty()) {
                    ArrayList<Point> listPoint = new ArrayList<Point>();
                    Marker marker = null;
                    for (PoiModel poiModel :
                            list) {
                        String lat = poiModel.getLat();
                        if(lat != null && lat.length() > 0){
                            marker = addMarker(poiModel);

                            listPoint.add(Point.fromLngLat(Double.parseDouble(poiModel.getLng()), Double.parseDouble(poiModel.getLat())));
                        }

                    }
                    // poi 显示在一定范围内
                    List<List<Point>> polygonDefinition = new ArrayList<List<Point>>() {
                        {
                            add(listPoint);
                        }
                    };
                    CameraPosition actualPosition = mapboxMap.getCameraForGeometry(Polygon.fromLngLats(polygonDefinition));

                    mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(actualPosition));

                } else {
                    Toast.makeText(SearchPoiActivity.this, "no data", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @SuppressLint("MissingPermission")
    private void initMap() {
        mapView.getMapAsync(map -> {
            mapboxMap = map;
            resetMap();

            mapboxMap.setStyle(Style.getPredefinedStyle("Streets"), new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull @NotNull Style style) {

                    LocationComponent component = mapboxMap.getLocationComponent();
                    component.activateLocationComponent(
                            LocationComponentActivationOptions
                                    .builder(SearchPoiActivity.this, style)
                                    .useDefaultLocationEngine(true)
                                    .build()
                    );
                    component.setLocationComponentEnabled(true);
                    component.getLocationEngine().getLastLocation(new LocationEngineCallback<LocationEngineResult>() {
                        @SuppressLint("MissingPermission")
                        @Override
                        public void onSuccess(LocationEngineResult result) {

                            if (!mapView.isDestroyed()) mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(result.getLastLocation()), 12.0));
                        }

                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {

                        }
                    });
                }
            });
        });
    }

    private Marker addMarker(PoiModel poiModel) {

        String title = poiModel.getName();
        String snippet = poiModel.getAddress();

        MarkerOptions marker = new MarkerOptions()
                .position(new LatLng(Double.parseDouble(poiModel.getLat()),Double.parseDouble(poiModel.getLng())))
                .title(title)
                .snippet(snippet);

        markerList.add(marker);
        return mapboxMap.addMarker(marker);


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