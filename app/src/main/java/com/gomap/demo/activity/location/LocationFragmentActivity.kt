package com.gomap.demo.activity.location

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gomap.demo.R
import com.gomap.sdk.camera.CameraUpdateFactory
import com.gomap.sdk.geometry.LatLng
import com.gomap.sdk.location.LocationComponentActivationOptions
import com.gomap.sdk.location.LocationComponentOptions
import com.gomap.sdk.location.engine.LocationEngineCallback
import com.gomap.sdk.location.engine.LocationEngineRequest
import com.gomap.sdk.location.engine.LocationEngineResult
import com.gomap.sdk.location.modes.CameraMode
import com.gomap.sdk.location.modes.RenderMode
import com.gomap.sdk.location.permissions.PermissionsListener
import com.gomap.sdk.location.permissions.PermissionsManager
import com.gomap.sdk.maps.MapView
import com.gomap.sdk.maps.MapboxMap
import com.gomap.sdk.maps.Style
import kotlinx.android.synthetic.main.activity_location_layer_fragment.*

class LocationFragmentActivity : AppCompatActivity() {
    private lateinit var permissionsManager: PermissionsManager

    private var locationFragment:LocationFragment ?=null

    private var isGpsMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location_layer_fragment)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            if (savedInstanceState == null) {
                locationFragment = LocationFragment.newInstance()
                supportFragmentManager
                    .beginTransaction()
                    .replace(R.id.container, locationFragment!!, LocationFragment.TAG)
                    .commit()
            }
        } else {
            permissionsManager = PermissionsManager(object : PermissionsListener {
                override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
                    Toast.makeText(
                        this@LocationFragmentActivity,
                        "You need to accept location permissions.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onPermissionResult(granted: Boolean) {
                    if (granted) {
                        if (savedInstanceState == null) {
                            locationFragment = LocationFragment.newInstance()
                            supportFragmentManager
                                .beginTransaction()
                                .replace(R.id.container, locationFragment!!, LocationFragment.TAG)
                                .commit()
                        }
                    } else {
                        finish()
                    }
                }
            })
            permissionsManager.requestLocationPermissions(this)
        }

        txt_change_mode.setOnClickListener {
            isGpsMode = !isGpsMode
            locationFragment?.changeRendererMode(isGpsMode)
            if (isGpsMode){
                txt_change_mode.text = "use gps"
            }else {
                txt_change_mode.text = "use compass"
            }
        }

        txt_change_gps_image.setOnClickListener {
            locationFragment?.changeGpsDrawable()
        }


    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    class LocationFragment : androidx.fragment.app.Fragment(), LocationEngineCallback<LocationEngineResult> {
        companion object {
            const val TAG = "LFragment"
            fun newInstance(): LocationFragment {
                return LocationFragment()
            }
        }

        private lateinit var mapView: MapView
        private lateinit var mapboxMap: MapboxMap

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            mapView = MapView(inflater.context)
            return mapView
        }

        @SuppressLint("MissingPermission")
        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            super.onViewCreated(view, savedInstanceState)
            mapView.onCreate(savedInstanceState)
            mapView.getMapAsync {
                mapboxMap = it
                it.setStyle(Style.getPredefinedStyle("Streets")) { style ->
                    val component = mapboxMap.locationComponent

                    val locationComponentOptions = LocationComponentOptions.builder(requireContext())
                        .foregroundDrawable(R.drawable.mapbox_user_icon)
                        .backgroundDrawable(R.drawable.mapbox_user_stroke_icon)
                        .foregroundDrawableStale(R.drawable.mapbox_user_icon_stale)
                        .backgroundDrawableStale(R.drawable.mapbox_user_stroke_icon)
                        .bearingDrawable(R.drawable.mapbox_user_bearing_icon)
                        .gpsDrawable(R.drawable.mapbox_user_puck_icon)
                        .pulseEnabled(true)
                        .pulseMaxRadius(50f)
                        .minZoomIconScale(0.6f)
                        .maxZoomIconScale(1.0f)
                        .pulseColor(Color.parseColor("#4B7DF6"))
                        .build()

                    component.activateLocationComponent(
                        LocationComponentActivationOptions
                            .builder(requireContext(), style)
                            .locationComponentOptions(locationComponentOptions)
                            .useDefaultLocationEngine(true)
                            .locationEngineRequest(
                                LocationEngineRequest.Builder(750)
                                .setFastestInterval(750)
                                .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                                .build())
                            .build()
                    )

                    component.isLocationComponentEnabled = true

                    component.cameraMode = CameraMode.TRACKING
                    component.renderMode = RenderMode.COMPASS
                    component.locationEngine?.getLastLocation(this)
                }
            }
        }



        override fun onSuccess(result: LocationEngineResult?) {
            if (!mapView.isDestroyed) mapboxMap.animateCamera(CameraUpdateFactory.newLatLngZoom(LatLng(result?.lastLocation), 12.0))
        }

        override fun onFailure(exception: Exception) {
            // noop
        }
        fun changeRendererMode(isGps:Boolean){
            if (isGps){
//                mapboxMap.locationComponent.setCameraMode(CameraMode.TRACKING_GPS)
                mapboxMap.locationComponent.renderMode = RenderMode.GPS
            }else {
//                mapboxMap.locationComponent.setCameraMode(CameraMode.TRACKING_COMPASS)
                mapboxMap.locationComponent.renderMode = RenderMode.COMPASS
            }
        }

        fun changeGpsDrawable() {
            val locationComponentOptions = LocationComponentOptions.builder(requireContext())
                .foregroundDrawable(R.drawable.mapbox_user_icon)
                .backgroundDrawable(R.drawable.mapbox_user_stroke_icon)
                .foregroundDrawableStale(R.drawable.mapbox_user_icon_stale)
                .backgroundDrawableStale(R.drawable.mapbox_user_stroke_icon)
                .bearingDrawable(R.drawable.mapbox_user_bearing_icon)
                .gpsDrawable(R.drawable.ic_car_top)
                .pulseEnabled(true)
                .pulseMaxRadius(50f)
                .minZoomIconScale(0.6f)
                .maxZoomIconScale(1.0f)
                .pulseColor(Color.parseColor("#4B7DF6"))
                .build()
            mapboxMap.locationComponent.applyStyle(locationComponentOptions)

        }

        override fun onStart() {
            super.onStart()
            mapView.onStart()
        }

        override fun onResume() {
            super.onResume()
            mapView.onResume()
        }

        override fun onPause() {
            super.onPause()
            mapView.onPause()
        }

        override fun onSaveInstanceState(outState: Bundle) {
            super.onSaveInstanceState(outState)
            mapView.onSaveInstanceState(outState)
        }

        override fun onStop() {
            super.onStop()
            mapView.onStop()
        }

        override fun onLowMemory() {
            super.onLowMemory()
            mapView.onLowMemory()
        }

        override fun onDestroyView() {
            super.onDestroyView()
            mapView.onDestroy()
        }
    }

    class EmptyFragment : androidx.fragment.app.Fragment() {
        companion object {
            const val TAG = "EmptyFragment"
            fun newInstance(): EmptyFragment {
                return EmptyFragment()
            }
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val textView = TextView(inflater.context)
            textView.text = "This is an empty Fragment"
            return textView
        }
    }
}
