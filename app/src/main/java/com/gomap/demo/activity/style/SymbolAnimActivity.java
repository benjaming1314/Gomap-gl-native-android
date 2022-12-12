package com.gomap.demo.activity.style;

import static com.gomap.sdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.gomap.sdk.style.layers.PropertyFactory.iconImage;
import static com.gomap.sdk.style.layers.PropertyFactory.iconRotate;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.gomap.demo.R;
import com.gomap.geojson.Feature;
import com.gomap.geojson.Point;
import com.gomap.geojson.Polygon;
import com.gomap.sdk.annotation.Symbol;
import com.gomap.sdk.annotation.SymbolManager;
import com.gomap.sdk.annotation.SymbolOptions;
import com.gomap.sdk.camera.CameraPosition;
import com.gomap.sdk.geometry.LatLng;
import com.gomap.sdk.maps.MapView;
import com.gomap.sdk.maps.MapboxMap;
import com.gomap.sdk.maps.MapboxMapOptions;
import com.gomap.sdk.maps.OnMapReadyCallback;
import com.gomap.sdk.maps.Style;
import com.gomap.sdk.style.layers.SymbolLayer;
import com.gomap.sdk.style.sources.GeoJsonSource;
import com.gomap.sdk.utils.BitmapUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * @author dong.jin@g42.ai
 * @description
 * @createtime 2022/12/8
 */
public class SymbolAnimActivity extends AppCompatActivity implements OnMapReadyCallback {
    private MapboxMap mapboxMap;
    private MapView mapView;
    private static final String ID_ICON_AIRPORT = "airport";
    private SymbolManager symbolManager;
    private Symbol symbol;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_symbollayer);
        // Create map configuration
        MapboxMapOptions mapboxMapOptions = MapboxMapOptions.createFromAttributes(this);
        mapboxMapOptions.camera(new CameraPosition.Builder().target(
                        new LatLng(24.4628, 54.3697))
                .zoom(10)
                .build()
        );

        // Create map programmatically, add to view hierarchy
        mapView = new MapView(this, mapboxMapOptions);
        mapView.getMapAsync(this);
        mapView.onCreate(savedInstanceState);
        ((ViewGroup) findViewById(R.id.container)).addView(mapView);

    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(new Style.Builder().fromUri(Style.BASE_DEFAULT), new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                symbolManager = new SymbolManager(mapView, mapboxMap, style);
                symbolManager.setIconAllowOverlap(true);
                addAnimLayer(style);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_symbol_anim_layer, menu);
        return true;
    }

    private GeoJsonSource animGeoJsonSource;
    private SymbolLayer animLayer;

    private void addAnimLayer(Style style) {
        String layerId = "demo_layer_anim_id";
        String layerSource = "demo_layer_anim_source";
        style.addImage(ID_ICON_AIRPORT,
                BitmapUtils.getBitmapFromDrawable(getResources().getDrawable(R.drawable.ic_airplanemode_active_black_24dp)));
        animGeoJsonSource = new GeoJsonSource(layerSource, Point.fromLngLat(54.3667, 24.4628));
        style.addSource(animGeoJsonSource);
        animLayer = new SymbolLayer(layerId, layerSource).withProperties(
                iconImage(ID_ICON_AIRPORT),
                iconRotate(0f),
                iconAllowOverlap(true));
        style.addLayer(animLayer);

        // create a symbol
        SymbolOptions symbolOptions = new SymbolOptions()
                .withLatLng(new LatLng(24.4328, 54.3997))
                .withIconImage(ID_ICON_AIRPORT)
                .withIconSize(1.3f)
                .withSymbolSortKey(1.0f)
                .withDraggable(true);
        symbol = symbolManager.create(symbolOptions);
    }

    private void symbolLayerAnim() {
        final LatLng originalPosition = new LatLng(24.4628, 54.3667);
        final LatLng newPosition = new LatLng(24.4928, 54.3367);

        GeoJsonSource source = mapboxMap.getStyle().getSourceAs("demo_layer_anim_source");

        //查询获取source原有的数据
        List<Feature> list = source.querySourceFeatures(null);
        Point point = ((Point) list.get(0).geometry());
        Log.e("tttt", "old lat: " + point.latitude() + ",old lng = " + point.longitude());

        //解析json获取原有的数据
        try {
            JSONObject json = new JSONObject(source.getFeatureCollection());
            Log.e("tttt", "FeatureCollection: " + json);
            JSONArray array = json.getJSONArray("coordinates");
            for (int i = 0; i < array.length(); i += 2) {
                Log.e("tttt", "old lat: " + array.get(i + 1) + ",old lng:" + array.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ValueAnimator moveSymbol = ValueAnimator.ofFloat(0, 1).setDuration(5000);
        moveSymbol.setInterpolator(new LinearInterpolator());
        moveSymbol.addUpdateListener(animation -> {

            float fraction = (float) animation.getAnimatedValue();

            double lat = ((newPosition.getLatitude() - originalPosition.getLatitude()) * fraction) + originalPosition.getLatitude();
            double lng = ((newPosition.getLongitude() - originalPosition.getLongitude()) * fraction) + originalPosition.getLongitude();
            source.setGeoJson(Point.fromLngLat(lng, lat));
        });
        moveSymbol.start();
    }

    private void symbolManagerLayerAnim() {
        final LatLng newPosition = new LatLng(24.4928, 54.4367);
        final LatLng originalPosition = symbol.getLatLng();
        ValueAnimator moveSymbol = ValueAnimator.ofFloat(0, 1).setDuration(5000);
        moveSymbol.setInterpolator(new LinearInterpolator());
        moveSymbol.addUpdateListener(animation -> {
            if (symbolManager == null || symbolManager.getAnnotations().indexOfValue(symbol) < 0) {
                return;
            }
            float fraction = (float) animation.getAnimatedValue();

            double lat = ((newPosition.getLatitude() - originalPosition.getLatitude()) * fraction) + originalPosition.getLatitude();
            double lng = ((newPosition.getLongitude() - originalPosition.getLongitude()) * fraction) + originalPosition.getLongitude();
            symbol.setGeometry(Point.fromLngLat(lng, lat));
            symbolManager.update(symbol);
        });

        moveSymbol.start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_anim_symbollayer:
                symbolLayerAnim();
                return true;
            case R.id.action_anim_symbolmanager:
                symbolManagerLayerAnim();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
