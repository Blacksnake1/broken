package com.nekosoft.brokenglass.base

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.viewbinding.ViewBinding
import com.nekosoft.brokenglass.customView.dialog.DialogPermission
import com.nekosoft.brokenglass.data.local.AppPreference
import com.nekosoft.brokenglass.utils.isPermissionGranted
import com.nekosoft.brokenglass.extention.sdkEqualOrGreaterThan

abstract class BaseStoragePermissionFragment<viewBinding : ViewBinding> :
    BaseFragment<viewBinding>() {
    private val sharedPreferences = AppPreference.getInstance(requireContext())
    private var countRequest: Int? = null
    private val requestStoragePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your app.
                onStorageGranted()
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied.
                activity?.let {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(
                            it,
                            getStoragePermissionString()
                        )
                    ) {
                        // denied without never show again
                    } else {
                        // denied never show again

//                        countRequest = sharedPreferences?.getInt("countRequest", 0)
//                        countRequest = countRequest!! + 1
//                        if (countRequest!! >= 2) {
                        showDialogRequestStoragePermissionFromSettings()
//                        } else {
//
//                        }
                    }
                }
//                countRequest?.let { sharedPreferences?.setInt("countRequest", it) }
            }

        }
    private var storageSettingContract =
        registerForActivityResult(StoragePermissionResultContract()) {
//            App.getInstance().returnSplash = false
            if (isPermissionGranted(context, getStoragePermissionString())) {
                onStorageGranted()
            }
        }

    open fun onStorageGranted() {}

    protected fun hasStoragePermission(): Boolean {
        return isPermissionGranted(context, getStoragePermissionString())
    }

    protected fun requestStoragePermission() {
        requestStoragePermissionLauncher.launch(getStoragePermissionString())
    }

    fun showDialogRequestStoragePermissionFromSettings() {
        val dialog = DialogPermission.Builder()
            .setOnClickListener(object : DialogPermission.Builder.OnClickDialog {
                override fun onCancel() {

                }

                override fun onOK() {
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts("package", activity?.packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }
            })
            .build(requireContext())
        dialog.show()
    }

    override fun onDestroyView() {
        storageSettingContract.unregister()
        requestStoragePermissionLauncher.unregister()
        super.onDestroyView()
    }

    private fun getStoragePermissionString(): String =
        if (sdkEqualOrGreaterThan(Build.VERSION_CODES.TIRAMISU)) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        }
}