package com.unitedinternet.portal.android.onlinestorage.glide

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.LibraryGlideModule
import okhttp3.ConnectionPool
import okhttp3.Dispatcher
import okhttp3.OkHttpClient
import timber.log.Timber
import java.io.InputStream
import java.util.concurrent.TimeUnit

@GlideModule
class OkHttpGlideModule : LibraryGlideModule() {

    companion object {
        private const val KEEP_ALIVE_DURATION = 5L // default value from okhttp

        fun getConcurrentRequestsCount(context: Context) = 16
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        val dispatcher = Dispatcher()
        val concurrentRequestsCount = getConcurrentRequestsCount(context)
        dispatcher.maxRequestsPerHost = concurrentRequestsCount
        val client = OkHttpClient.Builder()
            .connectionPool(ConnectionPool(concurrentRequestsCount, KEEP_ALIVE_DURATION, TimeUnit.MINUTES))
            .dispatcher(dispatcher)
            .addNetworkInterceptor { chain ->
                val request = chain.request()
                val response = chain.proceed(request)
                val requestDuration = response.receivedResponseAtMillis - response.sentRequestAtMillis
                Timber.d("Request duration $requestDuration Url:${request.url}")
                response.newBuilder().build()
            }
            .build()

        registry.replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory(client))
    }

}