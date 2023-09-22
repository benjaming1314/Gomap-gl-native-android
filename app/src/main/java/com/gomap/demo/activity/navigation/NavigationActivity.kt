package com.gomap.demo.activity.navigation

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.blankj.utilcode.util.SizeUtils
import com.blankj.utilcode.util.ToastUtils
import com.gomap.demo.R
import com.gomap.demo.activity.navigation.parse.ParseUtils
import com.gomap.maps.navigation.NavigationUIController
import com.gomap.maps.navigation.listener.OnNavListener
import com.gomap.maps.navigation.model.NavigationResult
import com.gomap.sdk.camera.CameraPosition
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
import com.gomap.sdk.maps.Style.OnStyleLoaded
import com.gomap.sdk.navigation.FrameWorkApiProxy
import com.gomap.sdk.navigation.NavigationControl
import com.gomap.sdk.navigation.bean.*
import com.gomap.sdk.utils.BitmapUtils
import com.gomap.sdk.utils.ThreadUtils
import kotlinx.android.synthetic.main.activity_navigation.*

class NavigationActivity : AppCompatActivity(), NavigationControl.NavigationEndListener,
    NavigationControl.ReRoutePlanListener, NavigationControl.RoutePlanListener,
    NavigationControl.ShowGuideInfoListener, LocationEngineCallback<LocationEngineResult> {

    private lateinit var startRoutePlanning: Button
    private lateinit var cancelRoutePlanning: Button
    private lateinit var startNavi: Button
    private lateinit var finishNavi: Button
    private lateinit var llWrapper: LinearLayout
    private lateinit var mapboxMap: MapboxMap
    private lateinit var mapView: MapView


    private var routesInfo: RoutesInfo? = null


    private var index = 0

    private val TAG = "navigation"

    private var navigationUIController: NavigationUIController? = null
//    start LngLat{longitude=54.4042004, latitude=24.4866095}
//    end LngLat{longitude=54.378343616053456, latitude=24.4945967220051}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation)


        if (PermissionsManager.areLocationPermissionsGranted(this)) {
        } else {
            var permissionsManager = PermissionsManager(object : PermissionsListener {
                override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
                    Toast.makeText(
                        this@NavigationActivity,
                        "You need to accept location permissions.",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onPermissionResult(granted: Boolean) {
                    if (granted) {
                    } else {
                        finish()
                    }
                }
            })
            permissionsManager.requestLocationPermissions(this)
        }

        startRoutePlanning = findViewById(R.id.btn_route)
        cancelRoutePlanning = findViewById(R.id.btn_route_cancel)
        startNavi = findViewById(R.id.btn_start_navi)
        finishNavi = findViewById(R.id.btn_finish_navi)
        llWrapper = findViewById(R.id.ll_wrapper)

        startRoutePlanning.setOnClickListener {
            mapboxMap.navigationControl.startRoutePlanningByCar(TestData.get())
        }

        cancelRoutePlanning.setOnClickListener {
            mapboxMap.navigationControl.finishRoutePlanning()
        }

        startNavi.setOnClickListener {
            //真实导航
//            mapboxMap.navigationControl.startNavigation()
            //模拟导航
            mapboxMap.navigationControl.startSimulationNavigation()
            navigationUIController!!.showNaviView()
            navigationUIController?.showNaviBottomVisible(false)
            llWrapper.visibility = View.GONE
        }
        //手动切换
        btn_change_navi.setOnClickListener {
            var max = 0
            if (routesInfo != null) {
                max = routesInfo?.getRoutesInfo()?.size ?: 0
            }
            if (max > 0) {
                index++
                if (index >= max) {
                    index = 0
                }
            }
            mapboxMap.navigationControl.changeRouteSelectIndex(index)
        }

        btn_route_avoid.setOnClickListener {
            var avoidLocations = AvoidLocations().apply {
                type = AvoidLocations.AvoidLocationsType.CIRCLE.type

                var avoidLocationList = ArrayList<LatLng>()
                var avoidRadiuses = ArrayList<Long>()
                avoidLocationList.add(LatLng()
                    .apply
                    {
                        latitude = 24.480464
                        longitude = 54.38684
                    })

                avoidRadiuses.add(300L)
                avoidLocations = avoidLocationList

                this.avoidRadiuses = avoidRadiuses
            }



//test
//            var avoidLocations = AvoidLocations().apply {
//                type = AvoidLocations.AvoidLocationsType.CIRCLE.type
//
//                var avoidLocationList = ArrayList<LatLng>()
//                var avoidRadiuses = ArrayList<Long>()
//                for (i in 0 until 10){
//                    avoidRadiuses.add(3L + i)
//                    avoidLocationList.add(LatLng()
//                        .apply
//                     {
//                         latitude = 24.3443 + i
//                         longitude = 54.13232 + i
//                     })
//                }
//                avoidLocations = avoidLocationList
//
//                var excludePolygons = ArrayList<Polygon>()
//
//                for (i in 0 until 10){
//                   var pointList  =  ArrayList<Point>()
//                    for (i in 0 until 20){
//                        var point  = Point.fromLngLat(54.13232 + i,24.3443 + i)
//                        pointList.add(point)
//                    }
//                    excludePolygons.add(Polygon.fromLngLats(
//                        ArrayList<List<Point>>().apply {
//                            add(pointList)
//                        }
//                    ))
//                }
//                this.avoidRadiuses = avoidRadiuses
//                this.excludePolygons = excludePolygons
//            }

            mapboxMap.navigationControl.startRoutePlanningByCar(TestData.get(), avoidLocations)
        }

        btn_route_more.setOnClickListener {
            mapboxMap.navigationControl.startRoutePlanningByCar(TestData.getMore())

        }

        finishNavi.setOnClickListener {
            //真实导航
//            mapboxMap.navigationControl.finishNavigation()
            //模拟导航
            mapboxMap.navigationControl.finishSimulationNavigation()
            navigationUIController!!.hideNaviView()
        }

        mapView = findViewById<MapView>(R.id.mapView)
        mapView.getMapAsync {
            mapboxMap = it
            mapboxMap.setStyle(
                Style.BASE_DEFAULT
            ) {
                mapboxMap.moveCamera(
                    CameraUpdateFactory.newCameraPosition(
                        CameraPosition.Builder()
                            .target(LatLng(24.4866095, 54.4042004))
                            .zoom(16.0)
                            .bearing(0.0)
                            .tilt(0.0)
                            .build()
                    )
                )
                FrameWorkApiProxy.setMap(mapboxMap.nativeMapPtr)
                mapboxMap.navigationControl.init(this@NavigationActivity)
                mapboxMap.navigationControl.setNavigationEndListener(this)
                mapboxMap.navigationControl.setReRoutePlanListener(this)
                mapboxMap.navigationControl.setRoutePlanListener(this)
                mapboxMap.navigationControl.setOnNavigationListener(this)
                navigationUIController = NavigationUIController(mapView)
                navigationUIController?.setOnNavListener(object : OnNavListener {
                    override fun onNaviCancel() {
                        mapboxMap.navigationControl.finishSimulationNavigation()
                        navigationUIController?.hideNaviView()
                        llWrapper.visibility = View.VISIBLE
                    }

                    override fun onSettingClick() {

                    }
                })

                mapboxMap.navigationControl.setRouteClickListener {
                    mapboxMap.navigationControl.changeRouteSelectIndex(it)
                }
                mapboxMap.navigationControl.setCameraClickListener {
                    ToastUtils.showShort("Camera:"+it.latitude +" " +it.longitude)
                }

                initNaviConfig()
                activateLocationComponent(it)


            }
        }
    }

    private fun activateLocationComponent(style: Style) {
        //初始化定位组件
        val component = mapboxMap.locationComponent

        val locationComponentOptions = LocationComponentOptions.builder(this)
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
                .builder(this, style)
                .locationComponentOptions(locationComponentOptions)
                .useDefaultLocationEngine(true)
                .locationEngineRequest(
                    LocationEngineRequest.Builder(750)
                        .setFastestInterval(750)
                        .setPriority(LocationEngineRequest.PRIORITY_HIGH_ACCURACY)
                        .build()
                )
                .build()
        )

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        component.isLocationComponentEnabled = true

        component.cameraMode = CameraMode.TRACKING
        component.renderMode = RenderMode.COMPASS
        component.locationEngine?.getLastLocation(this)

        //位置更新
        component.addLocationChangeListener(this)
    }

    override fun onSuccess(result: LocationEngineResult?) {
        if (result != null) {
            ToastUtils.showShort("lat:" + result.lastLocation?.latitude?.toString() + " lon:" + result.lastLocation?.longitude)
        }
    }

    override fun onFailure(exception: java.lang.Exception) {
    }

    private fun initNaviConfig() {

        mapboxMap.navigationControl?.setRouteSelectWidth(SizeUtils.dp2px(3f).toFloat())

        mapboxMap.navigationControl?.setRouteNoSelectColor(
            ContextCompat.getColor(
                this,
                R.color.commonview_green_bcecca
            )
        )
        mapboxMap?.navigationControl?.setRouteSelectColor(
            ContextCompat.getColor(
                this,
                R.color.commonview_green_5AC776
            )
        )

        var drawable = ContextCompat.getDrawable(this, R.drawable.biz_ic_start_point)
        var enddrawable = ContextCompat.getDrawable(this, R.drawable.ic_map_car)
        mapboxMap.navigationControl?.setStartRouteIcon(BitmapUtils.getBitmapFromDrawable(drawable))
        mapboxMap.navigationControl?.setEndRouteIcon(BitmapUtils.getBitmapFromDrawable(enddrawable))

    }

    //导航结束
    override fun naviEnd(p0: Double, p1: Double) {
        navigationUIController?.hideNaviView()
        ThreadUtils.runMain {
            llWrapper.visibility = View.VISIBLE
        }
    }

    //重新规划路线结果
    override fun reRoutePlan(p0: Boolean) {

    }

    //路线规划结果
    override fun routePlan(p0: Boolean) {
        mapboxMap.locationComponent.hideLocationLayer()
        mapboxMap.navigationControl.hideLocationIcon()

    }

    //路线规划信息，时间 距离 红黄灯等
    @SuppressLint("LogNotTimber")
    override fun routePlanInfo(p0: RoutesInfo?) {
        routesInfo = p0
        p0?.let {
            Log.i(TAG, " ")
            Log.i(TAG, "共${p0.routesInfo.size}条路线,信息如下: \n" + ParseUtils.covertRouteResult(it))
        }
    }

    private val navigationResult = NavigationResult()

    //超速 overspeed是否超速，curSpeed当前速度 km/h
    override fun onOverSpeedCallback(overspeed: Boolean, curSpeed: Int) {
        navigationResult.isOverspeed = overspeed
        navigationResult.currentSpeed = curSpeed
    }

    //当前限速信息 km/h
    override fun getCurRoadSpeedLimit(speed: Int) {
        navigationResult.limitSpeed = speed
    }

    //车道信息
    override fun getLaneInfoCallback(laneInfo: LaneInfo?) {
        navigationResult.laneInfo = laneInfo
    }

    //导航转向信息
    override fun getTurnInfoCallback(turnInfo: TurnInfo?) {
        navigationResult.turnInfo = turnInfo
    }

    /**
     * eta信息
     * 在导航过程中的数据回调，eta信息是最后一个调用，因此在这个接口更新数据
     *
     * @param etaInfo
     */
    override fun getEtaInfoCallback(etaInfo: EtaInfo?) {
        navigationResult.etaInfo = etaInfo
        ThreadUtils.runMain {
            navigationUIController!!.update(navigationResult, DrivingType.CAR)
        }
    }

    //track gps callback
    override fun getTrackGpsCallback(p0: GpsLocation?) {

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

    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
        //主界面没有地图 无需重置
//        resetMap()
    }

    /**
     * 由于底层对map对象操作时，只保留一个map对象
     * 所以当主界面是地图界面时，导航结束返回主界面要重设map对象
     */
    private fun resetMap() {
        val cur = FrameWorkApiProxy.getCurPtr()
        val old = FrameWorkApiProxy.getOldPtr()
        if (cur != 0L && old != 0L && cur != old) {
            FrameWorkApiProxy.setMap(old)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if(mapboxMap.navigationControl.isOnNavigation) {
            mapboxMap.navigationControl.finishSimulationNavigation()
        }
        finish()
    }

}