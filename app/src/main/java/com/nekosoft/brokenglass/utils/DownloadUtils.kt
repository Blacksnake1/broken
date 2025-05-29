package com.nekosoft.brokenglass.utils

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.media.MediaScannerConnection
import android.net.Uri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy
import com.bumptech.glide.request.RequestOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File


object DownloadUtils {

  /*  fun downloadByPrDownload(
        context: Context,
        urlString: String,
        dir: File,
        fileName: String,
        imageData: ImageData?
    ): Int? {
        val latch = CountDownLatch(1)
        var b: Int? = null
        val filePath =
            GetLocalUtils.getThumPath(
                context,
                imageData?.imageId!!,
                getString(R.string.downloaded)
            )
                ?.let { File(it) }
        if (filePath?.exists() == true) {
            b = -1
            savetoShareprefr(context, imageData)
            latch.countDown()
        } else {
            PRDownloader.download(
                urlString,
                dir.path,
                fileName
            )
                .build()
                .setOnProgressListener {
                }
                .start(object : OnDownloadListener {
                    override fun onDownloadComplete() {
                        b = 1
                        savetoShareprefr(context, imageData)
                        scannerToMediaStore(context, File(dir, fileName))
                        latch.countDown()

                    }

                    override fun onError(error: com.downloader.Error?) {
                        b = 0
                        latch.countDown()
                    }
                })

        }
        latch.await()
        return b
    }*/

    /** lấy url trả về bitmap*/
    @SuppressLint("CheckResult")
    suspend fun getBitmapDirectly(
        context: Context?, url:String?
    ): Bitmap? = withContext(Dispatchers.IO) {
        if (context == null || url == null) return@withContext null
       val threshold= context.resources.displayMetrics.let {
            it.heightPixels.coerceAtLeast(it.widthPixels)
        }
        return@withContext Glide.with(context)
            .asBitmap()
            .load(url)
            .apply(
                RequestOptions().apply {
                    downsample(DownsampleStrategy.CENTER_INSIDE)
                    threshold.let {
                        override(it)
                    }
                }
            )
            .submit()
            .get()
    }

    @SuppressLint("CheckResult")
    suspend fun getBitmapFromDrawble(
        context: Context?, drawable:Int?
    ): Bitmap? = withContext(Dispatchers.IO) {
        if (context == null || drawable == null) return@withContext null
        val threshold= context.resources.displayMetrics.let {
            it.heightPixels.coerceAtLeast(it.widthPixels)
        }
        return@withContext Glide.with(context)
            .asBitmap()
            .load(drawable)
            .apply(
                RequestOptions().apply {
                    downsample(DownsampleStrategy.CENTER_INSIDE)
                    threshold.let {
                        override(it)
                    }
                }
            )
            .submit()
            .get()
    }

    fun scannerToMediaStore(context: Context, file: File) {
        MediaScannerConnection.scanFile(
            context,
            arrayOf(file.path),
            null
        ) { path1: String?, uri: Uri? -> }
    }

    private fun galleryAddPic(context: Context, path: String) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val f = File(path)
        val contentUri = Uri.fromFile(f)
        mediaScanIntent.data = contentUri
        context.sendBroadcast(mediaScanIntent)
    }
}
