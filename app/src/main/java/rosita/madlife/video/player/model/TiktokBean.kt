package rosita.madlife.video.player.model

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

data class TiktokBean(
    val authorImgUrl: String? = null,
    val authorName: String? = null,
    val authorSex: Int = 0,
    val coverImgUrl: String? = null,
    val createTime: Long = 0,
    val dynamicCover: String? = null,
    val filterMusicNameStr: String? = null,
    val filterTitleStr: String? = null,
    val filterUserNameStr: String? = null,
    val formatLikeCountStr: String? = null,
    val formatPlayCountStr: String? = null,
    val formatTimeStr: String? = null,
    val likeCount: Int = 0,
    val musicAuthorName: String? = null,
    val musicImgUrl: String? = null,
    val musicName: String? = null,
    val playCount: Int = 0,
    val title: String? = null,
    val type: Int = 0,
    val videoDownloadUrl: String? = null,
    val videoHeight: Int = 0,
    val videoPlayUrl: String? = null,
    val videoWidth: Int = 0
) {
    companion object {
        fun arrayTiktokBeanFromData(str: String): List<TiktokBean> {
            val listType = object : TypeToken<List<TiktokBean>>() {}.type
            return Gson().fromJson(str, listType)
        }
    }
}
