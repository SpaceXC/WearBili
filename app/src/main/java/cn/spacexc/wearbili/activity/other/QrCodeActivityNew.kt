package cn.spacexc.wearbili.activity.other

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import androidx.wear.compose.material.Text
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.ui.BilibiliPink
import cn.spacexc.wearbili.ui.CirclesBackground
import cn.spacexc.wearbili.ui.puhuiFamily
import cn.spacexc.wearbili.utils.QRCodeUtil
import cn.spacexc.wearbili.utils.QRCodeUtil.ERROR_CORRECTION_L

/**
 * Created by XC-Qan on 2023/1/19.
 * I'm very cute so please be nice to my code!
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 * 给！爷！写！注！释！
 */
const val PARAM_QRCODE_URL = "qrcodeUrl"
const val PARAM_QRCODE_MESSAGE = "qrcodeMessage"

class QrCodeActivityNew : AppCompatActivity() {
    private val qrcodeBitmap = MutableLiveData<ImageBitmap>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val url = intent.getStringExtra(PARAM_QRCODE_URL)
        val message = intent.getStringExtra(PARAM_QRCODE_MESSAGE)
        val bitmap = QRCodeUtil.createQRCodeBitmap(url, 128, 128, ERROR_CORRECTION_L)
        qrcodeBitmap.value = bitmap?.asImageBitmap()
        setContent {
            CirclesBackground.RegularBackgroundWithTitleAndBackArrow(
                title = "",
                onBack = ::finish
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = 8.dp, end = 8.dp, top = 4.dp)
                        .clip(
                            RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                        )
                        .background(Color(39, 39, 39, 179))
                        .border(
                            width = (0.1).dp, color = Color(
                                112,
                                112,
                                112,
                                255
                            ), shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                        )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.twotwo_threethree),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                BilibiliPink
                            )
                            .padding(top = 24.dp)
                    )
                    Column(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .verticalScroll(
                                rememberScrollState()
                            ), horizontalAlignment = CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(vertical = 16.dp, horizontal = 20.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White)
                                .fillMaxWidth()
                                .aspectRatio(1f)
                                .padding(6.dp)
                        ) {
                            qrcodeBitmap.value?.let {
                                Image(
                                    bitmap = it,
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.FillBounds
                                )
                            }
                        }
                        Text(
                            text = message ?: "",
                            color = Color.White,
                            fontSize = 13.sp,
                            fontFamily = puhuiFamily,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}