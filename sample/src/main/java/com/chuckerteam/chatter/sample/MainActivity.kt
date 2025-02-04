package com.chatbooks.chatter.sample

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.chatbooks.chatter.api.Chatter
import com.chatbooks.chatter.api.ChatterCollector
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        val SCREEN_SESSION = Chatter.Screen("Session", Chatter.ScreenType.GENERIC)
        val SCREEN_HTTP = Chatter.Screen("HTTP", Chatter.ScreenType.HTTP)
        val SCREEN_CRASHES = Chatter.Screen("Crashes", Chatter.ScreenType.CRASHES)
        val SCREEN_EVENTS = Chatter.Screen("Events", Chatter.ScreenType.GENERIC)
        val SCREEN_APP_STRINGS = Chatter.Screen("AppStrings", Chatter.ScreenType.GENERIC)
    }

    private lateinit var client: HttpBinClient

    var toggle = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val screens = listOf(
                SCREEN_SESSION,
                SCREEN_HTTP,
                SCREEN_CRASHES,
                SCREEN_EVENTS,
                SCREEN_APP_STRINGS
        )
        Chatter.initChatter(screens)

        client = HttpBinClient(applicationContext)

        do_http.setOnClickListener { client.doHttpActivity() }
        trigger_exception.setOnClickListener { client.recordException() }

        val collector = ChatterCollector(this)
        triggerGeneric.setOnClickListener {
            if (toggle) {
                toggle = !toggle
                collector.onGeneric(SCREEN_EVENTS, "Generic Title", "generic subtitle", "some message", "Some content that could potentially be very long")
            } else {
                toggle = !toggle
                collector.onGeneric(SCREEN_SESSION, "Generic Session Title", "generic session subtitle", "some message", "Some content that could potentially be very long")
            }
        }


        with(launch_chatter_directly) {
            visibility = if (Chatter.isOp) View.VISIBLE else View.GONE
            setOnClickListener { launchChatterDirectly() }
        }

        client.initializeCrashHandler()
    }

    private fun launchChatterDirectly() {
        // Optionally launch Chatter directly from your own app UI
        startActivity(Chatter.getLaunchIntent(this, SCREEN_HTTP))
    }
}
