package com.nekosoft.brokenglass.base

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContract
import com.brokenscreen.prankapp.crack.screen.BuildConfig

class StoragePermissionResultContract : ActivityResultContract<Int, Int>() {

    override fun createIntent(context: Context, input: Int): Intent {
        return Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", BuildConfig.APPLICATION_ID, null)
        }
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Int {
        return resultCode
    }
}