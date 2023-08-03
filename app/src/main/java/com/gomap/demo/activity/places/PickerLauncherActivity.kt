package com.gomap.demo.activity.places

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gomap.demo.R
import com.gomap.sdk.camera.CameraPosition
import com.gomap.sdk.geometry.LatLng
import com.gomap.plugin.places.picker.PlacePicker
import com.gomap.plugin.places.picker.model.PlacePickerOptions
import kotlinx.android.synthetic.main.activity_picker_launcher.*

class PickerLauncherActivity : AppCompatActivity() {

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_picker_launcher)

        userLocationSwitch.text = getString(R.string.user_location_button_disabled)
        userLocationSwitch.setOnCheckedChangeListener { compoundButton, checked ->
            userLocationSwitch.text = if (checked)
                getString(R.string.user_location_button_enabled)
            else getString(R.string.user_location_button_disabled)
        }

        fabLocationPicker.setOnClickListener { _ ->
                startActivityForResult(
                        PlacePicker.IntentBuilder()
                                .placeOptions(PlacePickerOptions.builder()
                                        .includeDeviceLocationButton(userLocationSwitch.isChecked)
                                        .statingCameraPosition(CameraPosition.Builder()
                                                .target(LatLng(24.4628, 54.3697))
                                                .zoom(16.0)
                                                .build())
                                        .build())
                                .build(this), REQUEST_CODE
                )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val cameraPosition = PlacePicker.getLastCameraPosition(data)
            Toast.makeText(this, cameraPosition.target.toString(), Toast.LENGTH_LONG).show()
        }
    }

    companion object {
        private val REQUEST_CODE = 5678
    }
}
