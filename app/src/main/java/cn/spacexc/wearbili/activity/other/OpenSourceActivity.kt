package cn.spacexc.wearbili.activity.other

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Info
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Text
import cn.spacexc.wearbili.manager.isRound
import cn.spacexc.wearbili.ui.CirclesBackground
import cn.spacexc.wearbili.ui.ModifierExtends.clickVfx
import cn.spacexc.wearbili.ui.googleSansFamily
import cn.spacexc.wearbili.utils.ToastUtils

/*
WearBili  Copyright (C) 2022 XC
This program comes with ABSOLUTELY NO WARRANTY.
This is free software, and you are welcome to redistribute it under certain conditions.
*/

/*
 * Created by XC on 2022/11/19.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */


class OpenSourceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CirclesBackground.RegularBackgroundWithTitleAndBackArrow(
                title = "开源项目",
                onBack = ::finish
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(
                            vertical = if (isRound()) 40.dp else 8.dp,
                            horizontal = if (isRound()) 20.dp else 8.dp
                        ),
                    horizontalAlignment = if (isRound()) Alignment.CenterHorizontally else Alignment.Start
                ) {
                    Text(
                        text = "Open Source Information",
                        fontSize = 20.sp,
                        fontFamily = googleSansFamily,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Repository Address: https://github.com/SpaceXC/WearBili",
                        fontFamily = googleSansFamily,
                        color = Color.White,
                        modifier = Modifier.clickVfx {
                            val data: Uri = Uri.parse("weargit://repository/SpaceXC/WearBili")
                            val intent = Intent(Intent.ACTION_VIEW, data)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            try {
                                startActivity(intent)
                            } catch (e: Exception) {
                                e.printStackTrace()
                                val clipboardManager: ClipboardManager =
                                    ContextCompat.getSystemService(
                                        this@OpenSourceActivity,
                                        ClipboardManager::class.java
                                    ) as ClipboardManager
                                val clip: ClipData =
                                    ClipData.newPlainText(
                                        "repository",
                                        "https://github.com/SpaceXC/WearBili"
                                    )
                                clipboardManager.setPrimaryClip(clip)
                                ToastUtils.makeText("没有安装WearGit, 已复制仓库链接")
                                    .show()
                            }
                        }
                    )
                    Text(
                        text = "License",
                        fontSize = 20.sp,
                        fontFamily = googleSansFamily,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "License Name: GNU General Public License v3.0",
                        fontFamily = googleSansFamily,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Permissions",
                        fontFamily = googleSansFamily,
                        fontSize = 14.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    LicenseContentItem(type = TYPE_CHECK, text = "Commercial use")
                    LicenseContentItem(type = TYPE_CHECK, text = "Modification")
                    LicenseContentItem(type = TYPE_CHECK, text = "Distribution")
                    LicenseContentItem(type = TYPE_CHECK, text = "Patent use")
                    LicenseContentItem(type = TYPE_CHECK, text = "Private use")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Limitations",
                        fontFamily = googleSansFamily,
                        fontSize = 14.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    LicenseContentItem(type = TYPE_CROSS, text = "Liability")
                    LicenseContentItem(type = TYPE_CROSS, text = "Warranty")
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Conditions",
                        fontFamily = googleSansFamily,
                        fontSize = 14.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    LicenseContentItem(type = TYPE_INFO, text = "License and copyright notice")
                    LicenseContentItem(type = TYPE_INFO, text = "State changes")
                    LicenseContentItem(type = TYPE_INFO, text = "Disclose source")
                    LicenseContentItem(type = TYPE_INFO, text = "Same license")
                }
            }
        }
    }

    @Composable
    fun LicenseContentItem(type: Int, text: String) {
        val localDensity = LocalDensity.current
        var iconSize by remember {
            mutableStateOf(0.dp)
        }
        Row {
            Icon(
                imageVector = when (type) {
                    TYPE_CHECK -> Icons.Rounded.Check
                    TYPE_CROSS -> Icons.Rounded.Close
                    TYPE_INFO -> Icons.Outlined.Info
                    else -> Icons.Rounded.Info
                }, contentDescription = null, tint = when (type) {
                    TYPE_CHECK -> Color(android.graphics.Color.parseColor("#1a7f37"))
                    TYPE_CROSS -> Color(android.graphics.Color.parseColor("#cf222e"))
                    TYPE_INFO -> Color(android.graphics.Color.parseColor("#0969da"))
                    else -> Color.White
                },
                modifier = Modifier.size(iconSize)
            )
            Text(
                text = text,
                fontSize = 13.sp,
                fontFamily = googleSansFamily,
                modifier = Modifier.onGloballyPositioned {
                    iconSize = with(localDensity) { it.size.height.toDp() }
                }, maxLines = 1, overflow = TextOverflow.Ellipsis
            )
        }
    }

    companion object {
        const val TYPE_CHECK = 1
        const val TYPE_CROSS = 2
        const val TYPE_INFO = 3
    }
}