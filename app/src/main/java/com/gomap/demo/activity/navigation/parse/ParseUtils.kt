package com.gomap.demo.activity.navigation.parse

import com.gomap.sdk.navigation.bean.RoutesInfo
import com.gomap.sdk.utils.DistanceUtils
import kotlin.math.ceil

/**
 * @description
 * @author dong.jin@g42.ai
 * @createtime 2022/9/26
 */
object ParseUtils {

    fun covertRouteResult(p0: RoutesInfo): String {
        val builder = StringBuilder()
        p0?.routesInfo?.forEach {
            builder.append("路线总长：")
                .append(DistanceUtils.reformatTotalSurplusDistance(it.distance) + ",")
                .append("所需时间：").append(formatRoutingTime(it.time) + ",")
                .append("红绿灯个数：").append(it.trafficLightCount).append("\n")
        }
        return builder.toString()
    }

    private fun formatRoutingTime(seconds: Double): String? {
        val totalMin = ceil(seconds / 60.0)
        var minutes = totalMin % 60
        val hours = (totalMin / 60).toInt()
        if (seconds <= 60) {
            minutes = 1.0
        }
        return "$hours h $minutes min"
    }

}