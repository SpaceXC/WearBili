package cn.spacexc.wearbili.activity.settings

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.spacexc.wearbili.R
import cn.spacexc.wearbili.adapter.SettingsAdapter
import cn.spacexc.wearbili.manager.SettingsManager.settingsList
import cn.spacexc.wearbili.utils.TimeUtils
import kotlinx.coroutines.delay

class SettingsActivity : AppCompatActivity() {

    val adapter = SettingsAdapter(this)
    lateinit var pageName: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter.apply { submitList(settingsList) }
        pageName = findViewById(R.id.pageName)
        val timeText = findViewById<TextView>(R.id.timeText)
        pageName.setOnClickListener { finish() }
        lifecycleScope.launchWhenCreated {
            while (true) {
                timeText.text = TimeUtils.getCurrentTime()
                delay(500)
            }
        }
    }
}