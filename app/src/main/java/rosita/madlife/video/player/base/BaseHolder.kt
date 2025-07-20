package rosita.madlife.video.player.base

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class BaseHolder<T : Any>(view: View) :
    RecyclerView.ViewHolder(view) {
    val context: Context = view.context
    abstract fun bind(item: T, onItemClick: ((T) -> Unit)? = null)
}
