package rosita.madlife.video.player.base

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import rosita.madlife.video.player.R
import rosita.madlife.video.player.holder.BlankHolder
import kotlin.collections.toList

abstract class BaseAdapter :
    ListAdapter<Data<*>, BaseHolder<*>>(object : DiffUtil.ItemCallback<Data<*>>() {
        override fun areItemsTheSame(
            oldItem: Data<*>,
            newItem: Data<*>
        ): Boolean {
            if (oldItem.vhInfo != newItem.vhInfo) return false
            val oldData = oldItem.data
            val newData = newItem.data
            return when {
                oldData is Identifiable && newData is Identifiable ->
                    oldData.getItemId() == newData.getItemId()

                else -> oldData == newData
            }
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Data<*>, newItem: Data<*>): Boolean {
            return oldItem.data == newItem.data && oldItem.vhInfo == newItem.vhInfo
        }
    }) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<*> {
        val vhInfo = VHInfo.entries.getOrNull(viewType)
        return vhInfo?.let {
            val view = LayoutInflater.from(parent.context).inflate(it.layout, parent, false)
            it.createViewHolder(view)
        } ?: BlankHolder(View(parent.context))
    }

    override fun getItemViewType(position: Int): Int = getItemAt(position).vhInfo.ordinal

    override fun onViewDetachedFromWindow(holder: BaseHolder<*>) {
        super.onViewDetachedFromWindow(holder)
        holder.itemView.clearAnimation()
    }

    fun getItemAt(position: Int): Data<*> = currentList[position]

    fun getItemAtOrNull(position: Int): Data<*>? = currentList.getOrNull(position)

    fun setData(data: List<Data<*>>) {
        submitList(data.toList())
    }

    fun addData(listData: List<Data<*>>) {
        val newList = currentList.toMutableList().apply { addAll(listData) }
        if (currentList != newList) {
            submitList(newList.toList())
        }
    }

    fun currentList(): List<Data<*>> = currentList

    fun removeItemList(position: Int) {
        val newList = currentList.toMutableList()
        newList.removeAt(position)
        submitList(newList.toList())
    }

    fun removeRangeItemList(startPosition: Int, endIndex: Int) {
        val newList = currentList.toMutableList()
        newList.subList(startPosition, endIndex + 1).clear()
        submitList(newList)
    }

    fun addLoadMoreIndicator() {
//        val newList = currentList.toMutableList()
//        if (newList.none { it.vhInfo == VHInfo.LOADING }) {
//            newList.add(Data(LoadingConfig(), VHInfo.LOADING))
//            submitList(newList.toList())
//        }
    }

    fun removeLoadMoreIndicator() {
//        val newList = currentList.filterNot { it.vhInfo == VHInfo.LOADING }
//        submitList(newList.toList())
    }

    fun removeLoadMoreDetail() {
//        val newList = currentList.filterNot { it.vhInfo == VHInfo.SEE_MORE_DETAIL }
//        submitList(newList.toList())
    }

    protected open fun animateItemView(view: View) {
        if (view.getTag(R.id.has_animated) != true) {
            view.startAnimation(
                AnimationUtils.loadAnimation(view.context, R.anim.fade_in)
            )
            view.setTag(R.id.has_animated, true)
        }
    }

}
