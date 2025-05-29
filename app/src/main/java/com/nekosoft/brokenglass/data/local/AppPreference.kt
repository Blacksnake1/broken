package com.nekosoft.brokenglass.data.local

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import javax.inject.Inject

class AppPreference @Inject constructor(context: Context) {
    var sharedPreferences: SharedPreferences? = null


    init {
        sharedPreferences = context.getSharedPreferences("NEKOSOFT", Context.MODE_PRIVATE)
    }

    companion object {
        private var sInstance: AppPreference? = null
        fun getInstance(context: Context): AppPreference? {
            if (sInstance == null) {
                sInstance = AppPreference(context.applicationContext)
            }
            return sInstance
        }

    }

    fun setBoolean(str: String?, z: Boolean) {
        sharedPreferences!!.edit().putBoolean(str, z).apply()
    }

    fun getBoolean(str: String?, z: Boolean): Boolean {
        return sharedPreferences!!.getBoolean(str, z)
    }

    fun getString(str: String?, defauValue: String): String? {
        return sharedPreferences?.getString(str, defauValue)
    }

    fun setString(str: String?, str2: String?) {
        sharedPreferences?.edit()?.putString(str, str2)?.apply()
    }

    fun getLong(str: String?, defauValue: Long): Long {
        return sharedPreferences!!.getLong(str, defauValue)
    }

    fun setLong(str: String?, str2: Long) {
        sharedPreferences!!.edit().putLong(str, str2).apply()
    }

    fun setInt(str: String?, i: Int) {
        sharedPreferences!!.edit().putInt(str, i).apply()
    }

    fun getInt(str: String?, defauValue: Int = 0): Int {
        return sharedPreferences!!.getInt(str, defauValue)
    }

    fun removeString(str: String?) {
        sharedPreferences!!.edit().remove(str).apply()
    }


    private fun checkForNullKey(key: String?) {
        if (key == null) {
            throw NullPointerException()
        }
    }

    fun clearPreference(key: String) {
        sharedPreferences?.edit()?.remove(key)?.apply()
    }


    /**
     * Used to retrieve object from the Preferences.
     *
     * @param key Shared Preference key with which object was saved.
     **/
//    inline fun <reified T> getObject(key: String): T? {
//        //We read JSON String which was saved.
//        val value = sharedPreferences?.getString(key, null)
//        //JSON String was found which means object can be read.
//        //We convert this JSON String to model object. Parameter "c" (of
//        //type Class < T >" is used to cast.
//        try {
//            return GsonBuilder().create().fromJson(value, T::class.java)
//        }catch (e:Throwable){
//            return null
//        }
//    }

    /** Save object to Preferences
     *
     * implement Gson library for use
     * implementation 'com.google.code.gson:gson:2.10' */
    internal inline fun <reified T> saveObjectToSharePreference(key: String, data: T) {
        val gson = Gson()
        val json = gson.toJson(data)
        sharedPreferences!!.edit()?.putString(key, json)?.apply()
    }

    internal inline fun <reified T> getObjectFromSharePreference(key: String): T? {
        val serializedObject: String? = sharedPreferences?.getString(key, null)
        if (serializedObject != null) {
            val gson = Gson()
            val type: Type = object : TypeToken<T?>() {}.type
            return gson.fromJson(serializedObject, type)
        }
        return null
    }


    /** Save list to Preferences
     *
     * implement Gson library for use*/
    inline fun <reified T> saveListToSharePreference(key: String, list: MutableList<T>) {
        val gson = Gson()
        val json = gson.toJson(list)
        sharedPreferences?.edit()?.putString(key, json)?.apply()
    }

    /** Get list from Preferences
     *
     * implement Gson library for use*/

    inline fun <reified T> getListFromSharePreference(key: String): MutableList<T> {
        var arrayItems = mutableListOf<T>()
        val serializedObject: String? = sharedPreferences?.getString(key, null)
        if (serializedObject != null) {
            val gson = Gson()
            val type: Type = object : TypeToken<MutableList<T?>?>() {}.type
            arrayItems = gson.fromJson(serializedObject, type)
        }
        return arrayItems
    }
}