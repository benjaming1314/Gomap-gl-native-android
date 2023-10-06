package com.gomap.demo.activity.style;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.gomap.android.gestures.AndroidGesturesManager;
import com.gomap.demo.R;
import com.gomap.sdk.annotation.OnSymbolClickListener;
import com.gomap.sdk.annotation.Symbol;
import com.gomap.sdk.annotation.SymbolManager;
import com.gomap.sdk.annotation.SymbolOptions;
import com.gomap.sdk.maps.UiSettings;
import com.gomap.sdk.utils.FontUtils;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.gomap.geojson.Feature;
import com.gomap.geojson.FeatureCollection;
import com.gomap.geojson.Point;
import com.gomap.sdk.camera.CameraPosition;
import com.gomap.sdk.geometry.LatLng;
import com.gomap.sdk.maps.MapView;
import com.gomap.sdk.maps.MapboxMap;
import com.gomap.sdk.maps.MapboxMapOptions;
import com.gomap.sdk.maps.OnMapReadyCallback;
import com.gomap.sdk.maps.Style;
import com.gomap.sdk.style.expressions.Expression;
import com.gomap.sdk.style.layers.Property;
import com.gomap.sdk.style.layers.SymbolLayer;
import com.gomap.sdk.style.sources.GeoJsonSource;
import com.gomap.sdk.style.sources.Source;
import com.gomap.sdk.utils.BitmapUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import timber.log.Timber;

import static com.gomap.sdk.style.expressions.Expression.FormatOption.formatFontScale;
import static com.gomap.sdk.style.expressions.Expression.FormatOption.formatTextColor;
import static com.gomap.sdk.style.expressions.Expression.FormatOption.formatTextFont;
import static com.gomap.sdk.style.expressions.Expression.NumberFormatOption.currency;
import static com.gomap.sdk.style.expressions.Expression.NumberFormatOption.locale;
import static com.gomap.sdk.style.expressions.Expression.concat;
import static com.gomap.sdk.style.expressions.Expression.eq;
import static com.gomap.sdk.style.expressions.Expression.format;
import static com.gomap.sdk.style.expressions.Expression.formatEntry;
import static com.gomap.sdk.style.expressions.Expression.get;
import static com.gomap.sdk.style.expressions.Expression.literal;
import static com.gomap.sdk.style.expressions.Expression.match;
import static com.gomap.sdk.style.expressions.Expression.numberFormat;
import static com.gomap.sdk.style.expressions.Expression.rgb;
import static com.gomap.sdk.style.expressions.Expression.stop;
import static com.gomap.sdk.style.expressions.Expression.switchCase;
import static com.gomap.sdk.style.expressions.Expression.toBool;
import static com.gomap.sdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.gomap.sdk.style.layers.PropertyFactory.iconAnchor;
import static com.gomap.sdk.style.layers.PropertyFactory.iconColor;
import static com.gomap.sdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.gomap.sdk.style.layers.PropertyFactory.iconImage;
import static com.gomap.sdk.style.layers.PropertyFactory.iconOpacity;
import static com.gomap.sdk.style.layers.PropertyFactory.iconSize;
import static com.gomap.sdk.style.layers.PropertyFactory.textAllowOverlap;
import static com.gomap.sdk.style.layers.PropertyFactory.textAnchor;
import static com.gomap.sdk.style.layers.PropertyFactory.textColor;
import static com.gomap.sdk.style.layers.PropertyFactory.textField;
import static com.gomap.sdk.style.layers.PropertyFactory.textFont;
import static com.gomap.sdk.style.layers.PropertyFactory.textIgnorePlacement;
import static com.gomap.sdk.style.layers.PropertyFactory.textRotationAlignment;
import static com.gomap.sdk.style.layers.PropertyFactory.textSize;

/**
 * Test activity showcasing runtime manipulation of symbol layers.
 * <p>
 * Showcases the ability to offline render a symbol layer by using a packaged style and fonts from the assets folder.
 * </p>
 */
public class SymbolLayerActivity extends AppCompatActivity implements MapboxMap.OnMapClickListener, OnMapReadyCallback {

    private static final String ID_FEATURE_PROPERTY = "id";
    private static final String SELECTED_FEATURE_PROPERTY = "selected";
    private static final String TITLE_FEATURE_PROPERTY = "title";

    private static final String[] ITALIC_FONT_STACK = FontUtils.ITALIC_FONT_STACK;
    private static final String[] NORMAL_FONT_STACK= FontUtils.NORMAL_FONT_STACK;

    // layer & source constants
    private static final String MARKER_SOURCE = "marker-source";
    private static final String MARKER_LAYER = "marker-layer";
    private static final String MAPBOX_SIGN_SOURCE = "mapbox-sign-source";
    private static final String MAPBOX_SIGN_LAYER = "mapbox-sign-layer";
    private static final String NUMBER_FORMAT_SOURCE = "mapbox-number-source";
    private static final String NUMBER_FORMAT_LAYER = "mapbox-number-layer";

    private SymbolManager symbolManager;

    private static final Expression TEXT_FIELD_EXPRESSION =
            switchCase(toBool(get(SELECTED_FEATURE_PROPERTY)),
                    format(
                            formatEntry(
                                    get(TITLE_FEATURE_PROPERTY),
                                    formatTextFont(ITALIC_FONT_STACK)
                            ),
                            formatEntry("\nis fun!", formatFontScale(0.75))
                    ),
                    format(
                            formatEntry("This is", formatFontScale(0.75)),
                            formatEntry(
                                    concat(literal("\n"), get(TITLE_FEATURE_PROPERTY)),
                                    formatFontScale(1.25),
                                    formatTextFont(ITALIC_FONT_STACK)
                            )
                    )
            );

    private final Random random = new Random();
    private GeoJsonSource markerSource;
    private FeatureCollection markerCollection;
    private SymbolLayer markerSymbolLayer;
    private SymbolLayer mapboxSignSymbolLayer;
    private SymbolLayer numberFormatSymbolLayer;
    private MapboxMap mapboxMap;
    private MapView mapView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_symbollayer);

        // Create map configuration
        MapboxMapOptions mapboxMapOptions = MapboxMapOptions.createFromAttributes(this);
        mapboxMapOptions.camera(new CameraPosition.Builder().target(
                        new LatLng(24.4628, 54.3697))
                .zoom(13)
                .build()
        );

        // Create map programmatically, add to view hierarchy
        mapView = new MapView(this, mapboxMapOptions);
        mapView.getMapAsync(this);
        mapView.onCreate(savedInstanceState);
        ((ViewGroup) findViewById(R.id.container)).addView(mapView);

        // Use OnStyleImageMissing API to lazily load an icon
        mapView.addOnStyleImageMissingListener(id -> {
            Style style = mapboxMap.getStyle();
            if (style != null) {
                Timber.e("Adding image with id: %s", id);
                Bitmap androidIcon = BitmapUtils.getBitmapFromDrawable(getResources().getDrawable(R.drawable.ic_android_2));
                style.addImage(id, Objects.requireNonNull(androidIcon));
            }
        });
    }

    @Override
    public void onMapReady(@NonNull MapboxMap mapboxMap) {
        this.mapboxMap = mapboxMap;
        Bitmap carBitmap = BitmapUtils.getBitmapFromDrawable(
                getResources().getDrawable(R.drawable.ic_directions_car_black));

        // marker source
        markerCollection = FeatureCollection.fromFeatures(new Feature[]{
                Feature.fromGeometry(Point.fromLngLat(54.3667, 24.4628), featureProperties("1", "Android")),
                Feature.fromGeometry(Point.fromLngLat(54.3697, 24.4658), featureProperties("2", "Car"))
        });
        markerSource = new GeoJsonSource(MARKER_SOURCE, markerCollection);

        // marker layer
        markerSymbolLayer = new SymbolLayer(MARKER_LAYER, MARKER_SOURCE)
                .withProperties(
                        iconImage("police_station_s"),
                        iconAllowOverlap(false),
                        iconSize(switchCase(toBool(get(SELECTED_FEATURE_PROPERTY)), literal(1.5f), literal(1.0f))),
                        iconAnchor(Property.ICON_ANCHOR_BOTTOM),
                        iconColor(Color.BLUE),
                        textField(TEXT_FIELD_EXPRESSION),
                        textFont(NORMAL_FONT_STACK),
                        textColor(Color.BLUE),
                        textAllowOverlap(false),
                        textAnchor(Property.TEXT_ANCHOR_TOP),
                        textSize(10f)
                );

        // Setup a filter to filter out the title(feature property) is not Android
        Expression expression = eq(get(TITLE_FEATURE_PROPERTY), "Android");
        markerSource = new GeoJsonSource(MARKER_SOURCE, markerCollection);
        markerSymbolLayer.setFilter(expression);

        // mapbox sign layer
        Source mapboxSignSource = new GeoJsonSource(MAPBOX_SIGN_SOURCE, Point.fromLngLat(54.3597, 24.4628));
        mapboxSignSymbolLayer = new SymbolLayer(MAPBOX_SIGN_LAYER, MAPBOX_SIGN_SOURCE).withProperties(
                iconImage(get(TITLE_FEATURE_PROPERTY)),
                iconAllowOverlap(false),
                iconSize(switchCase(toBool(get(SELECTED_FEATURE_PROPERTY)), literal(1.5f), literal(1.0f))),
                iconAnchor(Property.ICON_ANCHOR_BOTTOM),
                iconColor(Color.BLUE),
                textField(TEXT_FIELD_EXPRESSION),
                textFont(NORMAL_FONT_STACK),
                textColor(Color.BLUE),
                textAllowOverlap(false),
                textAnchor(Property.TEXT_ANCHOR_TOP),
                textSize(10f)
        );
        shuffleMapboxSign();

        // number format layer
        Source numberFormatSource = new GeoJsonSource(NUMBER_FORMAT_SOURCE, Point.fromLngLat(54.3697, 24.4128));
        numberFormatSymbolLayer = new SymbolLayer(NUMBER_FORMAT_LAYER, NUMBER_FORMAT_SOURCE);
        numberFormatSymbolLayer.setProperties(
                textField(
                        numberFormat(123.456789, locale("nl-NL"), currency("EUR"))
                )
        );

        mapboxMap.setStyle(new Style.Builder()
                        .fromUri(Style.BASE_DEFAULT)
                        .withImage("Car", Objects.requireNonNull(carBitmap), false)
                        .withSources(markerSource, mapboxSignSource, numberFormatSource)
                        .withLayers(markerSymbolLayer, mapboxSignSymbolLayer, numberFormatSymbolLayer)
                , new Style.OnStyleLoaded() {
                    @Override
                    public void onStyleLoaded(@NonNull Style style) {
                        symbolManager = new SymbolManager(mapView, mapboxMap, style);
                        symbolManager.setIconAllowOverlap(true);
                        symbolManager.setTextAllowOverlap(true);

                        // Create Symbol
                        SymbolOptions SymbolOptions = new SymbolOptions()
                                .withLatLng(new LatLng(24.4628, 54.3697))
                                .withTextField("Test Data");
                        symbolManager.create(SymbolOptions);

                        symbolManager.addClickListener(new OnSymbolClickListener() {
                            @Override
                            public boolean onAnnotationClick(Symbol symbol) {
                                Toast.makeText(SymbolLayerActivity.this, "Click:" + symbol.getTextField(), Toast.LENGTH_SHORT).show();
                                return false;
                            }
                        });
                    }
                });

        // Set a click-listener so we can manipulate the map
        mapboxMap.addOnMapClickListener(SymbolLayerActivity.this);
    }

    @Override
    public boolean onMapClick(@NonNull LatLng point) {
        // Query which features are clicked
        PointF screenLoc = mapboxMap.getProjection().toScreenLocation(point);
        List<Feature> markerFeatures = mapboxMap.queryRenderedFeatures(screenLoc, MARKER_LAYER);
        if (!markerFeatures.isEmpty()) {
            for (Feature feature : Objects.requireNonNull(markerCollection.features())) {
                if (feature.getStringProperty(ID_FEATURE_PROPERTY)
                        .equals(markerFeatures.get(0).getStringProperty(ID_FEATURE_PROPERTY))) {

                    // use DDS
                    boolean selected = feature.getBooleanProperty(SELECTED_FEATURE_PROPERTY);
                    feature.addBooleanProperty(SELECTED_FEATURE_PROPERTY, !selected);

                    // validate symbol flicker regression for #13407
                    markerSymbolLayer.setProperties(iconOpacity(match(
                            get(ID_FEATURE_PROPERTY), literal(1.0f),
                            stop(feature.getStringProperty("id"), selected ? 0.3f : 1.0f)
                    )));
                }
            }
            markerSource.setGeoJson(markerCollection);
        } else {
            List<Feature> mapboxSignFeatures = mapboxMap.queryRenderedFeatures(screenLoc, MAPBOX_SIGN_LAYER);
            if (!mapboxSignFeatures.isEmpty()) {
                shuffleMapboxSign();
            }
        }

        return false;
    }

    private void toggleTextSize() {
        if (markerSymbolLayer != null) {
            Number size = markerSymbolLayer.getTextSize().getValue();
            if (size != null) {
                markerSymbolLayer.setProperties((float) size > 10 ? textSize(10f) : textSize(20f));
            }
        }
    }

    private void toggleTextField() {
        if (markerSymbolLayer != null) {
            if (TEXT_FIELD_EXPRESSION.equals(markerSymbolLayer.getTextField().getExpression())) {
                markerSymbolLayer.setProperties(textField("āA"));
            } else {
                markerSymbolLayer.setProperties(textField(TEXT_FIELD_EXPRESSION));
            }
        }
    }

    private void toggleTextFont() {
        if (markerSymbolLayer != null) {
            if (Arrays.equals(markerSymbolLayer.getTextFont().getValue(), NORMAL_FONT_STACK)) {
                markerSymbolLayer.setProperties(textFont(ITALIC_FONT_STACK));
            } else {
                markerSymbolLayer.setProperties(textFont(NORMAL_FONT_STACK));
            }
        }
    }

    private void shuffleMapboxSign() {
        if (mapboxSignSymbolLayer != null) {
            mapboxSignSymbolLayer.setProperties(
                    textField(
                            format(
                                    formatEntry("M", formatFontScale(2)),
                                    getRandomColorEntryForString("a"),
                                    getRandomColorEntryForString("p"),
                                    getRandomColorEntryForString("b"),
                                    getRandomColorEntryForString("o"),
                                    getRandomColorEntryForString("x")
                            )
                    ),
                    textColor(Color.BLACK),
                    textFont(ITALIC_FONT_STACK),
                    textSize(25f),
                    textRotationAlignment(Property.TEXT_ROTATION_ALIGNMENT_MAP)
            );
        }
    }

    private Expression.FormatEntry getRandomColorEntryForString(@NonNull String string) {
        return formatEntry(string,
                formatTextColor(
                        rgb(
                                random.nextInt(256),
                                random.nextInt(256),
                                random.nextInt(256)
                        )
                ));
    }

    private JsonObject featureProperties(@NonNull String id, @NonNull String title) {
        JsonObject object = new JsonObject();
        object.add(ID_FEATURE_PROPERTY, new JsonPrimitive(id));
        object.add(TITLE_FEATURE_PROPERTY, new JsonPrimitive(title));
        object.add(SELECTED_FEATURE_PROPERTY, new JsonPrimitive(false));
        return object;
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mapboxMap != null) {
            mapboxMap.removeOnMapClickListener(this);
        }
        mapView.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_symbol_layer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_toggle_text_size:
                toggleTextSize();
                return true;
            case R.id.action_toggle_text_field:
                toggleTextField();
                return true;
            case R.id.action_toggle_text_font:
                toggleTextFont();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}