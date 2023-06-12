package com.example.myintrovert

import android.annotation.SuppressLint
import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.MediaStore
import androidx.loader.content.CursorLoader

object RealPathUtil {
    fun getRealPath(context: Context, fileUri: Uri): String? {
        val realPath: String?
        // SDK < API11
        if (Build.VERSION.SDK_INT < 11) {
            realPath = getRealPathFromUriBelowApi11(context, fileUri)
        } else if (Build.VERSION.SDK_INT < 19) {
            realPath = getRealPathFromUriApi11to18(context, fileUri)
        } else {
            realPath = getRealPathFromUriAboveApi19(context, fileUri)
        }
        return realPath
    }

    @SuppressLint("Recycle")
    private fun getRealPathFromUriBelowApi11(context: Context, contentUri: Uri): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver.query(contentUri, proj, null, null, null)
        var realPath: String? = null
        if (cursor != null) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            realPath = cursor.getString(columnIndex)
            cursor.close()
        }
        return realPath
    }

    private fun getRealPathFromUriApi11to18(context: Context, contentUri: Uri): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val loader = CursorLoader(context, contentUri, proj, null, null, null)
        val cursor: Cursor? = loader.loadInBackground()
        var realPath: String? = null
        if (cursor != null) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            realPath = cursor.getString(columnIndex)
            cursor.close()
        }
        return realPath
    }

    private fun getRealPathFromUriAboveApi19(context: Context, uri: Uri): String? {
        var filePath: String? = null
        if (DocumentsContract.isDocumentUri(context, uri)) {
            // Jika dokumen adalah MediaStorage
            if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val contentUri: Uri = when (split[0]) {
                    "image" -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    "video" -> MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                    "audio" -> MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                    else -> throw IllegalArgumentException("Invalid URI")
                }
                val selection = "_id=?"
                val selectionArgs = arrayOf(split[1])
                filePath = getDataColumn(context, contentUri, selection, selectionArgs)
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri: Uri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    id.toLong()
                )
                filePath = getDataColumn(context, contentUri, null, null)
            } else if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                if (split.size >= 2) {
                    val type = split[0]
                    if ("primary".equals(type, ignoreCase = true)) {
                        filePath =
                            Environment.getExternalStorageDirectory().path + "/" + split[1]
                    }
                }
            }
        } else if ("content".equals(uri.scheme, ignoreCase = true)) {
            filePath = getDataColumn(context, uri, null, null)
        } else if ("file".equals(uri.scheme, ignoreCase = true)) {
            filePath = uri.path
        }
        return filePath
    }

    private fun getDataColumn(
        context: Context, uri: Uri, selection: String?,
        selectionArgs: Array<String>?
    ): String? {
        var cursor: Cursor? = null
        val column = "_data"
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(
                uri, projection,
                selection, selectionArgs, null
            )
            if (cursor != null && cursor.moveToFirst()) {
                val columnIndex: Int = cursor.getColumnIndexOrThrow(column)
                return cursor.getString(columnIndex)
            }
        } finally {
            cursor?.close()
        }
        return null
    }

    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }
}