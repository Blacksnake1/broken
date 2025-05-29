package com.nekosoft.brokenglass.extention

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.util.Log
import android.widget.FrameLayout
import androidx.fragment.app.FragmentActivity
import com.brokenscreen.prankapp.crack.screen.BuildConfig
import com.brokenscreen.prankapp.crack.screen.R
import com.gianghv.DialogLoadingAds
import com.gianghv.admob.AdaptiveBannerManager
import com.gianghv.admob.AppOpenAdManager
import com.gianghv.admob.BaseNativeAdView
import com.gianghv.admob.InterstitialPreloadAdManager
import com.gianghv.admob.InterstitialSingleReqAdManager
import com.gianghv.admob.MrecBannerManager
import com.gianghv.admob.NativeAdsDialogManager
import com.gianghv.admob.NativeAdsManager
import com.gianghv.admob.RewardAdsManager
import com.gianghv.max.BaseMaxNativeAdView
import com.gianghv.max.MaxAdaptiveBannerManager
import com.gianghv.max.MaxAppOpenAdManager
import com.gianghv.max.MaxInterstitialSingleReqAdManager
import com.gianghv.max.MaxNativeAdLargeView
import com.gianghv.max.MaxNativeAdsManager
import com.gianghv.max.MaxRewardAdsManager
import com.gianghv.utils.AdsConfigUtils
import com.gianghv.utils.AdsConfigUtils.Companion.ADMOB
import com.gianghv.utils.AdsConfigUtils.Companion.MAX
import com.gianghv.utils.ConstantsAds
import com.gianghv.utils.ConstantsAds.adsClicked
import com.gianghv.utils.ConstantsAds.isLoadingInterAds
import com.gianghv.utils.Utils
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.admanager.AdManagerAdView
import com.nekosoft.brokenglass.App
import com.nekosoft.brokenglass.customView.dialog.DialogNativeFull
import com.nekosoft.brokenglass.extention.ContextExt.changeStatusBarColor
import com.nekosoft.brokenglass.extention.ContextExt.getAdsNetworking
import com.nekosoft.brokenglass.extention.ContextExt.isOnline
import com.nekosoft.brokenglass.utils.canShowInter
import com.nekosoft.brokenglass.utils.setTimeAdsClose

fun FragmentActivity.requestInterFunction(
    status: String,
    inter: InterstitialPreloadAdManager,
    onAdLoader: ((MaxInterstitialSingleReqAdManager?) -> Unit)?,
    onAdLoadFail: (() -> Unit)?,
    onAdLoading: (() -> Unit)?
) {
    if (inter.isAdAvailable()) {
        onAdLoader?.invoke(null)
        return
    }
    runOnUiThread {
        if (getAdsNetworking(status) == ADMOB) {
            inter.loadAds(onAdLoader = {
                onAdLoader?.invoke(null)
            }, onAdLoadFail = {
                onAdLoadFail?.invoke()
            }, onAdLoading = {
                onAdLoading?.invoke()
            })
        } else if (getAdsNetworking(status) == MAX) {
            val maxInterstitialSingleReqAdManager =
                MaxInterstitialSingleReqAdManager(this, BuildConfig.max_inter)
            maxInterstitialSingleReqAdManager.requestAds(onLoadAdSuccess = {
                onAdLoader?.invoke(maxInterstitialSingleReqAdManager)
            }, onAdLoadFail = {
                onAdLoadFail?.invoke()
            })
        } else {
            onAdLoadFail?.invoke()
        }
    }

}


fun FragmentActivity.requestAndShowAdsInterFunction(
    status: String,
    inter: InterstitialPreloadAdManager,
    dialogLoadingAds: DialogLoadingAds?,
    showDialogLoading: Boolean = true,
    alwaysDisplay: Boolean = false,
    actionDone: ((Int?) -> Unit)?,
    actionFail: ((Int?) -> Unit)?,
) {
    var mDialogLoadingAds = dialogLoadingAds
    if (mDialogLoadingAds == null && showDialogLoading) {
        mDialogLoadingAds = DialogLoadingAds(this)
    }

    if (getAdsNetworking(status) == ADMOB) {
        if (!canShowInter(this@requestAndShowAdsInterFunction) && !alwaysDisplay) {
            actionDone?.invoke(ADMOB)
            return
        }

        if (inter.isAdAvailable()) {
            ConstantsAds.isShowAdsFull = true
            launchWhenResumed {
                adsClicked = false
                inter.showAds(this@requestAndShowAdsInterFunction, onAdClose = {
                    launchWhenResumed {
                        ConstantsAds.isShowAdsFull = false
                        mDialogLoadingAds?.dismissDialog()
                        setTimeAdsClose(this@requestAndShowAdsInterFunction)
                        actionDone?.invoke(ADMOB)
                    }
                }, onAdError = { adError ->
                    ConstantsAds.isShowAdsFull = false
                    actionFail?.invoke(ADMOB)
                    mDialogLoadingAds?.dismissDialog()
                }, onAdShowing = {
                    ConstantsAds.isShowAdsFull = true
                    changeStatusBarColor(R.color.black_1d1c1c)
                    mDialogLoadingAds?.dismissDialog()
                }, onAdClicked = {
                    adsClicked = true
                    ConstantsAds.isShowAdsFull = true
                })
            }
        } else {
            mDialogLoadingAds?.showDialog()
            if (isLoadingInterAds) {
                return
            }
            ConstantsAds.isShowAdsFull = true
            requestInterFunction(status, inter, onAdLoader = {
                launchWhenResumed {
                    adsClicked = false
                    inter.showAds(this@requestAndShowAdsInterFunction, onAdClose = {
                        ConstantsAds.isShowAdsFull = false
                        if (!adsClicked) {
                            mDialogLoadingAds?.dismissDialog()
                            setTimeAdsClose(this@requestAndShowAdsInterFunction)
                            actionDone?.invoke(ADMOB)
                        } else {
                            adsClicked = false
                        }
                    }, onAdError = { adError ->
                        ConstantsAds.isShowAdsFull = false
                        actionFail?.invoke(ADMOB)
                        mDialogLoadingAds?.dismissDialog()
                    }, onAdShowing = {
                        mDialogLoadingAds?.dismissDialog()
                        ConstantsAds.isShowAdsFull = true
                        changeStatusBarColor(R.color.black_1d1c1c)
                    }, onAdClicked = {
                        adsClicked = true
                        ConstantsAds.isShowAdsFull = false
                    })
                }
            }, onAdLoadFail = {
                ConstantsAds.isShowAdsFull = false
                mDialogLoadingAds?.dismissDialog()
                actionFail?.invoke(ADMOB)
            }, onAdLoading = {
                ConstantsAds.isShowAdsFull = true
                mDialogLoadingAds?.dismissDialog()
            })
        }

    } else if (getAdsNetworking(status) == AdsConfigUtils.MAX) {
        if (!canShowInter(this@requestAndShowAdsInterFunction) && !alwaysDisplay) {
            actionDone?.invoke(MAX)
            return
        }
        mDialogLoadingAds?.showDialog()
        ConstantsAds.isShowAdsFull = true
        requestInterFunction(status, inter, onAdLoader = {
            adsClicked = false
            launchWhenResumed {
                it?.show(onShowingAds = {
                    ConstantsAds.isShowAdsFull = true
                    mDialogLoadingAds?.dismissDialog()
                }, onShowAdsFinish = {
                    if (!adsClicked) {
                        ConstantsAds.isShowAdsFull = false
                        mDialogLoadingAds?.dismissDialog()
                        setTimeAdsClose(this@requestAndShowAdsInterFunction)
                        actionDone?.invoke(MAX)
                    } else {
                        adsClicked = false
                    }

                }, onAdsClicked = {
                    ConstantsAds.isShowAdsFull = false
                    adsClicked = true
                })
            }
        }, onAdLoadFail = {
            ConstantsAds.isShowAdsFull = false
            mDialogLoadingAds?.dismissDialog()
            actionFail?.invoke(MAX)
        }) {
            mDialogLoadingAds?.dismissDialog()
        }
    } else {
        ConstantsAds.isShowAdsFull = false
        mDialogLoadingAds?.dismissDialog()
        actionFail?.invoke(null)
    }
}

fun FragmentActivity.requestAndShowInter(
    startus: String,
    key: String,
    alwaysDisplay: Boolean? = true,
    dialogLoadingAds: DialogLoadingAds? = null,
    showLoading: Boolean = true,
    actionDone: ((Int?) -> Unit)?,
    actionFail: ((Int?) -> Unit)?,
    actionShowing: (() -> Unit)? = null
) {
    var mDialogLoadingAds = dialogLoadingAds
    if (mDialogLoadingAds == null && showLoading) {
        mDialogLoadingAds = DialogLoadingAds(this)
    }
    if (!canShowInter(this) && alwaysDisplay == false) {
        actionDone?.invoke(null)
        return
    }
    if (getAdsNetworking(startus) == ADMOB) {
        val interstitialSingleReqAdManager = InterstitialSingleReqAdManager(
            this,
            key,
        )
        mDialogLoadingAds?.showDialog()
        interstitialSingleReqAdManager.requestAds(
            onLoadAdSuccess = {
                launchWhenResumed {
                    interstitialSingleReqAdManager.showAds(
                        this@requestAndShowInter,
                        onAdClose = {
                            Log.d("extensionAds", "requestAndShowInter_358: ")
                            mDialogLoadingAds?.dismissDialog()
                            setTimeAdsClose(this@requestAndShowInter)
                            actionDone?.invoke(ADMOB)
                        }, onAdShowing = {
                            Log.d("extensionAds", "requestAndShowInter_364: ")
                            mDialogLoadingAds?.dismissDialog()
                            actionShowing?.invoke()
                        }, onAdClicked = {
                            adsClicked = true
                        }
                    )
                }

            }, onAdLoadFail = {
                ConstantsAds.isShowAdsFull = false
                mDialogLoadingAds?.dismissDialog()
                actionFail?.invoke(ADMOB)
            })
    } else if (getAdsNetworking(startus) == MAX) {
        dialogLoadingAds?.showDialog()
        val maxInterstitialSingleReqAdManager =
            MaxInterstitialSingleReqAdManager(this, BuildConfig.max_inter)
        maxInterstitialSingleReqAdManager.requestAds(onLoadAdSuccess = {
//            launchWhenResumed {
            maxInterstitialSingleReqAdManager.show(
                onShowingAds = {
                    mDialogLoadingAds?.dismissDialog()
                    actionShowing?.invoke()
                }, onShowAdsFinish = {
                    mDialogLoadingAds?.dismissDialog()
                    setTimeAdsClose(this@requestAndShowInter)
                    actionDone?.invoke(MAX)
                }, onAdsClicked = {
                    adsClicked = true
                }
            )
//            }
        }, onAdLoadFail = {
            ConstantsAds.isShowAdsFull = false
            mDialogLoadingAds?.dismissDialog()
            actionDone?.invoke(MAX)
        })
    } else {
        ConstantsAds.isShowAdsFull = false
        mDialogLoadingAds?.dismissDialog()
        actionDone?.invoke(null)
    }
}

fun FragmentActivity.requestAndShowBanner(
    startus: String,
    key: String,
    collapse: Boolean = false,
    view: FrameLayout,
    actionDone: (() -> Unit)? = null,
    actionFail: (() -> Unit)? = null,
    actionClicked: (() -> Unit)? = null
) {
    if (!isOnline()) {
        actionFail?.invoke()
        return
    }
    if (getAdsNetworking(startus) == ADMOB) {
        val adaptiveBannerManager = AdaptiveBannerManager(
            this, collapse, key
        )

        adaptiveBannerManager.loadBanner(view, onAdLoader = {
            actionDone?.invoke()
        }, onAdLoadFail = {
            view.goneExt()
            actionFail?.invoke()
        }, actionClicked = {
            Log.d("extensionAds", "_requestAndShowBanner_310: ")
            actionClicked?.invoke()
        })
    } else if (getAdsNetworking(startus) == MAX) {
        val maxAdaptiveBannerManager = MaxAdaptiveBannerManager(
            this, BuildConfig.max_banner
        )
        maxAdaptiveBannerManager.loadBanner(view, onAdLoader = {
            actionDone?.invoke()
        }, onAdLoadFail = {
            view.goneExt()
            actionFail?.invoke()
        })
    } else {
        view.goneExt()
        actionFail?.invoke()
    }
}

fun FragmentActivity.requestAndShowBannerMrect(
    startus: String,
    key: String,
    adContainerView: FrameLayout,
    actionDone: ((AdManagerAdView?) -> Unit)? = null,
    actionFail: (() -> Unit)? = null,
) {

    when (AdsConfigUtils(this).getStatusAdsPosition(startus)) {
        AdsConfigUtils.ADMOB -> {
            val adaptiveBannerV2Manager = MrecBannerManager(
                this, key
            )
            adaptiveBannerV2Manager.loadBanner(adContainerView, onAdLoader = {
                actionDone?.invoke(it)
            }, onAdLoadFail = {
                actionFail?.invoke()
            })
        }

        AdsConfigUtils.OFF -> {
            actionFail?.invoke()
        }
    }

}


fun FragmentActivity.requestAndShowNative(
    startus: String,
    key: String,
    timeOut: Long? = null,
    admobView: BaseNativeAdView? = null,
    maxView: BaseMaxNativeAdView? = null,
    actionDone: ((Any) -> Unit)? = null,
    actionFail: (() -> Unit)? = null,
    actionClicked: ((Boolean) -> Unit)? = null
) {
    Log.d("extensionAds", "requestAndShowNative_591: ")
    if (!isOnline()) {
        actionFail?.invoke()
        return
    }
    Log.d("extensionAds", "requestAndShowNative_596: ")
    val newTimeOut = timeOut ?: ConstantsAds.TIME_OUT

    when (getAdsNetworking(startus)) {
        ADMOB -> {
            admobView?.showShimmer(true)
            admobView?.visibleExt()
            maxView?.goneExt()

            val mNativeAdManager = NativeAdsManager(
                this,
                key,
                newTimeOut
            )
            mNativeAdManager.loadAds(onLoadSuccess = {
                Log.d("extensionAds", "requestAndShowNative_615: ")
                if (admobView == null && maxView == null) {
                    actionDone?.invoke(it)
                    return@loadAds
                }
                Log.d("extensionAds", "requestAndShowNative_620: ")
                admobView?.showShimmer(false)
                admobView?.setNativeAd(it)
                actionDone?.invoke(it)
            }, onLoadFail = {
                Log.d("extensionAds", "requestAndShowNative_625: ")
                admobView?.goneExt()
                maxView?.goneExt()
                actionFail?.invoke()
            }, onAdsClicked = {
                Log.d("extensionAds", "_requestAndShowNative_377: ")
                actionClicked?.invoke(true)
            })
        }

        MAX -> {
            maxView?.showShimmer(true)
            admobView?.goneExt()
            maxView?.visibleExt()
            val nativeMaxAd = MaxNativeAdsManager(this, true, BuildConfig.max_native)
            nativeMaxAd.loadAds(onLoadSuccess = {
                if (admobView == null && maxView == null) {
                    it?.let { it1 -> actionDone?.invoke(it1) }
                    return@loadAds
                }
                maxView?.showShimmer(false)
                maxView?.removeAllViews()
                maxView?.addView(it)
                actionDone?.invoke(it!!)
            }, onLoadFail = {
                admobView?.goneExt()
                maxView?.goneExt()
                actionFail?.invoke()
            })
        }

        else -> {
            actionFail?.invoke()
        }
    }
}

fun FragmentActivity.requestAndShowInterHaveNativeFull(
    activity: Activity,
    startus: String,
    key: String,
    alwaysDisplay: Boolean? = false,
    dialogLoadingAds: DialogLoadingAds? = null,
    showLoading: Boolean = true,
    actionDone: (() -> Unit)?,
    actionFail: (() -> Unit)? = null,
    actionShowing: (() -> Unit)? = null
) {
    var actionGo = false
    if (!canShowInter(this) && alwaysDisplay == false) {
        actionDone?.invoke()
        return
    }
    ConstantsAds.isShowAdsFull = true

    val mDialogNativeFull = DialogNativeFull(activity, action = { item ->
        ConstantsAds.isShowAdsFull = false
        actionDone?.invoke()
    })
    mDialogNativeFull.loadAdNative()

    requestAndShowInter(
        startus,
        key,
        alwaysDisplay,
        dialogLoadingAds,
        showLoading,
        actionDone = {
            mDialogNativeFull.interPing(DialogNativeFull.INTER_CLOSE, null)
        },
        actionFail = {
            mDialogNativeFull.interPing(DialogNativeFull.INTER_FAIL, null)
        },
        actionShowing = {
            mDialogNativeFull.interPing(DialogNativeFull.INTER_SHOW_FULL, null)
        }
    )
}

fun Dialog.requestAndShowNativeOnDialog(
    context: Context,
    startus: String,
    key: String,
    timeOut: Long? = null,
    admobView: BaseNativeAdView? = null,
    maxView: MaxNativeAdLargeView? = null,
    actionDone: ((Any) -> Unit)? = null,
    actionFail: (() -> Unit)? = null,
    actionClicked: (() -> Unit)? = null
) {
    if (!Utils.isNetworkConnected(context)) {
        actionFail?.invoke()
        return
    }
    val newTimeOut = timeOut ?: ConstantsAds.TIME_OUT
    var setNativeInRecycleView = false
    if (admobView == null && maxView == null) {
        setNativeInRecycleView = true
    }
    when (AdsConfigUtils(getContext()).getStatusAdsNetworking(startus)) {
        ADMOB -> {
            admobView?.showShimmer(true)
            admobView?.visibleExt()
            maxView?.goneExt()
            val mNativeAdManager = NativeAdsDialogManager(
                context,
                key,
                newTimeOut,
            )
            mNativeAdManager.loadAds(onLoadSuccess = {
                if (setNativeInRecycleView) {
                    actionDone?.invoke(it)
                    return@loadAds
                }
                admobView?.showShimmer(false)
                admobView?.setNativeAd(it)
                actionDone?.invoke(it)
            }, onLoadFail = {
                admobView?.goneExt()
                actionFail?.invoke()
            }, onAdsClicked = {})
        }

        MAX -> {
            maxView?.showShimmer(true)
            admobView?.goneExt()
            maxView?.visibleExt()
            val nativeMaxAd = MaxNativeAdsManager(context, true, BuildConfig.max_native)
            nativeMaxAd.loadAds(onLoadSuccess = {
                maxView?.showShimmer(false)
                maxView?.removeAllViews()
                maxView?.addView(it)
                actionDone?.invoke(it!!)
            }, onLoadFail = {
                admobView?.goneExt()
                maxView?.goneExt()
                actionFail?.invoke()
            })
        }

        else -> {
            actionFail?.invoke()
        }
    }
}

fun FragmentActivity.requestReward(
    status: String,
    reward: RewardAdsManager,
    onLoadAdSuccess: (() -> Unit)? = null,
    onAdLoadFail: (() -> Unit)? = null,
    onAdLoading: (() -> Unit)? = null
) {
    ConstantsAds.isShowAdsFull = true
    if (reward.isRewardAdAvailable()) {
        onLoadAdSuccess?.invoke()
        return
    }
    runOnUiThread {
        if (getAdsNetworking(status) == ADMOB) {
            reward.requestAds(onLoadAdSuccess = {
                onLoadAdSuccess?.invoke()
            }, onAdLoadFail = {
                onAdLoadFail?.invoke()
            }, onAdLoading = {
                onAdLoading?.invoke()
            })
        } else if (getAdsNetworking(status) == MAX) {
            val rewardMax = MaxRewardAdsManager(
                this, BuildConfig.max_reward
            )
            rewardMax.requestAds(onAdLoader = {
                onLoadAdSuccess?.invoke()
            }, onAdLoadFail = {
                onAdLoadFail?.invoke()
            })
        } else {
            onAdLoadFail?.invoke()
        }
    }
}

fun FragmentActivity.requestAndShowReward(
    status: String,
    reward: RewardAdsManager,
    dialogLoadingAds: DialogLoadingAds?,
    showDialogLoading: Boolean = true,
    actionDone: (() -> Unit)? = null,
    actionEarn: (() -> Unit)? = null,
    actionFail: (() -> Unit)? = null,
) {
    if (!isOnline()) {
        actionFail?.invoke()
        return
    }
    var mDialogLoadingAds = dialogLoadingAds
    if (mDialogLoadingAds == null && showDialogLoading) {
        mDialogLoadingAds = DialogLoadingAds(this)
    }

    if (getAdsNetworking(status) == ADMOB) {
        if (reward.isRewardLoading) { // thời điểm được gọi nếu reward đang trong giai đoạn loading thì sẽ hiện dialogLoadingAds hoặc không. Nhưng sẽ return luôn
            return
        }
        ConstantsAds.isShowAdsFull = true
        if (reward.isRewardAdAvailable()) {
            launchWhenResumed {
                adsClicked = false
                reward.showRewardAds(
                    this@requestAndShowReward,
                    object : RewardAdsManager.OnShowRewardAdListener {
                        override fun onShow() {
                            changeStatusBarColor(R.color.black_1d1c1c)
                            mDialogLoadingAds?.dismissDialog()
                        }

                        override fun onShowRewardSuccess() {
                            launchWhenResumed {
                                ConstantsAds.isShowAdsFull = false
                                if (!adsClicked) {
                                    actionDone?.invoke()
                                } else {
                                    adsClicked = false
                                }
                            }
                        }

                        override fun onUserEarnReward() {
                            mDialogLoadingAds?.dismissDialog()
                            actionEarn?.invoke()
                        }

                        override fun onShowRewardFailed(string: AdError?) {
                            ConstantsAds.isShowAdsFull = false
                            mDialogLoadingAds?.dismissDialog()
                            actionFail?.invoke()
                        }

                        override fun onClicked() {
                            adsClicked = true
                        }
                    }
                )
            }
        } else {
            if (showDialogLoading) mDialogLoadingAds?.showDialog()
            requestReward(status, reward, onLoadAdSuccess = {
                reward.showRewardAds(
                    this@requestAndShowReward,
                    object : RewardAdsManager.OnShowRewardAdListener {
                        override fun onShow() {
                            changeStatusBarColor(R.color.black_1d1c1c)
                            mDialogLoadingAds?.dismissDialog()
                        }

                        override fun onShowRewardSuccess() {
                            launchWhenResumed {
                                ConstantsAds.isShowAdsFull = false
                                if (!adsClicked) {
                                    mDialogLoadingAds?.dismissDialog()
                                    actionDone?.invoke()
                                } else {
                                    adsClicked = false
                                }
                            }
                        }

                        override fun onUserEarnReward() {
                            mDialogLoadingAds?.dismissDialog()
                            actionEarn?.invoke()
                        }

                        override fun onShowRewardFailed(string: AdError?) {
                            ConstantsAds.isShowAdsFull = false
                            adsClicked = false
                            mDialogLoadingAds?.dismissDialog()
                            actionFail?.invoke()
                        }

                        override fun onClicked() {
                            adsClicked = true
                        }
                    })
            }, onAdLoadFail = {
                mDialogLoadingAds?.dismissDialog()
                ConstantsAds.isShowAdsFull = false
                actionFail?.invoke()
            }, onAdLoading = {})

        }
    } else if (getAdsNetworking(status) == MAX) {
        mDialogLoadingAds?.showDialog()
        val rewardMax = MaxRewardAdsManager(
            this, BuildConfig.max_reward
        )
        ConstantsAds.isShowAdsFull = true
        rewardMax.requestAds(onAdLoader = {
            this.launchWhenResumed {
                rewardMax.show(
                    this@requestAndShowReward,
                    object : MaxRewardAdsManager.OnShowRewardAdListener {
                        override fun onShowRewardSuccess() {}

                        override fun onShowAdsFailed() {
                            ConstantsAds.isShowAdsFull = true
                            adsClicked = false
                            actionFail?.invoke()
                        }

                        override fun onShowRewardCompleteDone() {
                            mDialogLoadingAds?.dismissDialog()
                            actionEarn?.invoke()
                        }

                        override fun onCloseRewardCompletedDone() {
                            ConstantsAds.isShowAdsFull = false
                            if (!adsClicked) {
                                mDialogLoadingAds?.dismissDialog()
                                actionDone?.invoke()
                            } else {
                                adsClicked = false
                            }
                        }

                        override fun onAdsClicked() {
                            adsClicked = true
                        }
                    })
            }
        }, onAdLoadFail = {
            ConstantsAds.isShowAdsFull = true
            actionFail?.invoke()
        })
    }
}

fun FragmentActivity.requestAndShowOpenAds(
    status: String,
    key: String,
    dialogLoadingAds: DialogLoadingAds?,
    showDialogLoading: Boolean = true,
    actionDone: (() -> Unit)? = null,
    actionFail: (() -> Unit)? = null,
) {
    if (!isOnline()) {
        actionFail?.invoke()
        return
    }
    var mDialogLoadingAds = dialogLoadingAds
    if (mDialogLoadingAds == null && showDialogLoading) {
        mDialogLoadingAds = DialogLoadingAds(this)
    }
    mDialogLoadingAds?.showDialog()
    if (getAdsNetworking(status) == ADMOB) {
        val openAds = AppOpenAdManager(this, key)
        openAds.loadAd(onAdLoader = {
            launchWhenResumed {
                openAds.showAdIfAvailable(
                    this@requestAndShowOpenAds,
                    object : AppOpenAdManager.OnShowAdCompleteListener {
                        override fun onShowAdComplete() {
                            mDialogLoadingAds?.dismissDialog()
                            actionDone?.invoke()
                        }
                    })
            }
        }, onAdLoadFail = {
            mDialogLoadingAds?.dismissDialog()
            actionDone?.invoke()
        })

    } else if (getAdsNetworking(status) == MAX) {
        val openAdsMax = MaxAppOpenAdManager(this, key)
        openAdsMax.loadAd(onAdLoader = {
            launchWhenResumed {
                openAdsMax.showAdIfAvailable(object : MaxAppOpenAdManager.OnShowAdCompleteListener {
                    override fun onShowAdComplete() {
                        mDialogLoadingAds?.dismissDialog()
                        actionDone?.invoke()
                    }

                })
            }
            mDialogLoadingAds?.dismissDialog()
            actionDone?.invoke()
        }, onAdLoadFail = {
            mDialogLoadingAds?.dismissDialog()
            actionDone?.invoke()
        })
    }

}



