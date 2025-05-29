package com.gianghv.utils

import android.content.Context
import android.content.SharedPreferences

class AdsConfigUtils(context: Context) {
    companion object {
        /*Value Firebase config
        * Lưu ý các giá trị tên trong dấu suource phải define đúng theo tên trên file config*/


        /*....define const theo config tương tự như key bên dưới....*/
        const val SHOW_INTER_ADS = "show_inter_ads"

        const val banner_splash_status = "banner_splash_status"
        const val banner_splash_mrec_status = "banner_splash_mrec_status"
        const val banner_intro_status = "banner_intro_status"
        const val banner_home_status = "banner_home_status"

        const val native_language_status = "native_language_status"
        const val native_intro_status = "native_intro_status"
        const val native_full_status = "native_full_status"
        const val native_crank_status = "native_crank_status"
        const val native_downloading_status = "native_downloading_status"
        const val native_crank_done_status = "native_crank_done_status"
        const val native_history_status = "native_history_status"
        const val native_wallpaper_status = "native_wallpaper_status"
        const val native_detail_wallpaper_status = "native_detail_wallpaper_status"
        const val native_firescreen_status = "native_firescreen_status"
        const val native_select_weapon_status = "native_select_weapon_status"
        const val native_setting_status = "native_setting_status"

        const val inter_splash_status = "inter_splash_status"
        const val inter_resume_status = "inter_resume_status"
        const val inter_getstart_status = "inter_getstart_status"
        const val inter_crankeffect_back_status = "inter_crankeffect_back_status"
        const val inter_done_status = "inter_done_status"
        const val inter_history_back_status = "inter_history_back_status"
        const val inter_wallpaper_status = "inter_wallpaper_status"
        const val inter_unlock_status = "inter_unlock_status"
        const val inter_weapon_status = "inter_weapon_status"



        /*..................*/


        /*Enum value status theo vị trí quảng cáo, check trước khi show ads ở bất kỳ vị trí nào*/
        const val ADMOB = 1
        const val MAX = 2
        const val OFF = 0

        /* Giá trị mặc định nếu không request được remote config */
        const val DEFAULT_STATUS_ADS = ADMOB
        const val DEFAULT_TIME_BANNER = 3000

        const val TIME_CLOSED_INTER = "time_closed_inter"

    }


    private var sharedPreferences: SharedPreferences =
        context.getSharedPreferences("AdsConfigUtils", Context.MODE_PRIVATE)

    fun putStatusAdsNetworking(key: String, value: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.apply()
    }

    fun getStatusAdsPosition(key: String, def: Int = ADMOB): Int =
        sharedPreferences.getInt(key, def)

    fun getStatusAdsNetworking(key: String): Int =
        sharedPreferences.getInt(key, DEFAULT_STATUS_ADS)

    fun getTimeAdsNetworking(key: String): Int =
        sharedPreferences.getInt(key, DEFAULT_TIME_BANNER)

    fun removeKey(key: String) {
        sharedPreferences.edit().remove(key).apply()
    }
}