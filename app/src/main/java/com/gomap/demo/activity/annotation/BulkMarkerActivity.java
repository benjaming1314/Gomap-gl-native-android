package com.gomap.demo.activity.annotation;

import static com.gomap.sdk.style.layers.Property.ICON_ROTATION_ALIGNMENT_MAP;
import static com.gomap.sdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.gomap.sdk.style.layers.PropertyFactory.iconImage;
import static com.gomap.sdk.style.layers.PropertyFactory.iconRotationAlignment;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.gomap.demo.R;
import com.gomap.geojson.Feature;
import com.gomap.geojson.FeatureCollection;
import com.gomap.geojson.LineString;
import com.gomap.geojson.Point;
import com.gomap.sdk.geometry.LatLng;
import com.gomap.sdk.maps.MapView;
import com.gomap.sdk.maps.MapboxMap;
import com.gomap.sdk.maps.Style;
import com.gomap.sdk.style.layers.LineLayer;
import com.gomap.sdk.style.layers.Property;
import com.gomap.sdk.style.layers.PropertyFactory;
import com.gomap.sdk.style.layers.SymbolLayer;
import com.gomap.sdk.style.sources.GeoJsonSource;
import com.gomap.sdk.utils.BitmapUtils;
import com.gomap.turf.TurfMeasurement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author dong.jin@g42.ai
 * @description
 * @createtime 2022/10/25
 */
public class BulkMarkerActivity extends AppCompatActivity {

    private MapboxMap mapboxMap;
    private MapView mapView;
    private List<LatLng> locations = new ArrayList<>();
    private Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_bulk);

        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this::initMap);
    }

    private void initMap(MapboxMap mapboxMap) {
        this.mapboxMap =  mapboxMap;
        mapboxMap.setStyle(Style.getPredefinedStyle("Streets"));
        showLines();
    }

    private void showLines() {

        mapboxMap.clear();

        locations.add(new LatLng(38.1008356214229885,-76.30195071680042268));
        locations.add(new LatLng(38.10196196109243133,-76.31195071680042268));

        locations.add(new LatLng(38.10496196109243133,-76.31295071680042268));
        locations.add(new LatLng(38.10596196109243133,-76.31395071680042268));
        locations.add(new LatLng(38.10696196109243133,-76.31495071680042268));
        locations.add(new LatLng(38.10796196109243133,-76.31595071680042268));
        locations.add(new LatLng(38.10896196109243133,-76.31695071680042268));
        locations.add(new LatLng(38.10996196109243133,-76.31795071680042268));
        locations.add(new LatLng(38.11096196109243133,-76.31895071680042268));
        locations.add(new LatLng(38.11196196109243133,-76.31995071680042268));
        locations.add(new LatLng(38.11296196109243133,-76.32095071680042268));
        locations.add(new LatLng(38.11396196109243133,-76.32195071680042268));
        locations.add(new LatLng(38.11496196109243133,-76.32295071680042268));
        locations.add(new LatLng(38.11596196109243133,-76.32395071680042268));
        locations.add(new LatLng(38.11696196109243133,-76.32495071680042268));

        locations.add(new LatLng(38.11796196109243133,-76.33495071680042268));
        locations.add(new LatLng(38.11896196109243133,-76.34495071680042268));
        locations.add(new LatLng(38.11996196109243133,-76.35495071680042268));
        locations.add(new LatLng(38.120169619610924313,-76.36495071680042268));
        locations.add(new LatLng(38.12196196109243133,-76.37495071680042268));
        locations.add(new LatLng(38.12296196109243133,-76.38495071680042268));
        locations.add(new LatLng(38.12396196109243133,-76.39495071680042268));
        locations.add(new LatLng(38.12496196109243133,-76.40495071680042268));
        locations.add(new LatLng(38.12596196109243133,-76.41495071680042268));
        locations.add(new LatLng(38.12696196109243133,-76.42495071680042268));
        locations.add(new LatLng(38.12796196109243133,-76.43495071680042268));
        locations.add(new LatLng(38.12896196109243133,-76.4495071680042268));
        locations.add(new LatLng(38.12996196109243133,-76.45495071680042268));
        locations.add(new LatLng(38.13096196109243133,-76.46495071680042268));

        locations.add(new LatLng(38.13196196109243133,-76.47495071680042268));
        locations.add(new LatLng(38.13296196109243133,-76.48495071680042268));
        locations.add(new LatLng(38.13396196109243133,-76.49495071680042268));
        locations.add(new LatLng(38.134169619610924313,-76.50495071680042268));
        locations.add(new LatLng(38.13596196109243133,-76.5195071680042268));
        locations.add(new LatLng(38.13696196109243133,-76.52495071680042268));
        locations.add(new LatLng(38.13796196109243133,-76.53495071680042268));
        locations.add(new LatLng(38.13896196109243133,-76.54495071680042268));
        locations.add(new LatLng(38.13996196109243133,-76.55495071680042268));
        locations.add(new LatLng(38.14096196109243133,-76.56495071680042268));
        locations.add(new LatLng(38.14196196109243133,-76.57495071680042268));
        locations.add(new LatLng(38.14296196109243133,-76.5895071680042268));
        locations.add(new LatLng(38.14396196109243133,-76.59495071680042268));
        locations.add(new LatLng(38.14496196109243133,-76.60495071680042268));

        locations.add(new LatLng(38.14596196109243133,-76.61495071680042268));
        locations.add(new LatLng(38.14696196109243133,-76.62495071680042268));
        locations.add(new LatLng(38.14796196109243133,-76.63495071680042268));
        locations.add(new LatLng(38.148169619610924313,-76.64495071680042268));
        locations.add(new LatLng(38.14996196109243133,-76.6595071680042268));
        locations.add(new LatLng(38.15096196109243133,-76.66495071680042268));
        locations.add(new LatLng(38.15196196109243133,-76.67495071680042268));
        locations.add(new LatLng(38.15296196109243133,-76.68495071680042268));
        locations.add(new LatLng(38.15396196109243133,-76.69495071680042268));
        locations.add(new LatLng(38.15496196109243133,-76.70495071680042268));
        locations.add(new LatLng(38.15596196109243133,-76.71495071680042268));
        locations.add(new LatLng(38.15696196109243133,-76.7295071680042268));
        locations.add(new LatLng(38.15796196109243133,-76.73495071680042268));
        locations.add(new LatLng(38.15896196109243133,-76.74495071680042268));
        locations.add(new LatLng(38.15996196109243133,-76.75495071680042268));
        locations.add(new LatLng(38.16096196109243133,-76.76495071680042268));
        locations.add(new LatLng(38.16196196109243133,-76.77495071680042268));
        locations.add(new LatLng(38.162169619610924313,-76.78495071680042268));
        locations.add(new LatLng(38.16396196109243133,-76.7995071680042268));
        locations.add(new LatLng(38.16496196109243133,-76.80495071680042268));
        locations.add(new LatLng(38.16596196109243133,-76.81495071680042268));
        locations.add(new LatLng(38.16696196109243133,-76.82495071680042268));
        locations.add(new LatLng(38.16796196109243133,-76.83495071680042268));
        locations.add(new LatLng(38.16896196109243133,-76.84495071680042268));
        locations.add(new LatLng(38.16996196109243133,-76.85495071680042268));
        locations.add(new LatLng(38.17096196109243133,-76.8695071680042268));
        locations.add(new LatLng(38.17196196109243133,-76.8795071680042268));
        locations.add(new LatLng(38.17296196109243133,-76.880495071680042268));

        locations.add(new LatLng(38.30596196109243133,-76.61495071680042268));
        locations.add(new LatLng(38.31696196109243133,-76.62495071680042268));
        locations.add(new LatLng(38.32796196109243133,-76.63495071680042268));
        locations.add(new LatLng(38.338169619610924313,-76.64495071680042268));
        locations.add(new LatLng(38.34996196109243133,-76.6595071680042268));
        locations.add(new LatLng(38.35096196109243133,-76.66495071680042268));
        locations.add(new LatLng(38.36196196109243133,-76.67495071680042268));
        locations.add(new LatLng(38.37296196109243133,-76.68495071680042268));
        locations.add(new LatLng(38.38396196109243133,-76.69495071680042268));
        locations.add(new LatLng(38.39496196109243133,-76.70495071680042268));
        locations.add(new LatLng(38.40596196109243133,-76.71495071680042268));
        locations.add(new LatLng(38.41696196109243133,-76.7295071680042268));
        locations.add(new LatLng(38.42796196109243133,-76.73495071680042268));
        locations.add(new LatLng(38.43896196109243133,-76.74495071680042268));
        locations.add(new LatLng(38.44996196109243133,-76.75495071680042268));
        locations.add(new LatLng(38.45096196109243133,-76.76495071680042268));
        locations.add(new LatLng(38.46196196109243133,-76.77495071680042268));
        locations.add(new LatLng(38.472169619610924313,-76.78495071680042268));
        locations.add(new LatLng(38.4896196109243133,-76.7995071680042268));
        locations.add(new LatLng(38.49496196109243133,-76.80495071680042268));
        locations.add(new LatLng(38.50596196109243133,-76.81495071680042268));
        locations.add(new LatLng(38.51696196109243133,-76.82495071680042268));
        locations.add(new LatLng(38.52796196109243133,-76.83495071680042268));
        locations.add(new LatLng(38.53896196109243133,-76.84495071680042268));
        locations.add(new LatLng(38.54996196109243133,-76.85495071680042268));
        locations.add(new LatLng(38.56096196109243133,-76.8695071680042268));
        locations.add(new LatLng(38.58196196109243133,-76.8795071680042268));
        locations.add(new LatLng(38.60296196109243133,-76.880495071680042268));

        mapboxMap.moveCamera(locations.get(0),11f,500);

        showGlMarkers();

        showAirIcon();
    }

    private Point lastPoint;
    private void showAirIcon() {
        mapboxMap.getStyle(style -> {
            style.addImage("air_icon",
                    BitmapUtils.getBitmapFromDrawable(getResources().getDrawable(R.drawable.ic_airplanemode_active_black_24dp)));
            LatLng latlng = locations.remove(0);
            lastPoint = Point.fromLngLat(latlng.getLongitude(),latlng.getLatitude());
            GeoJsonSource geoJsonSource = new GeoJsonSource("air_icon_source",lastPoint);
            style.addSource(geoJsonSource);
            SymbolLayer symbolLayer = new SymbolLayer("bearing_icon_test","air_icon_source");
            symbolLayer.withProperties(
                    iconImage("air_icon"),
                    iconAllowOverlap(true),
                    iconRotationAlignment(ICON_ROTATION_ALIGNMENT_MAP));
            style.addLayer(symbolLayer);

            handler = new Handler(Looper.myLooper());
            handler.postDelayed(() -> {
                emitLocation();
            }, 1000);

        });
    }

    private synchronized void emitLocation() {
        if (locations.size() > 0){
            LatLng latlng = locations.remove(0);
            Point nextPoint = Point.fromLngLat(latlng.getLongitude(),latlng.getLatitude());
            double bearing = TurfMeasurement.bearing(lastPoint,nextPoint);
            mapboxMap.getStyle().getLayer("bearing_icon_test").setProperties(PropertyFactory.iconRotate((float) bearing));
            lastPoint = nextPoint;
            GeoJsonSource geoJsonSource = mapboxMap.getStyle().getSourceAs("air_icon_source");
            geoJsonSource.setGeoJson(nextPoint);
            handler.postDelayed(() -> {
                emitLocation();
            }, 1000);
        }
    }

    private void showGlMarkers() {

        List<Point> coordinateList = new ArrayList<>();
        for (int i = 0; i < locations.size(); i++) {
            LatLng latLng = locations.get(i);
            coordinateList.add(
                    Point.fromLngLat(
                            latLng.getLongitude(),
                            latLng.getLatitude()
                    )
            );
        }

        mapboxMap.getStyle(style -> {
            style.addSource(
                    new GeoJsonSource(
                            "route-line-source", FeatureCollection.fromFeatures(
                            Arrays.asList(
                                    Feature.fromGeometry(
                                            LineString.fromLngLats(coordinateList)
                                    )
                            )
                    )
                    )
            );

            style.addLayer(
                    new LineLayer("route-line-layer", "route-line-source").withProperties(
                            PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                            PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                            PropertyFactory.lineWidth(5f),
                            PropertyFactory.lineColor(Color.BLUE),
                            PropertyFactory.iconImage("oneway"),
                            PropertyFactory.iconSize(15.0f),
                            PropertyFactory.symbolSpacing(5.0f),
                            PropertyFactory.iconOpacity(0.5f),
                            iconRotationAlignment(ICON_ROTATION_ALIGNMENT_MAP),
                            PropertyFactory.symbolPlacement(Property.SYMBOL_PLACEMENT_LINE)
                    )
            );

            style.addLayer(
                    new LineLayer("route-line-layer2", "route-line-source").withProperties(
                            PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
                            PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
                            PropertyFactory.lineWidth(2f),
                            PropertyFactory.lineGapWidth(5f),
                            PropertyFactory.lineColor(Color.WHITE),
                            iconRotationAlignment(ICON_ROTATION_ALIGNMENT_MAP),
                            PropertyFactory.symbolPlacement(Property.SYMBOL_PLACEMENT_LINE)
                    )
            );


            style.addImage("icon_sample_test", BitmapFactory.decodeResource(getResources(),R.drawable.ic_up));

            style.addLayer(
                    new SymbolLayer("route-pt-layer", "route-line-source").withProperties(
                            PropertyFactory.iconSize(0.6f),//icon缩放比例
                            PropertyFactory.iconRotate(90.0f),
                            PropertyFactory.iconImage("icon_sample_test"),
                            PropertyFactory.symbolSpacing(10.0f),//间距
                            iconRotationAlignment(ICON_ROTATION_ALIGNMENT_MAP),
                            PropertyFactory.symbolPlacement(Property.SYMBOL_PLACEMENT_LINE)
                    )
            );

        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
