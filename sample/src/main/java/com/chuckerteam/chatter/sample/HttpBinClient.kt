package com.chatbooks.chatter.sample

import android.content.Context
import com.chatbooks.chatter.api.Chatter
import com.chatbooks.chatter.api.ChatterCollector
import com.chatbooks.chatter.api.ChatterInterceptor
import com.chatbooks.chatter.api.RetentionManager
import com.chatbooks.chatter.sample.HttpBinApi.Data
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val BASE_URL = "https://httpbin.org"

class HttpBinClient(
    context: Context
) {

    private val collector = ChatterCollector(
        context = context,
        showNotification = true,
        retentionPeriod = RetentionManager.Period.ONE_HOUR
    )

    private val chatterInterceptor = ChatterInterceptor(
        context = context,
        collector = collector,
        maxContentLength = 250000L
    )

    private val httpClient =
        OkHttpClient.Builder()
            // Add a ChatterInterceptor instance to your OkHttp client
            .addInterceptor(chatterInterceptor)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()

    private val api: HttpBinApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(httpClient)
            .build()
            .create(HttpBinApi::class.java)
    }

    @Suppress("MagicNumber")
    internal fun doHttpActivity() {
        val cb = object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                // no-op
            }
            override fun onFailure(call: Call<Void>, t: Throwable) { t.printStackTrace() }
        }

        with(api) {
            get().enqueue(cb)
            post(Data("posted")).enqueue(cb)
            patch(Data("patched")).enqueue(cb)
            put(Data("put")).enqueue(cb)
            delete().enqueue(cb)
            status(201).enqueue(cb)
            status(401).enqueue(cb)
            status(500).enqueue(cb)
            delay(9).enqueue(cb)
            delay(15).enqueue(cb)
            redirectTo("https://http2.akamai.com").enqueue(cb)
            redirect(3).enqueue(cb)
            redirectRelative(2).enqueue(cb)
            redirectAbsolute(4).enqueue(cb)
            stream(500).enqueue(cb)
            streamBytes(2048).enqueue(cb)
            image("image/png").enqueue(cb)
            gzip().enqueue(cb)
            xml().enqueue(cb)
            utf8().enqueue(cb)
            deflate().enqueue(cb)
            cookieSet("v").enqueue(cb)
            basicAuth("me", "pass").enqueue(cb)
            drip(512, 5, 1, 200).enqueue(cb)
            deny().enqueue(cb)
            cache("Mon").enqueue(cb)
            cache(30).enqueue(cb)
        }
    }

    internal fun initializeCrashHandler() {
        Chatter.registerDefaultCrashHandler(collector)
    }

    internal fun recordException() {
        collector.onError("Example button pressed", RuntimeException("User triggered the button"))
        // You can also throw exception, it will be caught thanks to "Chatter.registerDefaultCrashHandler"
        // throw new RuntimeException("User triggered the button");
    }
}
