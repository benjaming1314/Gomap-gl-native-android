package com.gomap.demo.activity.navigation

import com.gomap.sdk.navigation.bean.LngLat
import com.gomap.sdk.navigation.bean.Poi

/**
 * @description
 * @author dong.jin@g42.ai
 * @createtime 2022/9/22
 */
object TestData {
    fun get(): Array<Poi?> {
        val spoi = Poi()
        spoi.setName("Start Point")
        spoi.setPt(LngLat(54.4042004, 24.4866095))
        val epoi = Poi()
        epoi.setName("End Point")
        epoi.setPt(LngLat(54.378343616053456, 24.4945967220051))
        val pois = arrayOfNulls<Poi>(2)
        pois[0] = spoi
        pois[1] = epoi
        return pois
    }


    fun getMore(): Array<Poi> {
        val spoi = Poi()
        spoi.setName("Start Point")
        spoi.setPt(LngLat(54.4042004, 24.4866095))

        val stop1 = Poi()
        stop1.setName("Stop1 Point")
        stop1.setPt(LngLat(54.4142004, 24.4866095))

        val stop2 = Poi()
        stop2.setName("Stop2 Point")
        stop2.setPt(LngLat(54.4342004, 24.4966095))

        val epoi = Poi()
        epoi.setName("End Point")
        epoi.setPt(LngLat(54.378343616053456, 24.4945967220051))

        return arrayOf<Poi>(spoi,stop1,stop2,epoi)
    }

}