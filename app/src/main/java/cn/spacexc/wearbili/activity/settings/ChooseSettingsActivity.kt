package cn.spacexc.wearbili.activity.settings

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.adapter.ChooseSettingsAdapter
import cn.spacexc.wearbili.dataclass.settings.SettingItem
//import cn.spacexc.wearbili.utils.TimeUtils
//import kotlinx.coroutines.delay
//import androidx.lifecycle.lifecycleScope

class ChooseSettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_settings)
        val item = (intent.getParcelableExtra<SettingItem>("item"))!!
        val adapter = ChooseSettingsAdapter(
            item.settingName,
            item.defString
        )
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter.apply { submitList(item.chooseItems) }
        val pageName = findViewById<TextView>(R.id.pageName)
        val back = findViewById<TextView>(R.id.back)
        val description = findViewById<TextView>(R.id.description)
        pageName.text = item.displayName
        description.text = item.description
        back.setOnClickListener { finish() }
//        val timeText = findViewById<TextView>(R.id.timeText)
//        lifecycleScope.launchWhenCreated {
//            while (true) {
//                timeText.text = TimeUtils.getCurrentTime()
//                delay(500)
//            }
//        }
//        pai233注：移除有关获取时间的协程，改为TextClock
    }
}