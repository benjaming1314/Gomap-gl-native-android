package com.gomap.demo.activity.annotation;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
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

        locations.add(new LatLng(38.898356214229885,-76.95071680042268));
        locations.add(new LatLng(38.96196109243133,-77.00388711500801));
        locations.add(new LatLng(38.92682257867399,-77.00697621720708));
        locations.add(new LatLng(38.87947513411545,-76.93706903318301));
        locations.add(new LatLng(38.87756015531194,-76.99833925795618));
        locations.add(new LatLng(38.920826177353554,-77.00344143825373));
        locations.add(new LatLng(38.94973736942091,-77.09691804556762));
        locations.add(new LatLng(38.88373832686895,-77.0323420800206));
        locations.add(new LatLng(38.95882519049787,-77.01701177841085));
        locations.add(new LatLng(38.89196180216161,-77.02376546437307));

        showGlMarkers();
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
                            PropertyFactory.lineWidth(10f),
                            PropertyFactory.lineColor(Color.BLUE),
                            PropertyFactory.iconImage("oneway"),
                            PropertyFactory.iconSize(15.0f),
                            PropertyFactory.symbolSpacing(5.0f),
                            PropertyFactory.iconOpacity(0.5f),
                            PropertyFactory.iconRotationAlignment(Property.ICON_ROTATION_ALIGNMENT_MAP),
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
                            PropertyFactory.iconRotationAlignment(Property.ICON_ROTATION_ALIGNMENT_MAP),
                            PropertyFactory.symbolPlacement(Property.SYMBOL_PLACEMENT_LINE)
                    )
            );

        });
//        Float[] dashArray ={4.0f,1.5f};
//        IconFactory iconFactory = IconFactory.getInstance(this);
//        Source testSource = mapboxMap.getStyle().getSource("openmaptiles");
//        List<Layer> tmpList = mapboxMap.getStyle().getLayers();
//        Layer testLayer = mapboxMap.getStyle().getLayer("road_motorway");
    }

}
