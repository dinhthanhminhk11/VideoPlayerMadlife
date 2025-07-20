package rosita.madlife.video.player

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.navigation.NavigationBarView
import rosita.madlife.video.player.databinding.ActivityMainBinding
import rosita.madlife.video.player.fragment.main.ExtensionFragment
import rosita.madlife.video.player.fragment.main.HomeFragment
import rosita.madlife.video.player.fragment.main.ListFragment
import rosita.madlife.video.player.fragment.main.PipFragment
import rosita.madlife.video.playervideo.player.VideoViewManager

class MainActivity : AppCompatActivity(), NavigationBarView.OnItemSelectedListener {

    private lateinit var binding: ActivityMainBinding

    private val mFragments: MutableList<Fragment> = ArrayList()
    private val videoViewManager: VideoViewManager = VideoViewManager.instance()

    companion object {
        @JvmField
        var mCurrentIndex = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.navView.setOnItemSelectedListener(this)
        mFragments.add(HomeFragment())
        mFragments.add(ListFragment())
        mFragments.add(ExtensionFragment())
        mFragments.add(PipFragment())
        supportFragmentManager.beginTransaction()
            .add(R.id.layout_content, mFragments[0])
            .commitAllowingStateLoss()
        mCurrentIndex = 0

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (videoViewManager.onBackPress(Tag.LIST)) return
                if (videoViewManager.onBackPress(Tag.SEAMLESS)) return
                isEnabled = false
                this@MainActivity.onBackPressedDispatcher.onBackPressed()
            }
        })
    }

    override fun onNavigationItemSelected(menuItem: MenuItem): Boolean {
        val index: Int
        val itemId = menuItem.itemId
        index = when (itemId) {
            R.id.tab_api -> 0
            R.id.tab_list -> 1
            R.id.tab_extension -> 2
            R.id.tab_pip -> 3
            else -> 0
        }
        if (mCurrentIndex != index) {
            if (mCurrentIndex == 1) {
                VideoViewManager.instance().releaseByTag(Tag.LIST)
                VideoViewManager.instance().releaseByTag(Tag.SEAMLESS, false)
            }
            val transaction = supportFragmentManager.beginTransaction()
            val fragment = mFragments[index]
            val curFragment = mFragments[mCurrentIndex]
            if (fragment.isAdded) {
                transaction.hide(curFragment).show(fragment)
            } else {
                transaction.add(R.id.layout_content, fragment).hide(curFragment)
            }
            transaction.commitAllowingStateLoss()
            mCurrentIndex = index
        }
        return true
    }

    override fun onBackPressed() {
        if (videoViewManager.onBackPress(Tag.LIST)) return
        if (videoViewManager.onBackPress(Tag.SEAMLESS)) return
        super.onBackPressed()
    }
}