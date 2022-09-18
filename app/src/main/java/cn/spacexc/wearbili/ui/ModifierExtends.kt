package cn.spacexc.wearbili.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale

/**
 * Created by Xiaochang on 2022/9/17.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */
object ModifierExtends {
    @Composable
    fun Modifier.clickVfx(
        interactionSource: MutableInteractionSource =
            remember { MutableInteractionSource() },
        onClick: () -> Unit,
    ): Modifier = composed {
        val isPressed by interactionSource.collectIsPressedAsState()
        val sizePercent by animateFloatAsState(targetValue = if(isPressed) 0.9f else 1f)
        scale(sizePercent).
        clickable(indication = null, interactionSource = interactionSource, onClick = onClick)
    }
}