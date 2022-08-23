package com.gomap.demo.activity.annotation;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.PointF;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.gomap.demo.R;
import com.gomap.demo.model.HttpResponse;
import com.gomap.demo.model.MoreResponse;
import com.gomap.demo.model.PoiModel;
import com.gomap.demo.utils.ScreenUtils;
import com.gomap.geojson.Point;
import com.gomap.geojson.Polygon;
import com.gomap.sdk.annotations.Marker;
import com.gomap.sdk.annotations.MarkerOptions;
import com.gomap.sdk.camera.CameraPosition;
import com.gomap.sdk.camera.CameraUpdateFactory;
import com.gomap.sdk.geometry.LatLng;
import com.gomap.sdk.geometry.LatLngBounds;
import com.gomap.sdk.maps.MapView;
import com.gomap.sdk.maps.MapboxMap;
import com.gomap.sdk.maps.Style;
import com.gomap.sdk.poi.PoiService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class PoiListActivity extends AppCompatActivity {

    private MapView mapView;
    private MapboxMap mapboxMap;
    private ArrayList<MarkerOptions> markerList = new ArrayList<>();

    private static final DecimalFormat LAT_LON_FORMATTER = new DecimalFormat("#.#####");
    private final LatLng CENTER = new LatLng(24.4628,54.3697);

    private static String STATE_MARKER_LIST = "markerList";


    private View search ;

    @Override
    protected void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_poi_list);

        search = findViewById(R.id.txt_search_poi);
        mapView = (MapView) findViewById(R.id.mapView);

        mapView.onCreate(savedInstanceState);
        initMap();


        findViewById(R.id.txt_find_center).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LatLng center = findCenter();
                addMarker(center);

                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(center)
                        .zoom(12)
                        .tilt(30)
                        .tilt(0)
                        .build();
                mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LatLng center = findCenter();

                PoiService.getInstance().requestPoi(center,500,PoiListActivity.this,new PoiService.NetCallBack() {
                    @Override
                    public void onCallBack(String response) {
                        Type type = new TypeToken<HttpResponse<MoreResponse<PoiModel>> >() {
                        }.getType();
                        HttpResponse<MoreResponse<PoiModel>> httpResponse =  new Gson().fromJson(response,type);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mapboxMap.clear();
                                List<PoiModel> list = httpResponse.getData().getList();

                                if (list != null && !list.isEmpty()){
                                    ArrayList<Point> listPoint  = new ArrayList<Point>();
                                    Marker marker = null;
                                    for (PoiModel poiModel:
                                            list) {
                                        marker = addMarker(new LatLng(Double.parseDouble(poiModel.getLat()),Double.parseDouble(poiModel.getLng())));

                                        listPoint.add(Point.fromLngLat(Double.parseDouble(poiModel.getLng()),Double.parseDouble(poiModel.getLat())));
                                    }
//                                    if (marker != null){
//                                        mapboxMap.selectMarker(marker);
//                                    }
                                    // poi 显示在一定范围内
                                    List<List<Point>> polygonDefinition = new ArrayList<List<Point>>() {
                                        {
                                            add(listPoint);
                                        }
                                    };
                                    CameraPosition actualPosition = mapboxMap.getCameraForGeometry(Polygon.fromLngLats(polygonDefinition));

                                    mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(actualPosition));

                                }else {
                                    Toast.makeText(PoiListActivity.this,"no data",Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }
                });
            }
        });
    }

    private LatLng findCenter() {

        int height = ScreenUtils.getScreenHeight(PoiListActivity.this);
        int width = ScreenUtils.getScreenWidth(PoiListActivity.this);

        return mapboxMap.getProjection().fromScreenLocation(new PointF(width/2 ,height/2));

    }

    private void initMap(){
        mapView.getMapAsync(map -> {
            mapboxMap = map;
            resetMap();

            mapboxMap.setStyle(Style.getPredefinedStyle("Streets"));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(CENTER)
                    .zoom(12)
                    .tilt(30)
                    .tilt(0)
                    .build();
            mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        });
    }

    private Marker addMarker(LatLng point) {
        final PointF pixel = mapboxMap.getProjection().toScreenLocation(point);

        String title = LAT_LON_FORMATTER.format(point.getLatitude()) + ", "
                + LAT_LON_FORMATTER.format(point.getLongitude());
        String snippet = "X = " + (int) pixel.x + ", Y = " + (int) pixel.y;

        MarkerOptions marker = new MarkerOptions()
                .position(point)
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