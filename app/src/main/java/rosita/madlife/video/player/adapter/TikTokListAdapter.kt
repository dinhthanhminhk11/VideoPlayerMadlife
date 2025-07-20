package rosita.madlife.video.player.adapter

import rosita.madlife.video.player.base.BaseAdapter
import rosita.madlife.video.player.base.BaseHolder
import rosita.madlife.video.player.base.VHInfo
import rosita.madlife.video.player.holder.TikTokListViewHolder
import rosita.madlife.video.player.model.TiktokBean

class TikTokListAdapter(
    private val onItemClick: (TiktokBean, position: Int) -> Unit
) : BaseAdapter() {

    private var mId: Int = 0

    override fun onBindViewHolder(holder: BaseHolder<*>, position: Int) {
        val item = getItemAt(position)
        when (item.vhInfo) {
            VHInfo.ITEM_LIST_TIKTOK -> {
                val tiktokHolder = holder as TikTokListViewHolder
                tiktokHolder.bind(item.data as TiktokBean) {
                    onItemClick.invoke(it, position)
                }
            }
        }
    }

    fun setImpl(id: Int) {
        mId = id
    }
}