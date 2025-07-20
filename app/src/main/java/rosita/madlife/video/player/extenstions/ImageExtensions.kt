package rosita.madlife.video.player.extenstions

import android.annotation.SuppressLint
import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import jp.wasabeef.glide.transformations.BlurTransformation
import rosita.madlife.video.player.R

@SuppressLint("CheckResult")
fun ImageView.loadImageThumb(
    context: Context?,
    imageUrl: String?,
    imageDefault: Int = R.drawable.background_black
) {
    if (context == null || imageUrl.isNullOrBlank()) {
        this.setImageResource(imageDefault)
        return
    }

    try {
        val mainRequest = RequestOptions()
            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
            .placeholder(imageDefault)
            .error(imageDefault)

        val glideRequest = Glide.with(context)
            .load(imageUrl)
            .apply(mainRequest)
            .transition(DrawableTransitionOptions.withCrossFade(300))

        val blurOptions = RequestOptions().transform(BlurTransformation(25, 3))
        glideRequest.thumbnail(
            Glide.with(context)
                .load(imageUrl)
                .apply(blurOptions)
        )

        glideRequest.into(this)
    } catch (e: Exception) {
        e.printStackTrace()
        this.setImageResource(imageDefault)
    }
}