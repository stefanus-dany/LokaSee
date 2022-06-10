package com.bangkit.lokasee.util

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.os.Environment
import com.bangkit.lokasee.data.retrofit.ApiConfig.HOST
import com.bangkit.lokasee.data.store.UserStore.currentUser
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

private const val FILENAME_FORMAT = "dd-MMM-yyyy"

val timeStamp: String = SimpleDateFormat(
    FILENAME_FORMAT,
    Locale.US
).format(System.currentTimeMillis())

fun createCustomTempFile(context: Context): File {
    val storageDir: File? = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
    return File.createTempFile(timeStamp, ".jpg", storageDir)
}

fun uriToFile(selectedImg: Uri, context: Context): File {
    val contentResolver: ContentResolver = context.contentResolver
    val myFile = createCustomTempFile(context)
    val inputStream = contentResolver.openInputStream(selectedImg) as InputStream
    val outputStream: OutputStream = FileOutputStream(myFile)
    val buf = ByteArray(1024)
    var len: Int
    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
    outputStream.close()
    inputStream.close()
    return myFile
}

fun createPartFromString(descriptionString: String): RequestBody {
    return descriptionString.toRequestBody(MultipartBody.FORM)
}

const val AVATAR_URL_BASE = "https://ui-avatars.com/api/?bold=true&size=128&background=random&name="

fun getAvatarUrl(): String {
    return if (currentUser.avatarUrl == "") StringBuilder(AVATAR_URL_BASE).append(currentUser.avatarUrl.replace(' ', '+')).toString()
    else "${HOST}/${currentUser.avatarUrl}"
}

fun <K, V> Map<out K?, V?>.filterNotNull(): Map<K, V> = this.mapNotNull {
    it.key?.let { key ->
        it.value?.let { value ->
            key to value
        }
    }
}.toMap()