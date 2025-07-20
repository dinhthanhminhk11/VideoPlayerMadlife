package rosita.madlife.video.player.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import rosita.madlife.video.player.databinding.ItemTiktokListBinding
import rosita.madlife.video.player.model.TiktokBean

class TikTokListAdapter(
    var data: List<TiktokBean>
) : RecyclerView.Adapter<TikTokListAdapter.TikTokListViewHolder>() {
    private var mId: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TikTokListViewHolder {
        val binding = ItemTiktokListBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return TikTokListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TikTokListViewHolder, position: Int) {
        val item = data[position]
        with(holder.binding) {
            tvTitle.text = item.title
            Glide.with(ivThumb.context)
                .load(item.coverImgUrl)
                .into(ivThumb)
        }
        holder.position = position
    }

    override fun getItemCount(): Int = data.size

    fun setImpl(id: Int) {
        mId = id
    }

    inner class TikTokListViewHolder(val binding: ItemTiktokListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        var position: Int = 0

        init {
            binding.root.setOnClickListener {
                val context = binding.root.context
                when (mId) {
//                    R.id.impl_recycler_view -> TikTokActivity.start(context, position)
//                    R.id.impl_vertical_view_pager -> TikTok2Activity.start(context, position)
//                    R.id.impl_view_pager_2 -> TikTok3Activity.start(context, position)
                }
            }
        }
    }
}