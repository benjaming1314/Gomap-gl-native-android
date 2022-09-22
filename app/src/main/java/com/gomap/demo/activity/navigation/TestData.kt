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
        spoi.setName("")
        spoi.setPt(LngLat(54.4042004, 24.4866095))
        val epoi = Poi()
        epoi.setName("")
        epoi.setPt(LngLat(54.378343616053456, 24.4945967220051))
        val pois = arrayOfNulls<Poi>(2)
        pois[0] = spoi
        pois[1] = epoi
        return pois
    }

}