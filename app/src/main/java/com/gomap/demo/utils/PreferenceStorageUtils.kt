package com.gomap.demo.utils

import com.blankj.utilcode.util.SPUtils

const val SP_NAME = "gomap"

object PreferenceStorageUtils {

    private const val API_KEY = "api_key"

    private fun getDefaultPreferenceUtils(spName:String?= SP_NAME): SPUtils {
        return SPUtils.getInstance(spName)
    }
    fun saveApiKeyData(str: String?) {
        getDefaultPreferenceUtils().put(API_KEY, str)
    }

    fun getApiKeyData(defaultData: String?): String? {
        return getDefaultPreferenceUtils().getString(API_KEY, defaultData)
    }
}