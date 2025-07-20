package rosita.madlife.video.player.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import rosita.madlife.video.playervideo.player.VideoViewManager

abstract class BaseFragment<T : ViewBinding>(
    private val bindingInflater: (LayoutInflater) -> T
) : Fragment() {
    private var _binding: T? = null
    protected val binding get() = _binding!!

    protected var isLazyLoad: Boolean = false
    private var mIsInitData: Boolean = false
    protected val videoManager: VideoViewManager = VideoViewManager.instance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = bindingInflater.invoke(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        initOnClickListener()
        observeStateFlow()
        if (!isLazyLoad) {
            fetchData()
        }
    }

    override fun onResume() {
        super.onResume()
        fetchData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    abstract fun setupViews()
    abstract fun initOnClickListener()
    abstract fun observeStateFlow()
    abstract fun fetchInitialData()

    private fun fetchData() {
        if (mIsInitData) return
        fetchInitialData()
        mIsInitData = true
    }

}