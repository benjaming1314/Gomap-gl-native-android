package com.gomap.demo.utils

import com.blankj.utilcode.util.SPUtils
import com.gomap.plugin.api.model.AccessTokenModel

const val SP_NAME = "gomap"

object PreferenceStorageUtils {

    private const val API_KEY = "api_key"
    private const val ACCESS_TOKEN = "access_token"

    private fun getDefaultPreferenceUtils(spName:String?= SP_NAME): SPUtils {
        return SPUtils.getInstance(spName)
    }
    fun saveApiKeyData(str: String?) {
        getDefaultPreferenceUtils().put(API_KEY, str)
    }

    fun getApiKeyData(defaultData: String?): String? {
        return getDefaultPreferenceUtils().getString(API_KEY, defaultData)
    }

    fun saveAccessToken(str: String?) {
        getDefaultPreferenceUtils().put(ACCESS_TOKEN, str)
    }

    fun getAccessToken(defaultData: String?): AccessTokenModel? {
        val jsonStr = getDefaultPreferenceUtils().getString(ACCESS_TOKEN, defaultData)
        return AccessTokenModel.fromJson(jsonStr)
    }
}