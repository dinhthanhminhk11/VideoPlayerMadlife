package rosita.madlife.video.player.base

import android.view.View
import rosita.madlife.video.player.R
import rosita.madlife.video.player.holder.TikTokListViewHolder

enum class VHInfo(val layout: Int, val createViewHolder: (View) -> BaseHolder<*>) {
    ITEM_LIST_TIKTOK(R.layout.item_tiktok_list, { TikTokListViewHolder(it) })
}