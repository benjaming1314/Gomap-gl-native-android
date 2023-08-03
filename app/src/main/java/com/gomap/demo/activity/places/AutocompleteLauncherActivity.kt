package com.gomap.demo.activity.places

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gomap.demo.R
import com.gomap.demo.utils.PreferenceStorageUtils
import com.gomap.plugin.api.model.PoiModel
import com.gomap.sdk.camera.CameraPosition
import com.gomap.sdk.camera.CameraUpdateFactory
import com.gomap.sdk.geometry.LatLng
import com.gomap.sdk.maps.MapboxMap
import com.gomap.sdk.maps.OnMapReadyCallback
import com.gomap.sdk.maps.Style
import com.gomap.plugin.places.autocomplete.PlaceAutocomplete
import com.gomap.plugin.places.autocomplete.model.PlaceOptions
import kotlinx.android.synthetic.main.activity_places_launcher.*

class AutocompleteLauncherActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var home: PoiModel
    private lateinit var work: PoiModel
    private lateinit var mapboxMap: MapboxMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_places_launcher)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    override fun onMapReady(mapboxMap: MapboxMap) {

        this.mapboxMap = mapboxMap

        mapboxMap.setStyle(Style.BASE_DEFAULT) {

            addUserLocations()

            var apiKey = PreferenceStorageUtils.getApiKeyData("")?:""

            fabCard.setOnClickListener {
                val intent = PlaceAutocomplete.IntentBuilder()
                    .accessToken(apiKey)
                    .placeOptions(PlaceOptions.builder()
                        .backgroundColor(Color.parseColor("#EEEEEE"))
                        .addInjectedFeature(home)
                        .addInjectedFeature(work)
                        .build())
                    .lat(24.4628)
                    .lon(54.3697)

                        .build(this@AutocompleteLauncherActivity)
                startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE)
            }

            fabCard.setOnLongClickListener {
                PlaceAutocomplete.clearRecentHistory(this)
                Toast.makeText(this, "database cleared", Toast.LENGTH_LONG).show()
                true
            }

            fabFullScreen.setOnClickListener {
                val intent = PlaceAutocomplete.IntentBuilder()
                        .accessToken(apiKey)
                    .lat(24.4628)
                    .lon(54.3697)
                    .placeOptions(PlaceOptions.builder()
                        .backgroundColor(Color.WHITE)
                        .addInjectedFeature(home)
                        .addInjectedFeature(work)
                        .statusbarColor(Color.MAGENTA)
                        .build())
                        .build(this@AutocompleteLauncherActivity)
                startActivityForResult(intent, REQUEST_CODE_AUTOCOMPLETE)
            }
        }
    }

    private fun addUserLocations() {
        home = PoiModel.builder().name("Directions to Home")
            .lat("24.4428")
            .lng("54.3897")
            .address("300 Massachusetts Ave NW")
            .build()

        work = PoiModel.builder().name("Directions to Work")
            .address("740 15th St NW")
            .lat("24.4528")
            .lng("54.3797")
            .build()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_AUTOCOMPLETE) {
            val feature = PlaceAutocomplete.getPlace(data)
            Toast.makeText(this, feature.name(), Toast.LENGTH_LONG).show()

            // Retrieve selected location's CarmenFeature
            val selectedCarmenFeature = PlaceAutocomplete.getPlace(data)

            // Move map camera to the selected location
            mapboxMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.Builder()
                    .target(LatLng(selectedCarmenFeature.lat().toDouble(),selectedCarmenFeature.lng().toDouble()))
                    .zoom(15.5)
                    .build()), 3000)
        }
    }

    public override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    public override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    companion object {
        private val REQUEST_CODE_AUTOCOMPLETE = 1
    }
}
