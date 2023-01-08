package cn.spacexc.wearbili.activity.other

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.adapter.HorizontalButtonAdapter
import cn.spacexc.wearbili.adapter.UserHorizontalButtonAdapter
import cn.spacexc.wearbili.dataclass.HorizontalButtonData
import cn.spacexc.wearbili.listener.OnItemViewClickListener
import cn.spacexc.wearbili.utils.ToastUtils


//import cn.spacexc.wearbili.utils.TimeUtils
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.launch
//import androidx.lifecycle.lifecycleScope

const val APP_VERSION = "Rel-AL 0.21.0"

class AboutActivity : AppCompatActivity() {
    private val firstList = listOf(
        HorizontalButtonData(R.drawable.ic_outline_info_24, APP_VERSION, "版本号"),
        HorizontalButtonData(R.drawable.ic_github, "开源信息", "查看我们的开源项目"),
    )
    private val secondList = listOf(
        HorizontalButtonData(0, "uid480816699", "开发者 项目发起人"),
        HorizontalButtonData(0, "uid426907991", "图标设计师"),
        HorizontalButtonData(0, "uid293793435", "API仓库Owner"),
        HorizontalButtonData(
            R.drawable.akari,       //Rechrd's Avatar
            "虚位以待",     //Rechrd
            "UI/UA设计师"
        )      //Rechrd你无不无聊啊我还得多加一个逻辑我真的谢谢您嘞
        //pai233注：不做评价（
        //xc回复：不做评价还占个位置（
    )
    private val thirdList = listOf(
        HorizontalButtonData(0, "uid198338518", "设计组织"),
        HorizontalButtonData(0, "uid1463193869", "开发协力")
    )
    private val forthList = listOf(
        HorizontalButtonData(R.drawable.ic_github, "bilibili-API-collect", "SocialSisterYi"),
        HorizontalButtonData(R.drawable.ic_github, "bilimiao2", "10miaomiao")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        val recyclerView1 = findViewById<RecyclerView>(R.id.recyclerView1)
        val recyclerView2 = findViewById<RecyclerView>(R.id.recyclerView2)
        val recyclerView3 = findViewById<RecyclerView>(R.id.recyclerView3)
        val recyclerView4 = findViewById<RecyclerView>(R.id.recyclerView4)

        recyclerView1.layoutManager = LinearLayoutManager(this)
        recyclerView2.layoutManager = LinearLayoutManager(this)
        recyclerView3.layoutManager = LinearLayoutManager(this)
        recyclerView4.layoutManager = LinearLayoutManager(this)

        recyclerView1.adapter = HorizontalButtonAdapter(object : OnItemViewClickListener {
            override fun onClick(buttonName: String, viewHolder: RecyclerView.ViewHolder) {
                if (buttonName == "开源信息") {
                    Intent(this@AboutActivity, OpenSourceActivity::class.java).apply {
                        startActivity(this)
                    }
                }
            }

            override fun onLongClick(buttonName: String, viewHolder: RecyclerView.ViewHolder) {

            }
        }).also { it.submitList(firstList) }
        recyclerView2.adapter = UserHorizontalButtonAdapter(this).also { it.submitList(secondList) }
        recyclerView3.adapter = UserHorizontalButtonAdapter(this, listOf("uid198338518")) {
            if (it == "uid198338518") {
                startActivity(Intent(this, ToDesignInfoActivity::class.java))
            }
        }.also { it.submitList(thirdList) }
        recyclerView4.adapter = HorizontalButtonAdapter(object : OnItemViewClickListener {
            override fun onClick(buttonName: String, viewHolder: RecyclerView.ViewHolder) {
                val repoName = when (buttonName) {
                    "bilibili-API-collect" -> "SocialSisterYi/bilibili-API-collect"
                    "bilimiao2" -> "10miaomiao/bilimiao2"
                    else -> ""
                }
                val data: Uri = Uri.parse("weargit://repository/$repoName")
                val intent = Intent(Intent.ACTION_VIEW, data)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                try {
                    startActivity(intent)
                } catch (e: Exception) {
                    e.printStackTrace()
                    val clipboardManager: ClipboardManager =
                        ContextCompat.getSystemService(
                            this@AboutActivity,
                            ClipboardManager::class.java
                        ) as ClipboardManager
                    val clip: ClipData =
                        ClipData.newPlainText("repository", "https://github.com/$repoName")
                    clipboardManager.setPrimaryClip(clip)
                    ToastUtils.makeText("没有安装WearGit, 已复制仓库链接")
                        .show()
                }
            }

            override fun onLongClick(buttonName: String, viewHolder: RecyclerView.ViewHolder) {

            }
        }).also { it.submitList(forthList) }

        findViewById<TextView>(R.id.pageName).setOnClickListener { finish() }
//        lifecycleScope.launch {
//            while (true) {
//                findViewById<TextView>(R.id.timeText).text = TimeUtils.getCurrentTime()
//                delay(500)
//            }
//        }
//        pai233注：移除获取时间的协程，改为TextClock
    }
}