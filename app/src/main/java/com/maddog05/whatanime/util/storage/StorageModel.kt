package com.maddog05.whatanime.util.storage

import android.content.Context
import android.os.Environment
//import android.util.Log
import java.io.File
//import java.io.FileInputStream
//import java.io.FileOutputStream
//import java.lang.Exception
//import java.nio.channels.FileChannel

class StorageModel(private val context:Context) {
//    fun copyFile(sourceFile: File, destFile: File) {
//        var isProcessComplete: Boolean
//        if (!destFile.exists())
//            destFile.createNewFile()
//        var source: FileChannel? = null
//        var destination: FileChannel? = null
//        try {
//            source = FileInputStream(sourceFile).channel
//            destination = FileOutputStream(destFile).channel
//            destination.transferFrom(source, 0, source.size())
//            isProcessComplete = true
//        } catch (e: Exception) {
//            Log.e("#Andree", "Exception in copy $e")
//            isProcessComplete = false
//        } finally {
//            source?.close()
//            destination?.close()
//        }
//    }

    fun getTempImageFolder(): File {
        val folder =
            File(context.filesDir, Environment.DIRECTORY_DOWNLOADS)
        folder.mkdirs()
        return folder
    }

    fun isFileInStorage(folder: File, fileName: String): Boolean {
        val file = File(folder, "/$fileName")
        return file.exists()
    }

    fun getFile(folder: File, fileName: String): File {
        return File(folder, "/$fileName")
    }

    fun deleteFile(folder: File, fileName: String) {
        val file = File(folder, "/$fileName")
        if (file.exists()) {
            file.delete()
        }
    }
}