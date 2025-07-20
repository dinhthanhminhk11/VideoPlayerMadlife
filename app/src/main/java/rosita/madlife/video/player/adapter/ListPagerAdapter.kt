package rosita.madlife.video.player.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import rosita.madlife.video.player.fragment.list.ListViewFragment
import rosita.madlife.video.player.fragment.list.RecyclerViewAutoPlayFragment
import rosita.madlife.video.player.fragment.list.RecyclerViewFragment
import rosita.madlife.video.player.fragment.list.RecyclerViewPortraitFragment
import rosita.madlife.video.player.fragment.list.SeamlessPlayFragment
import rosita.madlife.video.player.fragment.list.TikTokListFragment

class ListPagerAdapter(fragment: Fragment, private val titles: List<String>) :
    FragmentStateAdapter(fragment) {

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            1 -> RecyclerViewFragment()
            2 -> RecyclerViewAutoPlayFragment()
            3 -> TikTokListFragment()
            4 -> SeamlessPlayFragment()
            5 -> RecyclerViewPortraitFragment()
            else -> ListViewFragment()
        }
    }

    override fun getItemCount(): Int = titles.size

}