package com.gomap.demo.animation

import android.animation.TypeEvaluator
import android.graphics.Color
import android.graphics.PointF
import androidx.annotation.ColorInt
import androidx.core.view.animation.PathInterpolatorCompat
import com.blankj.utilcode.util.ScreenUtils
import com.blankj.utilcode.util.SizeUtils
import com.gomap.sdk.location.LocationPropertyFactory
import com.gomap.sdk.maps.MapboxMap
import com.gomap.sdk.style.expressions.Expression

internal class PuckPulsingAnimator(var mapboxMap: MapboxMap) : PuckAnimator<Double>( TypeEvaluator<Double> { fraction, startValue, endValue ->
    startValue + fraction * (endValue - startValue)
}) {

  var enabled = false
  var maxRadius: Double = SizeUtils.dp2px(30f).toDouble()
  @ColorInt
  var pulsingColor: Int = Color.parseColor("#4B7DF6")
  var pulseFadeEnabled = true

  init {
    duration = PULSING_DEFAULT_DURATION
    repeatMode = RESTART
    repeatCount = INFINITE
    interpolator = PULSING_DEFAULT_INTERPOLATOR
  }

  /**
   * 根据经纬度 获取当前屏幕 1px 多少米
   */
  private fun calculateMetersPerPixel(mapboxMap: MapboxMap): Float {
   var centerLngLat = mapboxMap.projection.fromScreenLocation(PointF(ScreenUtils.getAppScreenWidth() / 2f,ScreenUtils.getAppScreenHeight() / 2f))
    val metersPerPixel = mapboxMap.projection.getMetersPerPixelAtLatitude(centerLngLat.latitude)
    return metersPerPixel.toFloat()
  }

  fun animateInfinite() {
    animate(0.0, maxRadius)
  }

  override fun updateLayer(fraction: Float, value: Double) {
    var opacity = 1.0f
    if (pulseFadeEnabled) {
      opacity = 1.0f - (value / maxRadius).toFloat()
    }
    opacity = if (fraction <= 0.1f) 0f else opacity

    val metersPerPixel =  calculateMetersPerPixel(mapboxMap);
    val newValue = value * metersPerPixel

    val rgbaArray = colorToRgbaArray(pulsingColor)
    rgbaArray[3] = opacity ?: 1f
      locationRenderer?.withProperties(
        LocationPropertyFactory.accuracyRadius(newValue.toFloat()),
        LocationPropertyFactory.accuracyRadiusColor(buildRGBAExpression(rgbaArray))
      )
//    locationRenderer?.emphasisCircleRadius(value)
//    locationRenderer?.emphasisCircleColor(buildRGBAExpression(rgbaArray))
  }

  companion object {

    fun buildRGBAExpression(colorArray: FloatArray): Expression {
      return Expression.rgba(colorArray[0].toDouble(), colorArray[1].toDouble(),colorArray[2].toDouble(),colorArray[3].toDouble())
    }


    fun colorToRgbaArray(@ColorInt color: Int): FloatArray {
      return floatArrayOf(
        (color shr 16 and 0xFF.toFloat().toInt()).toFloat(), // r (0-255)
        (color shr 8 and 0xFF.toFloat().toInt()).toFloat(), // g (0-255)
        (color and 0xFF.toFloat().toInt()).toFloat(), // b (0-255)
        (color shr 24 and 0xFF) / 255.0f // a (0-1)
      )
    }

    const val PULSING_DEFAULT_DURATION = 3_000L
    private val PULSING_DEFAULT_INTERPOLATOR = PathInterpolatorCompat.create(
      0.0f,
      0.0f,
      0.25f,
      1.0f
    )
  }
}