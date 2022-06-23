package cn.spacexc.wearbili.utils

import android.media.MediaPlayer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner

/**
 * Created by XC-Qan on 2022/6/18.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */

class XCMediaPlayer : MediaPlayer(), LifecycleEventObserver {
    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when(event){
            Lifecycle.Event.ON_PAUSE ->{
                pause()
            }
            Lifecycle.Event.ON_RESUME -> {
                 start()
            }
            else -> {}
        }
    }

}