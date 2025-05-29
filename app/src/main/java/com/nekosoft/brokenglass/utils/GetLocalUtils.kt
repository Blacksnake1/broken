/*
 *    Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.nekosoft.brokenglass.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import android.os.StatFs
import android.provider.MediaStore
import android.util.Log
import com.brokenscreen.prankapp.crack.screen.R
import java.io.File
import java.util.*

object GetLocalUtils {
    const val pngFormat = ".png"
    const val mp4Format = ".mp4"

    /**
     * Getting All Images Path in external memory have end by .png, .jpg, .jpeg.
     *
     * Required Storage Permission
     *
     * @return ArrayList with images Path
     */
    @SuppressLint("Recycle")
    fun loadListImagesFromSDCard(context: Context): ArrayList<String> {
        val listOfAllImages = ArrayList<String>()
        val query = context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            null,
            null as String?,
            null as Array<String?>?,
            MediaStore.Images.Media.DATE_ADDED + " DESC"
        )
        val count = query!!.count
        val strArr = arrayOfNulls<String>(count)
        for (i in 0 until count) {
            query.moveToPosition(i)
            strArr[i] = query.getString(query.getColumnIndexOrThrow(MediaStore.Images.Media.DATA))
            if (strArr[i] != null) {
                val file = strArr[i]?.let { File(it) }
                if (file?.exists() == true) {
                    if (file.absolutePath.endsWith(".png") || file.absolutePath.endsWith(".jpg") || file.absolutePath.endsWith(
                            ".jpeg"
                        )
                    ) {
                        listOfAllImages.add(file.absolutePath)
                    }
                }
            }
        }

        return listOfAllImages
    }


    /** C1: kiểm tra xem bộ nhớ máy còn đủ dung lượng tối thiểu 60mb
     *  (có thể thay dung lượng tối thiểu bằng số khac) không*/
    fun hasFreeStorage(): Boolean {
        val freeSpace = Environment.getExternalStorageDirectory().freeSpace
        if (freeSpace > 60 * 1024 * 1024) {
            return true
        }
        return false
    }

    /** C2: kiểm tra xem bộ nhớ máy còn đủ dung lượng tối thiểu 60mb
     * (có thể thay dung lượng tối thiểu bằng số khac) không*/
    fun checkAvaibleExternal(): Boolean {
        if (getAvailableExternalMemorySize() > 60 * 1024 * 1024) {
            return true
        }
        return false
    }

    fun getAvailableExternalMemorySize(): Long {
        return if (externalMemoryAvailable()) {
            val path: File = Environment.getExternalStorageDirectory()
            val stat = StatFs(path.path)
            val blockSize = stat.blockSizeLong
            val availableBlocks = stat.availableBlocksLong
            return availableBlocks * blockSize
        } else {
            0L
        }
    }

    private fun externalMemoryAvailable(): Boolean {
        return Environment.getExternalStorageState() ==
                Environment.MEDIA_MOUNTED
    }

    /** lấy toàn bộ file trong folder được chỉ định trên thiết bị*/
    fun getAllFileInDirectory(context: Context, childFolderName: String?): Array<File>? {
        val directoryPath =
            createExternalFolder(context.getString(R.string.app_name), childFolderName)
        val directory = File(directoryPath.absolutePath)
        var filesInDirectory: Array<File>? = null
        if (directory.exists() && directory.isDirectory) {
            filesInDirectory = directory.listFiles()
////             Lặp qua từng tệp trong thư mục
//            if (filesInDirectory != null) {
//                for (file in filesInDirectory) {
//                    if (file.isFile) {
//                        // Đây là một tệp, bạn có thể làm gì đó với nó ở đây
//                        val fileName = file.name
//                        val filePath = file.absolutePath
//                    }
//                }
//            }
        } else {
            // Thư mục không tồn tại hoặc không phải là một thư mục
            filesInDirectory = null
        }
        return filesInDirectory
    }


    /** Lấy đường dẫn của file trong thư mục cố định */
    fun getThumPath(
        context: Context?,
        id: String,
        childFolderName: String?,
        fileFormat: String?,
    ): String? {
        val rootType =
            context?.getString(R.string.app_name)?.let { createDirPath(it, childFolderName) }
        return if (rootType != null) {
            val file = File(
                rootType,
                id + fileFormat
            )

            file.absolutePath
        } else {
            null
        }
    }

    /**
     * tạo một thư mục mới trong thư mục Download tại bộ nhớ ngoài của thiết bị
     *
     * @param parentFolderName thư mục lớn.( appName)
     * @param childFolderName thư mục con.
     */
    fun createExternalFolder(parentFolderName: String, childFolderName: String?): File {
        val dirPath = createDirPath(parentFolderName, childFolderName)
        val dir = File(dirPath)
        if (!checkFileExists(dirPath)) {
            dir.mkdirs()
        }
        return dir
    }

    /**tạo đường dẫn cho thư mục mới tại thư mực Download của thiết bị
     *
     *  @param parentFolderName thư mục lớn.( appName)
     *  @param childFolderName thư mục con.
     */
    fun createDirPath(parentFolderName: String, childFolderName: String?): String {
        val dirPath = if (childFolderName == null) {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + File.separator + parentFolderName + File.separator
        } else {
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + File.separator + parentFolderName + File.separator + childFolderName + File.separator
        }
        return dirPath
    }

    /**
     * tạo một thư mục mới trong thư mục Download tại bộ nhớ trong của app
     *
     * @param parentFolderName thư mục lớn.( appName)
     * @param childFolderName thư mục con.
     */
    fun createInternalFolder(
        context: Context,
        parentFolderName: String,
        childFolderName: String?,
    ): File? {
        val filePath = createInternalDirPath(context, parentFolderName, childFolderName)
        val file = File(filePath)
        if (file.exists()) {
            return file
        } else {
            if (file.mkdirs()) {
                return file
            } else {
                Log.e("Exception", "Unable to create root directory \"" + file.path + "\"")
            }
        }
        return null
    }

    /**tạo đường dẫn cho thư mục mới tại bộ nhớ trong của app
     *
     *  @param parentFolderName thư mục lớn.( appName)
     *  @param childFolderName thư mục con.
     */
    fun createInternalDirPath(
        context: Context,
        parentFolderName: String,
        childFolderName: String?,
    ): String {
        // Get Zero folder
        var filePath: String = context.filesDir.toString()
        if (childFolderName == null) {
            filePath = "$filePath/$parentFolderName/"
        } else {
            filePath = "$filePath/$parentFolderName/$childFolderName"
        }
        return filePath
    }

    /** Kiểm tra xem file có tồn tại hay chưa*/
    fun checkFileExists(dirPath: String): Boolean {
        val dir = File(dirPath)
        return dir.exists()
    }

    /** tạo 1 file ở trong thư mục */
    fun getFileInExternalStorage(folderPath: String, fileName: String): File {
        val externalStorageDirectory = Environment.getExternalStorageDirectory()
        val folder = File(externalStorageDirectory, folderPath)
        return File(folder, fileName)
    }

    /** lấy 1 file ở folder Download của bộ nhớ ngoài
     * trả về một File
     * Nếu không tồn tại sẽ trả về null
     * */
    fun getAnyFileInFolder(
        parentFolderName: String,
        childFolderName: String?,
        nameFile: String?,
        fileFormat: String,
    ): File? {
        var mPair: File? = null
        val filePath =
            Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOWNLOADS
            ).absolutePath +
                    File.separator + parentFolderName +
                    File.separator + childFolderName +
                    File.separator + nameFile + fileFormat
        if (File(filePath).exists()) {
            mPair = File(filePath)
        } else {
            mPair = null
        }
        return mPair
    }
}