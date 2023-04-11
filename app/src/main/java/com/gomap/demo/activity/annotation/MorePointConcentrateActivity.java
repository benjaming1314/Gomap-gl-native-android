package com.gomap.demo.activity.annotation;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.gomap.demo.R;
import com.gomap.sdk.annotation.OnSymbolClickListener;
import com.gomap.sdk.annotation.Symbol;
import com.gomap.sdk.annotation.SymbolManager;
import com.gomap.sdk.annotation.SymbolOptions;
import com.gomap.sdk.camera.CameraPosition;
import com.gomap.sdk.camera.CameraUpdateFactory;
import com.gomap.sdk.geometry.LatLng;
import com.gomap.sdk.maps.MapView;
import com.gomap.sdk.maps.Style;
import com.gomap.sdk.utils.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

public class MorePointConcentrateActivity extends AppCompatActivity {
    private final LatLng CENTER = new LatLng(24.4628,54.3697);

    private MapView mapView;
    private List<LatLng> locations = new ArrayList<>();

    private static final String ID_ICON_AIRPORT = "airport";

    private SymbolManager symbolManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_point_concentrate);
        initData();
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(mapboxMap -> {
            mapboxMap.setStyle(new Style.Builder()
                            .fromUri(Style.BASE_DEFAULT)
                            .withImage(ID_ICON_AIRPORT,
                                    BitmapUtils.getBitmapFromDrawable(getResources().getDrawable(R.drawable.ic_airplanemode_active_black_24dp)),
                                    true)
                    , new Style.OnStyleLoaded() {
                        @Override
                        public void onStyleLoaded(@NonNull Style style) {
                            CameraPosition cameraPosition = new CameraPosition.Builder()
                                    .target(CENTER)
                                    .zoom(10)
                                    .tilt(30)
                                    .tilt(0)
                                    .build();
                            mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                            //添加point
                            symbolManager = new SymbolManager(mapView, mapboxMap, style);
                            symbolManager.setIconAllowOverlap(true);
                            symbolManager.setTextAllowOverlap(true);

                            for (int i = 0; i < locations.size(); i++) {
                                LatLng latLng = locations.get(i);
                                // Create Symbol
                                SymbolOptions SymbolOptions = new SymbolOptions()
                                        .withLatLng(new LatLng(latLng.getLatitude(), latLng.getLongitude()))
                                        .withTextField("Test Data ：" + i)
                                        .withIconImage(ID_ICON_AIRPORT);
                                symbolManager.create(SymbolOptions);
                            }

                            symbolManager.addClickListener(new OnSymbolClickListener() {
                                @Override
                                public boolean onAnnotationClick(Symbol symbol) {
                                    Toast.makeText(MorePointConcentrateActivity.this, "Click:" + symbol.getTextField(), Toast.LENGTH_SHORT).show();
                                    return false;
                                }
                            });

                        }
                    });

        });


    }

    private void initData(){
        locations.add(new LatLng( 24.4628,54.3697));
        locations.add(new LatLng( 23.4328,54.3597));
        locations.add(new LatLng( 23.4428,54.3897));
        locations.add(new LatLng( 23.4028,54.3197));
        locations.add(new LatLng( 23.4228,54.3297));
        locations.add(new LatLng( 23.4128,54.3397));
        locations.add(new LatLng( 23.4528,54.3497));
        locations.add(new LatLng( 23.4728,54.3297));
        locations.add(new LatLng( 23.4828,54.3797));
        locations.add(new LatLng( 23.4928,54.3297));
        locations.add(new LatLng( 23.5028,54.3397));
        locations.add(new LatLng( 23.5228,54.3397));
        locations.add(new LatLng( 23.5428,54.3297));
        locations.add(new LatLng( 23.5328,54.3397));
        locations.add(new LatLng( 23.5528,54.3597));
        locations.add(new LatLng( 23.5628,54.3097));
        locations.add(new LatLng( 23.5728,54.3297));
    }

}