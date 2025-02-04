package com.chatbooks.chatter.api

import android.content.Context
import android.content.Intent
import com.chatbooks.chatter.internal.support.ChatterCrashHandler
import com.chatbooks.chatter.internal.support.NotificationHelper
import com.chatbooks.chatter.internal.ui.MainActivity

/**
 * Chatter methods and utilities to interact with the library.
 */
object Chatter {

    /**
     * Check if this instance is the operation one or no-op.
     * @return `true` if this is the operation instance.
     */
    val isOp = true

    val SCREEN_HTTP = Screen("HTTP", ScreenType.HTTP)
    val SCREEN_CRASHES = Screen("Crashes", ScreenType.CRASHES)
    private val defaultScreens = listOf(
            SCREEN_HTTP,
            SCREEN_CRASHES
    )

    /**
     * Set the screens that will be supported in the app.
     * Screens must have unique name.
     * Default to HTTP and Crashes
     */
    fun initChatter(screens: List<Screen>) {
        this.screens = screens
    }

    /**
     * Get an Intent to launch the Chatter UI directly.
     * @param context An Android [Context].
     * @param screen The [Screen] to display: SCREEN_HTTP or SCREEN_ERROR.
     * @return An Intent for the main Chatter Activity that can be started with [Context.startActivity].
     */
    @JvmStatic
    fun getLaunchIntent(context: Context, screen: Screen): Intent {
        return Intent(context, MainActivity::class.java)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(MainActivity.EXTRA_SCREEN, screen.name)
    }

    /**
     * Configure the default crash handler of the JVM to report all uncaught [Throwable] to Chatter.
     * You may only use it for debugging purpose.
     *
     * @param collector the ChatterCollector
     */
    @JvmStatic
    fun registerDefaultCrashHandler(collector: ChatterCollector) {
        Thread.setDefaultUncaughtExceptionHandler(ChatterCrashHandler(collector))
    }

    /**
     * Method to dismiss the Chatter notification of HTTP Transactions
     */
    @JvmStatic
    fun dismissTransactionsNotification(context: Context) {
        NotificationHelper(context).dismissTransactionsNotification()
    }

    /**
     * Method to dismiss the Chatter notification of Uncaught Errors.
     */
    @JvmStatic
    fun dismissErrorsNotification(context: Context) {
        NotificationHelper(context).dismissErrorsNotification()
    }

    enum class ScreenType {
        HTTP,
        CRASHES,
        GENERIC,
    }

    var screens: List<Screen> = defaultScreens

    class Screen(
            val name: String,
            val screenType: ScreenType
    )

    internal const val LOG_TAG = "Chatter"
}
