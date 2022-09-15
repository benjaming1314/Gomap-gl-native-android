package com.gomap.demo.model.annotations;

import com.gomap.sdk.annotations.Marker;

public class CityStateMarker extends Marker {

  private String infoWindowBackgroundColor;

  public CityStateMarker(CityStateMarkerOptions cityStateOptions, String color) {
    super(cityStateOptions);
    infoWindowBackgroundColor = color;
  }

  public String getInfoWindowBackgroundColor() {
    return infoWindowBackgroundColor;
  }

}
