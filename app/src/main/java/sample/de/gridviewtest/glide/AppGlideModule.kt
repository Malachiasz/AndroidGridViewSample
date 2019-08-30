package sample.de.gridviewtest.glide

import android.content.Context
import androidx.annotation.NonNull
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.engine.executor.GlideExecutor
import com.bumptech.glide.load.engine.executor.GlideExecutor.newSourceExecutor
import com.bumptech.glide.module.AppGlideModule
import com.unitedinternet.portal.android.onlinestorage.glide.OkHttpGlideModule


@GlideModule
class AppGlideModule : AppGlideModule() {

    companion object {
        private const val GLIDE_EXECUTOR = "GlideExecutor"
    }

    override fun applyOptions(@NonNull context: Context, @NonNull builder: GlideBuilder) {
        builder.setSourceExecutor(newSourceExecutor(OkHttpGlideModule.getConcurrentRequestsCount(context), GLIDE_EXECUTOR, GlideExecutor.UncaughtThrowableStrategy.DEFAULT))
    }

}