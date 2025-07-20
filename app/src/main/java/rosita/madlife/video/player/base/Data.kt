package rosita.madlife.video.player.base

data class Data<out T: Any>(
    val data: T,
    val vhInfo: VHInfo,
)