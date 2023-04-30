package xyz.potasyyum.mathsnap.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Environment
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.format.DateTimeFormatter
import java.util.*

object Utils {

    fun getCurrentDateString() : String {
        return SimpleDateFormat("yyyyMMddHHmm", Locale.getDefault()).format(Date())
    }

    private fun rotateBitmap(bitmap: Bitmap, orientation: Int = 90): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(orientation.toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    fun resizeAndRotateImage(uri: Uri, context: Context) {

        val inputStream = context.contentResolver.openInputStream(uri)
        val resultBitmap = rotateBitmap(BitmapFactory.decodeStream(inputStream))



        val outputStream = ByteArrayOutputStream()
        resultBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)

        var quality = 95
        while (outputStream.toByteArray().size / 1024 > 1024) {
            outputStream.reset()
            resultBitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            quality -= 5
        }

        val outputStream2 = context.contentResolver.openOutputStream(uri)
        outputStream2?.write(outputStream.toByteArray())
        outputStream2?.close()
    }

    fun createImageFile(context: Context): File {
        // Create an image file name
        val timeStamp: String = Utils.getCurrentDateString()
        val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "${timeStamp}_", /* prefix */
            ".jpg", /* suffix */
            storageDir /* directory */
        )
    }
}