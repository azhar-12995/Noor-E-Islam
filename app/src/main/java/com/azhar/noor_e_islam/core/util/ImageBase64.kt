package com.azhar.noor_e_islam.core.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import java.io.ByteArrayOutputStream

/**
 * Helpers to convert image [Uri]s to and from compact Base64 strings for
 * Firestore storage (Firestore doc max = 1 MiB, so we down-scale aggressively).
 */
object ImageBase64 {

    private const val MAX_DIM = 720      // px on the longest side
    private const val JPEG_Q  = 70       // 70 = good quality / small payload

    /** Read the image at [uri], downscale, JPEG-compress, return Base64. */
    fun encodeFromUri(context: Context, uri: Uri): String? = runCatching {
        val bmp = loadAndScale(context, uri) ?: return null
        val baos = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, JPEG_Q, baos)
        Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP)
    }.getOrNull()

    /** Decode a Base64 payload back into a [Bitmap], or null if invalid. */
    fun decodeToBitmap(base64: String?): Bitmap? {
        if (base64.isNullOrBlank()) return null
        return runCatching {
            val bytes = Base64.decode(base64, Base64.NO_WRAP)
            BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        }.getOrNull()
    }

    private fun loadAndScale(context: Context, uri: Uri): Bitmap? {
        val cr = context.contentResolver
        // First pass: bounds only.
        val opts = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        cr.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, opts) }
        val (w, h) = opts.outWidth to opts.outHeight
        if (w <= 0 || h <= 0) return null

        var sample = 1
        val max = maxOf(w, h)
        while (max / sample > MAX_DIM * 2) sample *= 2

        val opts2 = BitmapFactory.Options().apply { inSampleSize = sample }
        val raw = cr.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, opts2)
        } ?: return null

        // Final scale to keep longest side <= MAX_DIM.
        val longest = maxOf(raw.width, raw.height)
        if (longest <= MAX_DIM) return raw
        val ratio = MAX_DIM.toFloat() / longest
        return Bitmap.createScaledBitmap(raw, (raw.width * ratio).toInt(), (raw.height * ratio).toInt(), true)
    }
}

