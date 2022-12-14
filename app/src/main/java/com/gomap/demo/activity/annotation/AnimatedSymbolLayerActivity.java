package com.gomap.demo.activity.annotation;

import static com.gomap.sdk.style.expressions.Expression.get;
import static com.gomap.sdk.style.layers.PropertyFactory.iconAllowOverlap;
import static com.gomap.sdk.style.layers.PropertyFactory.iconIgnorePlacement;
import static com.gomap.sdk.style.layers.PropertyFactory.iconImage;
import static com.gomap.sdk.style.layers.PropertyFactory.iconRotate;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.LinearInterpolator;

import androidx.appcompat.app.AppCompatActivity;

import com.gomap.demo.R;
import com.gomap.geojson.Feature;
import com.gomap.geojson.FeatureCollection;
import com.gomap.geojson.Point;
import com.gomap.sdk.camera.CameraPosition;
import com.gomap.sdk.camera.CameraUpdateFactory;
import com.gomap.sdk.geometry.LatLng;
import com.gomap.sdk.geometry.LatLngBounds;
import com.gomap.sdk.maps.MapView;
import com.gomap.sdk.maps.MapboxMap;
import com.gomap.sdk.maps.Style;
import com.gomap.sdk.style.layers.SymbolLayer;
import com.gomap.sdk.style.sources.GeoJsonSource;
import com.gomap.turf.TurfMeasurement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Test activity showcasing animating a SymbolLayer.
 */
public class AnimatedSymbolLayerActivity extends AppCompatActivity {

  private static final String PASSENGER = "passenger";
  private static final String PASSENGER_LAYER = "passenger-layer";
  private static final String PASSENGER_SOURCE = "passenger-source";
  private static final String TAXI = "taxi";
  private static final String TAXI_LAYER = "taxi-layer";
  private static final String TAXI_SOURCE = "taxi-source";
  private static final String RANDOM_CAR_LAYER = "random-car-layer";
  private static final String RANDOM_CAR_SOURCE = "random-car-source";
  private static final String RANDOM_CAR_IMAGE_ID = "random-car";
  private static final String PROPERTY_BEARING = "bearing";
  private static final String WATERWAY_LAYER_ID = "water_intermittent";
  private static final int DURATION_RANDOM_MAX = 1500;
  private static final int DURATION_BASE = 3000;

  private final Random random = new Random();
  private final LatLng CENTER = new LatLng(24.4628,54.3697);

  private MapView mapView;
  private MapboxMap mapboxMap;
  private Style style;

  private List<Car> randomCars = new ArrayList<>();
  private GeoJsonSource randomCarSource;
  private Car taxi;
  private GeoJsonSource taxiSource;
  private LatLng passenger;

  private List<Animator> animators = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_animated_marker);

    mapView = findViewById(R.id.mapView);
    mapView.onCreate(savedInstanceState);
    mapView.getMapAsync(map -> {
      this.mapboxMap = map;
      map.setStyle(Style.getPredefinedStyle("Streets"), style -> {

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(CENTER)
                .zoom(12)
                .tilt(30)
                .tilt(0)
                .build();
        mapboxMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        this.style = style;
        setupCars();
        animateRandomRoutes();
        animateTaxi();
      });
    });
  }

  private void setupCars() {
    addRandomCars();
    addPassenger();
    addMainCar();
  }

  private void animateRandomRoutes() {
    final Car longestDrive = getLongestDrive();
    final Random random = new Random();
    for (final Car car : randomCars) {
      final boolean isLongestDrive = longestDrive.equals(car);
      ValueAnimator valueAnimator = ValueAnimator.ofObject(new LatLngEvaluator(), car.current, car.next);
      valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

        private LatLng latLng;

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
          latLng = (LatLng) animation.getAnimatedValue();
          car.current = latLng;
          if (isLongestDrive) {
            updateRandomCarSource();
          }
        }
      });

      if (isLongestDrive) {
        valueAnimator.addListener(new AnimatorListenerAdapter() {
          @Override
          public void onAnimationEnd(Animator animation) {
            super.onAnimationEnd(animation);
            updateRandomDestinations();
            animateRandomRoutes();
          }
        });
      }

      valueAnimator.addListener(new AnimatorListenerAdapter() {
        @Override
        public void onAnimationStart(Animator animation) {
          super.onAnimationStart(animation);
          car.feature.properties().addProperty("bearing", Car.getBearing(car.current, car.next));
        }
      });

      int offset = random.nextInt(2) == 0 ? 0 : random.nextInt(1000) + 250;
      valueAnimator.setStartDelay(offset);
      valueAnimator.setDuration(car.duration - offset);
      valueAnimator.setInterpolator(new LinearInterpolator());
      valueAnimator.start();

      animators.add(valueAnimator);
    }
  }

  private void animateTaxi() {
    ValueAnimator valueAnimator = ValueAnimator.ofObject(new LatLngEvaluator(), taxi.current, taxi.next);
    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

      private LatLng latLng;

      @Override
      public void onAnimationUpdate(ValueAnimator animation) {
        latLng = (LatLng) animation.getAnimatedValue();
        taxi.current = latLng;
        updateTaxiSource();
      }
    });

    valueAnimator.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationEnd(Animator animation) {
        super.onAnimationEnd(animation);
        updatePassenger();
        animateTaxi();
      }
    });

    valueAnimator.addListener(new AnimatorListenerAdapter() {
      @Override
      public void onAnimationStart(Animator animation) {
        super.onAnimationStart(animation);
        taxi.feature.properties().addProperty("bearing", Car.getBearing(taxi.current, taxi.next));
      }
    });

    valueAnimator.setDuration((long) (7 * taxi.current.distanceTo(taxi.next)));
    valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
    valueAnimator.start();

    animators.add(valueAnimator);
  }

  private void updatePassenger() {
    passenger = getLatLngInBounds();
    updatePassengerSource();
    taxi.setNext(passenger);
  }

  private void updatePassengerSource() {
    GeoJsonSource source = style.getSourceAs(PASSENGER_SOURCE);
    FeatureCollection featureCollection = FeatureCollection.fromFeatures(new Feature[] {
      Feature.fromGeometry(
        Point.fromLngLat(
          passenger.getLongitude(),
          passenger.getLatitude()
        )
      )
    });
    source.setGeoJson(featureCollection);
  }

  private void updateTaxiSource() {
    taxi.updateFeature();
    taxiSource.setGeoJson(taxi.feature);
  }

  private void updateRandomDestinations() {
    for (Car randomCar : randomCars) {
      randomCar.setNext(getLatLngInBounds());
    }
  }

  private Car getLongestDrive() {
    Car longestDrive = null;
    for (Car randomCar : randomCars) {
      if (longestDrive == null) {
        longestDrive = randomCar;
      } else if (longestDrive.duration < randomCar.duration) {
        longestDrive = randomCar;
      }
    }
    return longestDrive;
  }

  private void updateRandomCarSource() {
    for (Car randomCarsRoute : randomCars) {
      randomCarsRoute.updateFeature();
    }
    randomCarSource.setGeoJson(featuresFromRoutes());
  }

  private FeatureCollection featuresFromRoutes() {
    List<Feature> features = new ArrayList<>();
    for (Car randomCarsRoute : randomCars) {
      features.add(randomCarsRoute.feature);
    }
    return FeatureCollection.fromFeatures(features);
  }

  private long getDuration() {
    return random.nextInt(DURATION_RANDOM_MAX) + DURATION_BASE;
  }

  private void addRandomCars() {
    LatLng latLng;
    LatLng next;
    for (int i = 0; i < 10; i++) {
      latLng = getLatLngInBounds();
      next = getLatLngInBounds();

      JsonObject properties = new JsonObject();
      properties.addProperty(PROPERTY_BEARING, Car.getBearing(latLng, next));

      Feature feature = Feature.fromGeometry(
        Point.fromLngLat(
          latLng.getLongitude(),
          latLng.getLatitude()
        ), properties);

      randomCars.add(
        new Car(feature, next, getDuration())
      );
    }

    randomCarSource = new GeoJsonSource(RANDOM_CAR_SOURCE, featuresFromRoutes());
    style.addSource(randomCarSource);
    style.addImage(RANDOM_CAR_IMAGE_ID,
      ((BitmapDrawable) getResources().getDrawable(R.drawable.ic_car_top)).getBitmap());

    SymbolLayer symbolLayer = new SymbolLayer(RANDOM_CAR_LAYER, RANDOM_CAR_SOURCE);
    symbolLayer.withProperties(
      iconImage(RANDOM_CAR_IMAGE_ID),
      iconAllowOverlap(true),
      iconRotate(get(PROPERTY_BEARING)),
      iconIgnorePlacement(true)
    );

    style.addLayerBelow(symbolLayer, WATERWAY_LAYER_ID);
  }

  private void addPassenger() {
    passenger = getLatLngInBounds();
    FeatureCollection featureCollection = FeatureCollection.fromFeatures(new Feature[] {
      Feature.fromGeometry(
        Point.fromLngLat(
          passenger.getLongitude(),
          passenger.getLatitude()
        )
      )
    });

    style.addImage(PASSENGER,
      ((BitmapDrawable) getResources().getDrawable(R.drawable.icon_burned)).getBitmap());

    GeoJsonSource geoJsonSource = new GeoJsonSource(PASSENGER_SOURCE, featureCollection);
    style.addSource(geoJsonSource);

    SymbolLayer symbolLayer = new SymbolLayer(PASSENGER_LAYER, PASSENGER_SOURCE);
    symbolLayer.withProperties(
      iconImage(PASSENGER),
      iconIgnorePlacement(true),
      iconAllowOverlap(true)
    );
    style.addLayerBelow(symbolLayer, RANDOM_CAR_LAYER);
  }

  private void addMainCar() {
    LatLng latLng = getLatLngInBounds();
    JsonObject properties = new JsonObject();
    properties.addProperty(PROPERTY_BEARING, Car.getBearing(latLng, passenger));
    Feature feature = Feature.fromGeometry(
      Point.fromLngLat(
        latLng.getLongitude(),
        latLng.getLatitude()), properties);
    FeatureCollection featureCollection = FeatureCollection.fromFeatures(new Feature[] {feature});

    taxi = new Car(feature, passenger, getDuration());
    style.addImage(TAXI,
      ((BitmapDrawable) getResources().getDrawable(R.drawable.ic_taxi_top)).getBitmap());
    taxiSource = new GeoJsonSource(TAXI_SOURCE, featureCollection);
    style.addSource(taxiSource);

    SymbolLayer symbolLayer = new SymbolLayer(TAXI_LAYER, TAXI_SOURCE);
    symbolLayer.withProperties(
      iconImage(TAXI),
      iconRotate(get(PROPERTY_BEARING)),
      iconAllowOverlap(true),
      iconIgnorePlacement(true)

    );
    style.addLayer(symbolLayer);
  }

  private LatLng getLatLngInBounds() {
    LatLngBounds bounds = mapboxMap.getProjection().getVisibleRegion().latLngBounds;
    Random generator = new Random();
    double randomLat = bounds.getLatSouth() + generator.nextDouble()
      * (bounds.getLatNorth() - bounds.getLatSouth());
    double randomLon = bounds.getLonWest() + generator.nextDouble()
      * (bounds.getLonEast() - bounds.getLonWest());
    return new LatLng(randomLat, randomLon);
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
  protected void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
    mapView.onSaveInstanceState(outState);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    for (Animator animator : animators) {
      if (animator != null) {
        animator.removeAllListeners();
        animator.cancel();
      }
    }

    mapView.onDestroy();
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    mapView.onLowMemory();
  }

  /**
   * Evaluator for LatLng pairs
   */
  private static class LatLngEvaluator implements TypeEvaluator<LatLng> {

    private LatLng latLng = new LatLng();

    @Override
    public LatLng evaluate(float fraction, LatLng startValue, LatLng endValue) {
      latLng.setLatitude(startValue.getLatitude()
        + ((endValue.getLatitude() - startValue.getLatitude()) * fraction));
      latLng.setLongitude(startValue.getLongitude()
        + ((endValue.getLongitude() - startValue.getLongitude()) * fraction));
      return latLng;
    }
  }


  private static class Car {
    private Feature feature;
    private LatLng next;
    private LatLng current;
    private long duration;

    Car(Feature feature, LatLng next, long duration) {
      this.feature = feature;
      Point point = ((Point) feature.geometry());
      this.current = new LatLng(point.latitude(), point.longitude());
      this.duration = duration;
      this.next = next;
    }

    void setNext(LatLng next) {
      this.next = next;
    }

    void updateFeature() {
      feature = Feature.fromGeometry(Point.fromLngLat(
        current.getLongitude(),
        current.getLatitude())
      );
      feature.properties().addProperty("bearing", getBearing(current, next));
    }

    private static float getBearing(LatLng from, LatLng to) {
      return (float) TurfMeasurement.bearing(
        Point.fromLngLat(from.getLongitude(), from.getLatitude()),
        Point.fromLngLat(to.getLongitude(), to.getLatitude())
      );
    }
  }
}