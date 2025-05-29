package com.nekosoft.brokenglass.base

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.viewbinding.ViewBinding
import com.nekosoft.brokenglass.customView.dialog.DialogPermission
import com.nekosoft.brokenglass.utils.isPermissionGranted
import com.nekosoft.brokenglass.extention.sdkEqualOrGreaterThan

abstract class BaseStoragePermissionActivity<viewBinding : ViewBinding> :
    BaseActivity<viewBinding>() {
    private var countRequest: Int? = null
    private val requestStoragePermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your app.
                onStorageGranted()
            } else {

                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied.
                if (!shouldShowRequestPermissionRationale(
                        getStoragePermissionString()
                    )
                ) {
                    // denied without never show again

                } else {
                    // denied never show again
                    //                    countRequest = sharedPreferences?.getInt("countRequest", 0)
//                    countRequest = countRequest!! + 1
//                    if (countRequest!! >= 2) {
                    showDialogRequestStoragePermissionFromSettings()
//                    } else {
//                      }
//
                }
                countRequest?.let { sharedPreferences?.setInt("countRequest", it) }
            }
        }

    private var storageSettingContract =
        registerForActivityResult(StoragePermissionResultContract()) {
            if (isPermissionGranted(this, getStoragePermissionString())) {
                onStorageGranted()
            }
        }

    open fun onStorageGranted() {}

    protected fun hasStoragePermission(): Boolean {
        return isPermissionGranted(this, getStoragePermissionString())
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
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                }
            })
            .build(this)
        dialog.show()
    }


    override fun onDestroy() {
        storageSettingContract.unregister()
        requestStoragePermissionLauncher.unregister()
        super.onDestroy()
    }

    private fun getStoragePermissionString(): String =
        if (sdkEqualOrGreaterThan(Build.VERSION_CODES.TIRAMISU)) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        }
}