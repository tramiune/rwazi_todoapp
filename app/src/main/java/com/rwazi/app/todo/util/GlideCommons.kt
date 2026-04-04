package com.rwazi.app.todo.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition

object GlideCommons {

    private fun getDefaultRequestOptions(): RequestOptions {
        return RequestOptions().diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .skipMemoryCache(false)
    }

    fun loadIntoImageView(
        context: Context,
        path: Any,
        imageView: ImageView,
        progressBar: ProgressBar? = null,
        placeholder: Int = 0,
        errorImage: Int = 0,
        useCache: Boolean = true,
        onLoading: (() -> Unit)? = null,
        onSuccess: (() -> Unit)? = null,
        onError: (() -> Unit)? = null,
        requestOptions: RequestOptions? = null,
    ) {
        onLoading?.invoke()
        progressBar?.visibility = View.VISIBLE

        val finalOptions = requestOptions ?: createRequestOptions(
            placeholder = placeholder, errorImage = errorImage, useCache = useCache
        )

        Glide.with(context).load(path).apply(finalOptions)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable?>,
                    isFirstResource: Boolean,
                ): Boolean {
                    progressBar?.visibility = View.GONE
                    onError?.invoke()
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable?>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean,
                ): Boolean {
                    progressBar?.visibility = View.GONE
                    onSuccess?.invoke()
                    return false
                }
            }).into(imageView)
    }

    fun loadBitmap(
        context: Context,
        path: Any,
        onLoading: (() -> Unit)? = null,
        onSuccess: (Bitmap) -> Unit,
        onError: (() -> Unit)? = null,
        requestOptions: RequestOptions? = null,
    ) {
        onLoading?.invoke()

        Glide.with(context).asBitmap().load(path)
            .apply(requestOptions ?: getDefaultRequestOptions())
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    onSuccess(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    onError?.invoke()
                }
            })
    }

    fun loadSimple(
        context: Context,
        path: Any,
        imageView: ImageView,
        placeholder: Int = 0,
        errorImage: Int = 0,
        useCache: Boolean = true,
    ) {
        val options =
            RequestOptions().diskCacheStrategy(if (useCache) DiskCacheStrategy.AUTOMATIC else DiskCacheStrategy.NONE)
                .skipMemoryCache(!useCache)

        var request = Glide.with(context).load(path).apply(options)

        if (placeholder != 0) request = request.placeholder(placeholder)
        if (errorImage != 0) request = request.error(errorImage)

        request.into(imageView)
    }

    fun loadBitmapWithSize(
        context: Context,
        path: Any,
        onSuccess: (Bitmap) -> Unit,
        onError: (() -> Unit)? = null,
        width: Int = -1,
        height: Int = -1,
        useCache: Boolean = true,
    ) {
        val options =
            RequestOptions().diskCacheStrategy(if (useCache) DiskCacheStrategy.AUTOMATIC else DiskCacheStrategy.NONE)
                .skipMemoryCache(!useCache)

        Glide.with(context).asBitmap().load(path).apply(options)
            .into(object : CustomTarget<Bitmap>(width, height) {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    onSuccess(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Cleanup
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    onError?.invoke()
                }
            })
    }

    fun loadBitmapFromAsset(
        context: Context,
        uri: String,
        onLoading: (() -> Unit)? = null,
        onSuccess: (Bitmap) -> Unit,
        onError: (() -> Unit)? = null,
        requestOptions: RequestOptions? = null,
    ) {
        loadBitmap(
            context = context,
            path = uri.toUri(),
            onLoading = onLoading,
            onSuccess = onSuccess,
            onError = onError,
            requestOptions = requestOptions
        )
    }


    /**
     *  RequestOptions config
     */
    fun createRequestOptions(
        placeholder: Int = 0,
        errorImage: Int = 0,
        useCache: Boolean = true,
        centerCrop: Boolean = false,
        fitCenter: Boolean = false,
        circularCrop: Boolean = false,
        width: Int = -1,
        height: Int = -1,
    ): RequestOptions {
        var options =
            RequestOptions().diskCacheStrategy(if (useCache) DiskCacheStrategy.AUTOMATIC else DiskCacheStrategy.NONE)
                .skipMemoryCache(!useCache)

        if (placeholder != 0) options = options.placeholder(placeholder)
        if (errorImage != 0) options = options.error(errorImage)
        if (centerCrop) options = options.centerCrop()
        if (fitCenter) options = options.fitCenter()
        if (circularCrop) options = options.circleCrop()
        if (width > 0 && height > 0) options = options.override(width, height)

        return options
    }

}
