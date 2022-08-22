package cn.spacexc.wearbili.utils

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.wear.widget.WearableLinearLayoutManager
import kotlin.math.abs


/**
 * Created by XC-Qan on 2022/8/22.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class WearableLayoutManagerCallback : WearableLinearLayoutManager.LayoutCallback() {
    override fun onLayoutFinished(child: View, parent: RecyclerView) {
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
                    abs(0.5f - yRelativeToCenterOffset).coerceAtMost(0.27555555555555555555f)  //感谢群友 @琉璃_Ruri 提供的甲方行为（掌声

                // Follow a curved path, rather than triangular!
                progressToCenter = Math.cos(progressToCenter * Math.PI * 0.70f).toFloat()
                child.scaleX = progressToCenter
                child.scaleY = progressToCenter
            } catch (e: Exception) {
            }


        }
    }
}