package cn.spacexc.wearbili.utils

import android.content.Context
import androidx.recyclerview.widget.LinearSmoothScroller

/**
 * Created by XC-Qan on 2022/8/14.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class RecyclerViewUtils {
    class TopLinearSmoothScroller(context: Context?) : LinearSmoothScroller(context) {
        override fun getVerticalSnapPreference(): Int {
            return SNAP_TO_START
        }
    }
}