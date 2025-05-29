package com.gianghv.utils



//class InAppUpdate(private val parentActivity: Activity) {
//    private val appUpdateManager: AppUpdateManager?
//    private var updateType = IMMEDIATE
//
//    init {
//        appUpdateManager = AppUpdateManagerFactory.create(parentActivity)
//    }
//
//    fun checkForAppUpdate(affterUpdate: () -> Unit) {
//        isInit = false
////        if (updateType == FLEXIBLE) {
//        appUpdateManager?.registerListener(installStateUpdatedListener)
////        }
//        appUpdateManager!!.appUpdateInfo.addOnSuccessListener { info ->
//            val isUpdateAvailable =
//                info.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
//            val isUpdateAllowed = when (updateType) {
//                FLEXIBLE -> info.isFlexibleUpdateAllowed
//                IMMEDIATE -> info.isImmediateUpdateAllowed
//                else -> false
//            }
//            if (isUpdateAvailable && isUpdateAllowed) {
//                try {
//                    appUpdateManager.startUpdateFlowForResult(
//                        info,
//                        updateType,
//                        parentActivity,
//                        MY_REQUEST_CODE
//                    )
//                } catch (e: IntentSender.SendIntentException) {
//                    throw RuntimeException(e)
//                }
//            } else {
//                if (!isInit) {
//                    isInit = true
//                    affterUpdate.invoke()
//                }
//            }
//        }.addOnFailureListener {
//            it.printStackTrace()
//            if (!isInit) {
//                isInit = true
//                affterUpdate.invoke()
//            }
//        }.addOnCanceledListener {
//
//        }
////        appUpdateManager.registerListener(stateUpdatedListener)
//    }
//
//    private val installStateUpdatedListener = InstallStateUpdatedListener { state ->
//        if (state.installStatus() == DOWNLOADED) {
//            Toast.makeText(
//                parentActivity, "Download successful. Restart app in 5 seconds", Toast.LENGTH_SHORT
//            ).show()
//            runBlocking {
//                delay(5000)
//                appUpdateManager?.completeUpdate()
//            }
//        }
//    }
//
//    fun onActivityResult(requestCode: Int, resultCode: Int, evenshow: (Int) -> Unit) {
//        if (requestCode == MY_REQUEST_CODE) {
//            if (resultCode == Activity.RESULT_CANCELED) {
//                evenshow(0)
//                runBlocking {
//                    delay(3000)
//                    parentActivity.finishAffinity()
//                }
//            } else if (resultCode == RESULT_IN_APP_UPDATE_FAILED) {
//                evenshow(-1)
//                runBlocking {
//                    delay(3000)
//                    parentActivity.finishAffinity()
//                }
//            }
//        }
//    }
//
//    fun actionForResume() {
//        if (updateType == IMMEDIATE) {
//            appUpdateManager?.appUpdateInfo?.addOnSuccessListener { info: AppUpdateInfo ->
//                if (info.updateAvailability() == DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
//                    appUpdateManager.startUpdateFlowForResult(
//                        info, updateType, parentActivity, MY_REQUEST_CODE
//                    )
//                }
////            if (info.installStatus() == InstallStatus.DOWNLOADED) {
////                popupSnackBarForCompleteUpdate()
////            }
//            }
//        }
//    }
//
//
//    fun onDestroy() {
//        appUpdateManager?.unregisterListener(installStateUpdatedListener)
//    }
//
//    companion object {
//        var isInit = false
//        val MY_REQUEST_CODE = 500
//    }
//}