package cn.spacexc.wearbili.utils

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.wear.widget.WearableLinearLayoutManager
import cn.spacexc.wearbili.manager.SettingsManager
import kotlin.math.abs


/**
 * Created by XC-Qan on 2022/8/22.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

/**
 * From https://www.soinside.com/question/gTBStHR4chmpZeYVWbJ8R8
 * MODIFIED BY XC on 2022/08/22
 */
class WearableLayoutManagerCallback(private val maxVal: Float = 0.27555555555555555555f) :
    WearableLinearLayoutManager.LayoutCallback() {
    override fun onLayoutFinished(child: View, parent: RecyclerView) {
        if (SettingsManager.hasScrollVfx()) {
            child.apply {
                /*val difference = abs(parent.height - child.height)
                val percent: Float = difference.toFloat() / parent.height
                scaleX = 1 - percent
                scaleY = 1 - percent*/
                //alpha = percent
                // Figure out % progress from top to bottom
                /*val centerOffset = height.toFloat() / 200f / parent.height.toFloat()
                val yRelativeToCenterOffset = y / parent.height + centerOffset

                // Normalize for center
                progressToCenter = abs(0.1f - yRelativeToCenterOffset)
                // Adjust to the maximum scale
                progressToCenter = progressToCenter.coerceAtMost(0.25f)

                alpha = 1f - progressToCenter
                scaleX = 1f - progressToCenter
                scaleY = 1f - progressToCenter*/
                try {
                    val centerOffset =
                        child.height.toFloat() / 2.0f / parent.height.toFloat()
                    val yRelativeToCenterOffset: Float =
                        child.y / parent.height + centerOffset

                    // Normalize for center, adjusting to the maximum scale

                    /**
                     * 2022/08/22
                     * 谨此纪念
                     * 琉璃_Ruri  23:07:00
                     *  海星吧（
                     * 琉璃_Ruri  23:07:15
                     *  如果我说还要再减点你会不会揍我（
                     * 琉璃_Ruri  23:07:34
                     *  活成了自己最讨厌的样子）
                     */
                    var progressToCenter =
                        abs(0.5f - yRelativeToCenterOffset).coerceAtMost(maxVal)  //我：卑微乙方.jpg

                    // Follow a curved path, rather than triangular!
                    progressToCenter = Math.cos(progressToCenter * Math.PI * 0.7f).toFloat()
                    child.scaleX = progressToCenter
                    child.scaleY = progressToCenter
                } catch (e: Exception) {
                }
            }
        } else return
    }
}