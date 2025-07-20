@file:Suppress("MISSING_DEPENDENCY_SUPERCLASS_IN_TYPE_ARGUMENT")

package rosita.madlife.video.player.fragment.list

import android.annotation.SuppressLint
import androidx.appcompat.widget.PopupMenu
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import rosita.madlife.video.player.DataUtil
import rosita.madlife.video.player.R
import rosita.madlife.video.player.adapter.TikTokListAdapter
import rosita.madlife.video.player.base.Data
import rosita.madlife.video.player.base.VHInfo
import rosita.madlife.video.player.databinding.FragmentTikTokListBinding
import rosita.madlife.video.player.fragment.BaseFragment
import rosita.madlife.video.player.model.TiktokBean
import kotlin.collections.map

class TikTokListFragment :
    BaseFragment<FragmentTikTokListBinding>(FragmentTikTokListBinding::inflate) {

    private lateinit var tikTokListAdapter: TikTokListAdapter
    private var idMenu: Int = R.id.impl_vertical_view_pager

    override fun isLazyLoad(): Boolean = true

    @SuppressLint("SetTextI18n")
    override fun setupViews() {
        tikTokListAdapter = TikTokListAdapter() { data, position ->
            when (idMenu) {
                R.id.impl_recycler_view -> {
//                    TikTokActivity.start(context, position)
                }

                R.id.impl_vertical_view_pager -> {
//                    TikTok2Activity.start(context, position)
                }

                R.id.impl_view_pager_2 -> {
//                    TikTok3Activity.start(context, position)
                }
            }
        }
        with(binding) {
            btnSwitchImpl.text = "VerticalViewPager"

            rvTiktok.layoutManager = GridLayoutManager(requireContext(), 2)
            rvTiktok.adapter = tikTokListAdapter
        }

        val menu = PopupMenu(requireContext(), binding.btnSwitchImpl)
        menu.inflate(R.menu.tiktok_impl_menu)

        menu.setOnMenuItemClickListener { item ->
            idMenu = item.itemId
            when (item.itemId) {
                R.id.impl_recycler_view -> {
                    binding.btnSwitchImpl.text = "RecyclerView"
                }

                R.id.impl_vertical_view_pager -> {
                    binding.btnSwitchImpl.text = "VerticalViewPager"
                }

                R.id.impl_view_pager_2 -> {
                    binding.btnSwitchImpl.text = "ViewPager2"
                }
            }
            false
        }

        binding.btnSwitchImpl.setOnClickListener {
            menu.show()
        }
    }

    override fun initOnClickListener() {

    }

    override fun observeStateFlow() {

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun fetchInitialData() {
        lifecycleScope.launch {
            val tiktokBeans: List<TiktokBean> = withContext(Dispatchers.IO) {
                DataUtil.getTiktokDataFromAssets(requireContext())
            }
//            val dataItems = tiktokBeans.map { Data(it, VHInfo.ITEM_LIST_TIKTOK) }
            tikTokListAdapter.setData(tiktokBeans.toDataItems())
        }
    }

    fun List<TiktokBean>.toDataItems(): List<Data<*>> =
        map { Data(it, VHInfo.ITEM_LIST_TIKTOK) }
}