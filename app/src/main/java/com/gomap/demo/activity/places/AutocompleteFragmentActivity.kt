package com.gomap.demo.activity.places

import android.graphics.Color
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.gomap.demo.R
import com.gomap.demo.utils.PreferenceStorageUtils
import com.gomap.plugin.api.model.PoiModel

import com.gomap.sdk.Mapbox
import com.gomap.sdk.exceptions.MapboxConfigurationException
import com.gomap.plugin.places.autocomplete.model.PlaceOptions
import com.gomap.plugin.places.autocomplete.ui.PlaceAutocompleteFragment
import com.gomap.plugin.places.autocomplete.ui.PlaceSelectionListener

class AutocompleteFragmentActivity : AppCompatActivity() {

    private var lat = 24.4628
    private var lon = 54.3697

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_places_fragment)

        val autocompleteFragment: PlaceAutocompleteFragment
        if (savedInstanceState == null) {
            val placeOptions = PlaceOptions.builder()
                    .build()

            var apiKey = PreferenceStorageUtils.getApiKeyData("")?:""

            autocompleteFragment = PlaceAutocompleteFragment.newInstance(
                apiKey,lat,lon,
                    placeOptions
            )

            val transaction = supportFragmentManager.beginTransaction()
            transaction.add(R.id.fragment_container, autocompleteFragment, PlaceAutocompleteFragment.TAG)
            transaction.commit()
        } else {
            autocompleteFragment = supportFragmentManager.findFragmentByTag(PlaceAutocompleteFragment.TAG) as PlaceAutocompleteFragment
        }

        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(carmenFeature: PoiModel) {
                Toast.makeText(this@AutocompleteFragmentActivity,
                        carmenFeature.name(), Toast.LENGTH_LONG).show()
                finish()
            }

            override fun onCancel() {
                finish()
            }
        })
    }
}
