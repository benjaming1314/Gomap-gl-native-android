package com.gomap.demo.activity.navigation

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.blankj.utilcode.util.DeviceUtils
import com.blankj.utilcode.util.ToastUtils
import com.gomap.demo.R
import com.gomap.demo.activity.navigation.TestData
import com.gomap.demo.activity.navigation.parse.ParseUtils
import com.gomap.demo.utils.PreferenceStorageUtils
import com.gomap.maps.navigation.NavigationUIController
import com.gomap.maps.navigation.listener.OnNavListener
import com.gomap.maps.navigation.model.NavigationResult
import com.gomap.plugin.api.GomapAlongWaySearch
import com.gomap.plugin.api.model.*
import com.gomap.sdk.annotations.MarkerOptions
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
import com.gomap.sdk.markerview.MarkerView
import com.gomap.sdk.markerview.MarkerViewManager
import com.gomap.sdk.navigation.FrameWorkApiProxy
import com.gomap.sdk.navigation.NavigationControl
import com.gomap.sdk.navigation.bean.*
import com.gomap.sdk.utils.ThreadUtils
import kotlinx.android.synthetic.main.activity_annotation.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DecimalFormat
import java.util.*

class AlongTheWayActivity : AppCompatActivity(),
    NavigationControl.ReRoutePlanListener, NavigationControl.RoutePlanListener,
    NavigationControl.ShowGuideInfoListener, LocationEngineCallback<LocationEngineResult> {

    private lateinit var startRoutePlanning: Button
    private lateinit var cancelRoutePlanning: Button
    private lateinit var searchRestaurantAlongTheWay: Button
    private lateinit var searchShopAlongTheWay: Button
    private lateinit var searchMallAlongTheWay: Button
    private lateinit var mapboxMap: MapboxMap
    private lateinit var mapView: MapView


    private var routesInfo: RoutesInfo? = null

    private val TAG = "alongtheway"

    private var navigationUIController: NavigationUIController? = null

    private var markerViewManager: MarkerViewManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_along_the_way)


        if (PermissionsManager.areLocationPermissionsGranted(this)) {
        } else {
            var permissionsManager = PermissionsManager(object : PermissionsListener {
                override fun onExplanationNeeded(permissionsToExplain: MutableList<String>?) {
                    Toast.makeText(
                        this@AlongTheWayActivity,
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
        searchRestaurantAlongTheWay = findViewById(R.id.btn_search_resturant)
        searchShopAlongTheWay = findViewById(R.id.btn_search_shop)
        searchMallAlongTheWay = findViewById(R.id.btn_search_mall)

        startRoutePlanning.setOnClickListener {
            mapboxMap.navigationControl.startRoutePlanningByCar(TestData.get())
        }

        cancelRoutePlanning.setOnClickListener {
            mapboxMap.navigationControl.finishRoutePlanning()
        }

        searchRestaurantAlongTheWay.setOnClickListener {
            searchAlongTheWay("restaurant")
        }

        searchShopAlongTheWay.setOnClickListener {
            searchAlongTheWay("shop")
        }

        searchMallAlongTheWay.setOnClickListener {
            searchAlongTheWay("mall")
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

                markerViewManager = MarkerViewManager(mapView, mapboxMap)

                FrameWorkApiProxy.setMap(mapboxMap.nativeMapPtr)
                mapboxMap.navigationControl.init(this@AlongTheWayActivity)
                mapboxMap.navigationControl.setReRoutePlanListener(this)
                mapboxMap.navigationControl.setRoutePlanListener(this)
                mapboxMap.navigationControl.setOnNavigationListener(this)
                navigationUIController = NavigationUIController(mapView)
                navigationUIController?.setOnNavListener(object : OnNavListener {
                    override fun onNaviCancel() {
                        mapboxMap.navigationControl.finishSimulationNavigation()
                        navigationUIController?.hideNaviView()
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

    private fun searchAlongTheWay(name: String) {
        val routeInfo = routesInfo?.getRoutesInfo()
        routeInfo.let {
            routeInfo?.get(0)?.let { it1 -> getRouteGeometry(it1, name) }
        }
    }

    private fun getRouteGeometry(routeInfo: RouteInfo, name: String) {
        val lngLats = routeInfo.getRouteInfoPts()
        val pathLine = lngLats.toTypedArray();
        val multiPolygon = mapboxMap.navigationControl.bufferOp(pathLine, 50.0)
        val firstPolygon = multiPolygon.polygons()[0];
        val jsonObject = JSONObject(firstPolygon.toJson());
        val coordinates = jsonObject.getJSONArray("coordinates");
        requestAlongWay(name, null, coordinates.toString())
    }

    private fun handleAlongWaySearchResult(resultList: List<AlongWayPoiModel>) {
        val markerOptionsList: MutableList<MarkerOptions> = ArrayList()
        val formatter = DecimalFormat("#.#####")
        for (m in resultList) {
            val latLng = LatLng(m.lat(), m.lng())
            markerOptionsList.add(
                MarkerOptions()
                    .position(latLng)
                    .title(latLng.toString())
                    .snippet(formatter.format(latLng.latitude) + ", " + formatter.format(latLng.longitude))
            )
            mapboxMap.addMarkers(markerOptionsList)
        }
    }

    @SuppressLint("LogNotTimber")
    private fun requestAlongWay(name: String, location: AlongWaySearchReq.Location?, polygonJson: String) {
        val tokenModel = PreferenceStorageUtils.getAccessToken("");
        val tokenType = tokenModel?.token_type()
        val token = tokenModel?.access_token()
        val auth = "$tokenType $token"

        val req : AlongWaySearchReq? = if(location == null) {
            AlongWaySearchReq(DeviceUtils.getAndroidID(), name, polygonJson)
        } else {
            AlongWaySearchReq(DeviceUtils.getAndroidID(), name, polygonJson)
        }

        Log.i(TAG, "requestAlongWay,imei: ${DeviceUtils.getAndroidID()}, name: ${name}, polygon: $polygonJson")

        GomapAlongWaySearch.builder().authorization(auth).alongWaySearchReq(req).build().enqueueCall(object : Callback<HttpResponse<MoreResponse<AlongWayPoiModel>>> {
            @SuppressLint("LogNotTimber")
            override fun onResponse(
                call: Call<HttpResponse<MoreResponse<AlongWayPoiModel>>>,
                response: Response<HttpResponse<MoreResponse<AlongWayPoiModel>>>
            ) {
                Log.i(TAG, "code: ${response.code()}, msg: ${response.message()}");
                if (response.isSuccessful) {
                    val list = response.body()?.data?.list
                    Log.i(TAG, "data: ${response.body()?.data}");
                    if (list != null) {
                        handleAlongWaySearchResult(list)
                    }
                }
            }

            override fun onFailure(
                call: Call<HttpResponse<MoreResponse<AlongWayPoiModel>>>,
                t: Throwable
            ) {
                Log.i(TAG, "on failure: " + t.message);
            }
        });

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
        finish()
    }

}
