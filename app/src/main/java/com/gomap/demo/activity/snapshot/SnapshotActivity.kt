package com.gomap.demo.activity.snapshot

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.gomap.demo.R
import com.gomap.sdk.log.Logger
import com.gomap.sdk.maps.MapView
import com.gomap.sdk.maps.MapboxMap
import com.gomap.sdk.maps.OnMapReadyCallback
import com.gomap.sdk.maps.Style
import kotlinx.android.synthetic.main.activity_snapshot.*
import timber.log.Timber

/**
 * Test activity showcasing the Snapshot API to create and display a bitmap of the current shown Map.
 */
class SnapshotActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mapboxMap: MapboxMap

    private val idleListener = object : MapView.OnDidFinishRenderingFrameListener {
        override fun onDidFinishRenderingFrame(fully: Boolean) {
            if (fully) {
                mapView.removeOnDidFinishRenderingFrameListener(this)
                Logger.v(TAG, LOG_MESSAGE)
                mapboxMap.snapshot { snapshot ->
                    imageView.setImageBitmap(snapshot)
                    mapView.addOnDidFinishRenderingFrameListener(this)
                }
            }
        }
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_snapshot)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)
    }

    override fun onMapReady(map: MapboxMap) {
        mapboxMap = map
        mapboxMap.setLocalStyle(Style.getLocalStyle("Streets")) { mapView.addOnDidFinishRenderingFrameListener(idleListener) }
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
        mapboxMap.snapshot {
            Timber.e("Regression test for https://github.com/mapbox/mapbox-gl-native/pull/11358")
        }
        mapView.onPause()
    }

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    public override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView.onSaveInstanceState(outState)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    public override fun onDestroy() {
        super.onDestroy()
        mapView.removeOnDidFinishRenderingFrameListener(idleListener)
        mapView.onDestroy()
    }

    companion object {
        const val TAG = "Mbgl-SnapshotActivity"
        const val LOG_MESSAGE = "OnSnapshot"
    }
}