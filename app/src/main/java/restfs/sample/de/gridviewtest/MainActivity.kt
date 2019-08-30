package restfs.sample.de.gridviewtest

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.util.FixedPreloadSizeProvider
import com.futuremind.recyclerviewfastscroll.FastScroller
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var listGlideRequestBuilderProvider: ListGlideRequestBuilderProvider

    private val adapter: RecyclerView.Adapter<RecyclerView.ViewHolder> = object :
        RecyclerView.Adapter<RecyclerView.ViewHolder>(), ListPreloader.PreloadModelProvider<String> {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            return object : RecyclerView.ViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.gridview_item,
                    parent,
                    false
                )
            ) {
            }
        }

        override fun getItemCount() = 1000
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            val imageView = holder.itemView as ImageView

            imageView.setOnClickListener { Toast.makeText(applicationContext, "Position: $position", Toast.LENGTH_SHORT).show() }
            val url = computeUrlForPosition(position)
            holder.itemView.setBackgroundColor(position * position + 1500 * 1500)

            listGlideRequestBuilderProvider.provideGlideRequestBuilderWithLowResThumbnail(url, false)
                .transition(DrawableTransitionOptions.withCrossFade())
                .addListener(createRequestListener(url))
                .into(imageView)
        }

        override fun getItemId(position: Int): Long = position.toLong()

        override fun getPreloadItems(position: Int): List<String> {
            return listOf(computeUrlForPosition(position))
        }

        override fun getPreloadRequestBuilder(item: String): RequestBuilder<*>? {
            return listGlideRequestBuilderProvider.provideGlideRequestBuilderWithLowResThumbnail(item, true);
        }

        private fun computeUrlForPosition(positon: Int) = "https://picsum.photos/id/$positon/30";
    }

    private fun createRequestListener(url: String): RequestListener<Drawable> {
        return object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?,
                model: Any,
                target: Target<Drawable>,
                isFirstResource: Boolean
            ): Boolean {
                Timber.e("Thumbnail with url: ${url} failed")
                return false
            }

            override fun onResourceReady(
                resource2: Drawable,
                model: Any,
                target: Target<Drawable>,
                dataSource: DataSource,
                isFirstResource: Boolean
            ): Boolean {
                Timber.d("Thumbnail with url: ${url} loaded layeredImageView from: $dataSource")
                return false
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val scrollbar: FastScroller = findViewById(R.id.fastscroll)
        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)

        val gridLayoutManager = GridLayoutManager(this, 3)
        gridLayoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                if (position % 20 != 0) {
                    return 1
                } else {
                    return gridLayoutManager.spanCount
                }
            }
        }
        gridLayoutManager.spanSizeLookup.isSpanIndexCacheEnabled = true
        gridLayoutManager.isItemPrefetchEnabled = true
        gridLayoutManager.isSmoothScrollbarEnabled = true

        //     adapter.setHasStableIds(true)
        listGlideRequestBuilderProvider = ListGlideRequestBuilderProvider(this, android.R.drawable.ic_lock_lock, android.R.dimen.thumbnail_width, R.dimen.image_rounding)

        recyclerView.setItemViewCacheSize(20)
        recyclerView.itemAnimator = null
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.adapter = adapter

        val sizeProvider: ListPreloader.PreloadSizeProvider<String> = FixedPreloadSizeProvider(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
        recyclerView.addOnScrollListener(RecyclerViewPreloader<String>(Glide.with(this), adapter as ListPreloader.PreloadModelProvider<String>, sizeProvider, 75))


        scrollbar.setRecyclerView(recyclerView)
    }
}
