package rosita.madlife.video.player.fragment.main

import com.google.android.material.tabs.TabLayoutMediator
import rosita.madlife.video.player.R
import rosita.madlife.video.player.Tag
import rosita.madlife.video.player.adapter.ListPagerAdapter
import rosita.madlife.video.player.databinding.FragmentListBinding
import rosita.madlife.video.player.fragment.BaseFragment

class ListFragment : BaseFragment<FragmentListBinding>(FragmentListBinding::inflate) {
    override fun setupViews() {
        val titles = listOf<String>(
            getString(R.string.str_list_view),
            getString(R.string.str_recycler_view),
            getString(R.string.str_auto_play_recycler_view),
            getString(R.string.str_tiktok),
            getString(R.string.str_seamless_play),
            getString(R.string.str_portrait_when_fullscreen),
        )

        binding.viewPager.adapter = ListPagerAdapter(this, titles)

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()
    }

    override fun initOnClickListener() {
    }

    override fun observeStateFlow() {
    }

    override fun fetchInitialData() {
    }

    override fun onDetach() {
        super.onDetach()
        videoManager.releaseByTag(Tag.LIST)
        videoManager.releaseByTag(Tag.SEAMLESS)
    }

}