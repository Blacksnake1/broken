package com.gianghv.utils

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustConfig
import com.adjust.sdk.LogLevel
import com.applovin.sdk.AppLovinMediationProvider
import com.applovin.sdk.AppLovinSdk
import com.gianghv.libads.R
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.ump.ConsentInformation
import com.google.android.ump.UserMessagingPlatform


class NekoAdsLoader(var context: Application) {
    var cmpUtils = CMPUtils(context)
    private lateinit var googleMobileAdsConsentManager: GoogleMobileAdsConsentManager
    private val consentInformation: ConsentInformation =
        UserMessagingPlatform.getConsentInformation(context)

    val isPrivacyOptionsRequired: Boolean
        get() =
            consentInformation.privacyOptionsRequirementStatus ==
                    ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED

    fun initialize() {
        initAdjust()
        cmpUtils = CMPUtils(context)
        googleMobileAdsConsentManager = GoogleMobileAdsConsentManager.getInstance(context)
    }

    fun showConsentForm(
        activity: Activity,
        onInitSuccess: (() -> Unit),
        onDialogCMPShowing: (() -> Unit)
    ) {
        googleMobileAdsConsentManager.gatherConsent(activity,
            onDialogCMPShowing = {
                onDialogCMPShowing.invoke()
            },
            onCanShowAds = {
                initializeAdmobAndMax(onInitSuccess)
            }, onDisableAds = {
                onInitSuccess.invoke()
            })
    }

    private fun initializeAdmobAndMax(onInitSuccess: (() -> Unit)) {
        MobileAds.initialize(context) {
            val configuration =
                RequestConfiguration.Builder().setTestDeviceIds(ConstantsAds.testDevices()).build()
            MobileAds.setRequestConfiguration(configuration)
            val statusMap = it.adapterStatusMap
            for (adapterClass in statusMap.keys) {
                val status = statusMap[adapterClass]
                Log.e(
                    "NekoAdsLoader", String.format(
                        " NekoAdsLoader Mediation Adapter name: %s, Description: %s, Latency: %d, initializationState: %s",
                        adapterClass,
                        status!!.description,
                        status.latency,
                        status.initializationState
                    )
                )
            }
            Log.e("NekoAdsLoader", "initialize success")
        }
        initApplovinMediation()
        onInitSuccess.invoke()
    }

    fun showTestingMediation(activity: Activity) {
        MobileAds.openAdInspector(activity) {}
    }

    private fun initApplovinMediation() {
        AppLovinSdk.getInstance(context).mediationProvider = AppLovinMediationProvider.MAX
        AppLovinSdk.getInstance(context).initializeSdk {}
    }

    private fun initAdjust() {
        val config = AdjustConfig(
            context,
            context.getString(R.string.adjust_token_key),
            AdjustConfig.ENVIRONMENT_PRODUCTION
        )
        config.setLogLevel(LogLevel.WARN)
//        Adjust.onCreate(config)
        Adjust.initSdk(config)
        context.registerActivityLifecycleCallbacks(
            AdjustLifecycleCallbacks()
        )
    }


    inner class AdjustLifecycleCallbacks : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, bundle: Bundle?) {}

        override fun onActivityStarted(activity: Activity) {
        }

        override fun onActivityResumed(activity: Activity) {
            Adjust.onResume()
        }

        override fun onActivityPaused(activity: Activity) {
            Adjust.onPause()
        }

        override fun onActivityStopped(activity: Activity) {}

        override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}

        override fun onActivityDestroyed(activity: Activity) {}
    }

}