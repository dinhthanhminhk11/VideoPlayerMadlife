package rosita.madlife.video.player.holder

import android.view.View
import rosita.madlife.video.player.base.BaseHolder
import rosita.madlife.video.player.databinding.ItemTiktokListBinding
import rosita.madlife.video.player.extenstions.loadImageThumb
import rosita.madlife.video.player.model.TiktokBean

class TikTokListViewHolder(view: View) : BaseHolder<TiktokBean>(view) {
    val binding = ItemTiktokListBinding.bind(view)

    override fun bind(
        item: TiktokBean,
        onItemClick: ((TiktokBean) -> Unit)?
    ) {
        with(binding) {
            tvTitle.text = item.title
            ivThumb.loadImageThumb(ivThumb.context , item.coverImgUrl)
        }
    }
}