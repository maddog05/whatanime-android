package com.maddog05.whatanime.core.image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import android.provider.MediaStore
import android.util.Base64
import java.io.ByteArrayOutputStream

class KImageUtil {
    companion object {
        fun getExternalImageAsBitmap(context: Context, uri: Uri): Bitmap? {
            val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

            val cursor =
                    context.contentResolver.query(uri, filePathColumn, null, null, null)
            var bitmap: Bitmap?
            try {
                bitmap = if (cursor != null) {
                    cursor.moveToFirst()

                    val columnIndex = cursor.getColumnIndex(filePathColumn[0])
                    val picturePath = cursor.getString(columnIndex)
                    cursor.close()

                    resizeLittle(BitmapFactory.decodeFile(picturePath))
                } else
                    null
            } catch (e: Exception) {
                bitmap = null
            } finally {
                cursor?.close()
            }
            return bitmap
        }

        private fun resizeLittle(originalBitmap: Bitmap): Bitmap {
            val widthFinal = 512
            return if (originalBitmap.width <= widthFinal || originalBitmap.height <= widthFinal)
                originalBitmap
            else {
                val factor = widthFinal / originalBitmap.width.toFloat()
                Bitmap.createScaledBitmap(
                        originalBitmap,
                        widthFinal,
                        (originalBitmap.height * factor).toInt(),
                        true
                )
            }
        }
    }

    interface OnEncodedListener {
        fun onComplete(encoded: String)
    }

    class ImageEncoderAsyncTask(private val listener: OnEncodedListener) :
            AsyncTask<Bitmap, Void, String>() {
        override fun doInBackground(vararg params: Bitmap): String {
            val result: String
            result = try {
                val bitmap = params[0]
                val byteArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                val byteArray = byteArrayOutputStream.toByteArray()
                Base64.encodeToString(byteArray, Base64.DEFAULT)
            } catch (e: Exception) {
                ""
            }
            return result
        }

        override fun onPostExecute(result: String?) {
            listener.onComplete(result ?: "")
        }
    }
}