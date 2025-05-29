package com.gianghv.utils

import android.app.Activity
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform

/*
 * @author: GiangFpt191195@gmail.com
 * create: 19/12/2023
 */
class GoogleMobileAdsConsentManager private constructor(context: Context) {
    private val consentInformation: ConsentInformation =
        UserMessagingPlatform.getConsentInformation(context)
    private val cmpUtils: CMPUtils = CMPUtils(context)

    /** Helper variable to determine if the app can request ads. */
    val canRequestAds: Boolean
        get() = consentInformation.canRequestAds()

    /** Helper variable to determine if the privacy options form is required. */
    val isPrivacyOptionsRequired: Boolean
        get() = consentInformation.privacyOptionsRequirementStatus != ConsentInformation.PrivacyOptionsRequirementStatus.NOT_REQUIRED

    /**
     * Helper method to call the UMP SDK methods to request consent information and load/show a
     * consent form if necessary.
     */
    fun gatherConsent(
        activity: Activity,
        onDialogCMPShowing: (() -> Unit),
        onCanShowAds: (() -> Unit),
        onDisableAds: (() -> Unit),
    ) {
//        Test debug
//        val debugSettings = ConsentDebugSettings.Builder(activity)
//            .setDebugGeography(ConsentDebugSettings.DebugGeography.DEBUG_GEOGRAPHY_EEA)
//            .addTestDeviceHashedId("9BCE4BB076DC0E1FCF486C8D0234E853")
//            .build()
//        val params =
//            ConsentRequestParameters.Builder().setConsentDebugSettings(debugSettings).build()

        /*Release*/
        val params = ConsentRequestParameters.Builder().setTagForUnderAgeOfConsent(false).build()
        consentInformation.requestConsentInfoUpdate(activity, params, {
            if (isPrivacyOptionsRequired) {
                if (!cmpUtils.canShowAds()) {
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (!activity.isFinishing && !activity.isDestroyed){
                            UserMessagingPlatform.showPrivacyOptionsForm(
                                activity
                            ) {
                                Log.e(
                                    "CMP",
                                    "showConsentFormInPlash FormError: ${it?.errorCode}: ${it?.message}"
                                )
                                if (cmpUtils.canShowAds()) {
                                    Log.e("CMP", "showConsentForm canShowAds")
                                    onCanShowAds.invoke()
                                } else {
                                    Log.e("CMP", "showConsentForm disableAds")
                                    onDisableAds.invoke()
                                }
                            }
                            onDialogCMPShowing.invoke()
                        }
                    }, 3000)
                } else {
                    onCanShowAds.invoke()
                }
            } else {
                Log.e("CMP", "Không nằm trong vùng Yêu cầu")
                onCanShowAds.invoke()
            }
        }, { requestConsentError ->
            if (isPrivacyOptionsRequired) {
                if (cmpUtils.canShowAds()) {
                    onCanShowAds.invoke()
                } else {
                    onDisableAds.invoke()
                }
            } else {
                onCanShowAds.invoke()
            }
        })
    }

    /** Helper method to call the UMP SDK method to show the privacy options form. */
    fun showPrivacyOptionsForm(
        activity: Activity,
        onConsentFormDismissedListener: ConsentForm.OnConsentFormDismissedListener
    ) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity, onConsentFormDismissedListener)
    }

    companion object {
        @Volatile
        private var instance: GoogleMobileAdsConsentManager? = null

        fun getInstance(context: Context) = instance ?: synchronized(this) {
            instance ?: GoogleMobileAdsConsentManager(context).also { instance = it }
        }
    }
}