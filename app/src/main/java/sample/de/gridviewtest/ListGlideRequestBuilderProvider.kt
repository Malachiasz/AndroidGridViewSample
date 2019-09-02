package sample.de.gridviewtest

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import java.util.*

internal class ListGlideRequestBuilderProvider constructor(
    activity: Activity,
    placeholderRes: Int?,
    thumbSizeRes: Int,
    roundedCornerRadiusRes: Int
) {

    private val lowResThumbSize: Int?
    private val thumbSize: Int?
    private val requestOptions: RequestOptions
    val requestManager: RequestManager

    var placeholderResId: Int? = null
        private set

    init {
        thumbSize = Math.round(activity.resources.getDimension(thumbSizeRes))
        lowResThumbSize = 100

        requestOptions = RequestOptions()
            .override(thumbSize)
            .transform(
                MultiTransformation<Bitmap>(
                    CenterCrop(),
                    RoundedCorners(
                        activity.resources.getDimensionPixelSize(roundedCornerRadiusRes)
                    )
                )
            )

        if (placeholderRes != null) {
            this.placeholderResId = placeholderRes
            requestOptions.placeholder(placeholderResId!!)
        }

        requestOptions.format(DecodeFormat.PREFER_RGB_565)
        requestManager = Glide.with(activity)
    }


    /**
     * @param resource thumbnailUri must not be null
     */
    fun provideGlideRequestBuilderWithLowResThumbnail(
        url: String,
        lowPriority: Boolean
    ): RequestBuilder<Drawable> {

        val thumbRequestBuilder = requestManager
            .load(CachingGlideUrl(url))
            .apply(requestOptions)
            .priority(if (lowPriority) Priority.NORMAL else Priority.IMMEDIATE)

        val highResUrl = url + "0" // just to symulate high resolution

        return requestManager
            .load(CachingGlideUrl(highResUrl))
            .thumbnail(thumbRequestBuilder)
            .apply(requestOptions)
            .priority(if (lowPriority) Priority.LOW else Priority.HIGH)
    }
}


class CachingGlideUrl(private val url: String) : GlideUrl(url) {
    private val headersMap = HashMap<String, String>()


    override fun getCacheKey(): String {
        return url
    }

    /**
     * IMPORTANT: this method should return the cacheKey since Glide is using both getCacheKey() and toString()
     * to resolve the image from the cache.
     */
    override fun toString(): String {
        return url
    }

    override fun getHeaders(): Map<String, String> {
        return headersMap
    }

    override fun equals(thatObject: Any?): Boolean {
        if (this === thatObject) {
            return true
        }
        if (thatObject !is CachingGlideUrl) {
            return false
        }
        if (!super.equals(thatObject)) {
            return false
        }

        val that = thatObject as CachingGlideUrl?

        return cacheKey == that!!.cacheKey && url == that.url
    }

    override fun hashCode(): Int {
        return Objects.hash(super.hashCode(), getCacheKey(), url)
    }
}