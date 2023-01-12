package com.hairysnow.play.integrity.api.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import com.hairysnow.play.integrity.api.core.PlayIntegrityHelper
import com.hairysnow.play.integrity.api.core.configuration.IntegrityConfiguration
import com.hairysnow.play.integrity.api.core.exception.IntegrityException
import com.hairysnow.play.integrity.api.core.result.IntegrityResult
import java.util.HashMap

class MainActivity : AppCompatActivity() {
    private lateinit var playIntegrityHelper: PlayIntegrityHelper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val sslContextTrustManager = HttpSslUtils.getSSLContextTrustManager(this)
        PlayIntegrityHelper(
            IntegrityConfiguration.Builder(
                this,
                "https://baidu.com",
                30_000,
                true,
                null,
                sslContextTrustManager.mSSLContext.socketFactory,
                sslContextTrustManager.mX509TrustManager,
                true,

            ).build()
        ).also { playIntegrityHelper = it }
    }

    fun request(view: View) {
        playIntegrityHelper.requestIntegrityToken(object :
            PlayIntegrityHelper.OnIntegrityResultListener {
            override fun onSuccess(
                integrityResult: IntegrityResult,
                responseString: String
            ) {

            }

            override fun onFailure(e: IntegrityException) {
            }

        })
//        playIntegrityHelper.requestBackendIntegrityValidation("eyJhbGciOiJBMjU2S1ciLCJlbmMiOiJBMjU2R0NNIn0.kuxNw8b0tpIwZ4EHz2J0ZfftGdCICIxnVnPoXfIjLdaOTtUxQkT8wQ.Jr27eAhWI4BtHbk8.C8bcTnk5uv_oOfE8hKKebku-ApLrNYmRRzVpafkWplIoR7fupeiMg6Jn3U8eKRuNJBy5qBJ40e1zoa5JQ0HGxgn-ImAC1Y4kyBuFruM9S3yhX0NsSbANx6BYl-fPEf-MHMPPUHIfdvmqNw7kPO169NTI5Ipr-6xQFZ-pb7TZZ0FvPc2DRNo3bX9DLaWD-AV5gT6dkKO_tQBu5q3VfxVw9ec2mRIuQKWOzpTFbXYhuTaD-91Xlu7LObK_HwRPEtO2svubO1dOIIdOxisfzSjuZvvSkIxiqZokNWvA_OuoBJbtxxMt6fs_n85QWbnI-rC2HtZHXgUSzEGLHdOCSbcO5x2--i1uNF87rkiBvjqqcmhLCucROr6ApytKsFh1So0zP9NVSKozTy4VYiEU2C4saQc3mdKa8lMzHOsBDuwepOJDQ1IV5vbDUxOC3V8YFw1sxqzV3F8fRXjHRGYrBHDoll9XWZpzu0WuP6WmaT3un1CM38FfKy8IBCedbXlo9tsCXPFB3jAOdylu6bKLNYX24SAQqZHLtAMwllaY4W52eg29_qc31RIUh7WMFGsc4TOd1Qgls4OKI3G3gteiTmTMK9tBA7jODi3Q-Y52uy9eJnDIgZF01NjzPah5LuvOsQUftZ65apgDL25U91wOxuOtryD7aC8lkvj3u-0vO7M81EGPJpg2gQJuAMtrvJYH8I20HYsLMa7-zzIG2X6uUGII2NznN3o_N8fB1___k_lS5y3eTukDoXHM7ZFZbruRyR3ykQSTzKV5VRjzoaeV2H-VHM4HRMYfxwXZN8aVT30FySclB8ZcmiWDfO2D2y25LZA5ZM5CIZEoahUb_rZC5T_xe2skhTz0bQI-Sj2o2baOTJrzM3BnStJqvVApsXNNl90n_DD4Z7-lsnj9w1mrUM96UpnFHgyaSrI0FomuEQGft7BfRKOa3TQfYaq7_RIn3tCW4YJudZ1vssutiJ-__6aLNo18wZUh6IlNApQAzrNHTt9uJfywzZKrP8RusGnSqURZzvRbRa1iJq1OAEGP9-tXhOaxwdf2GPFMfrkJzHwF36Yz4kSMi2TMtLJievg-GMfBHit7pT55YOBXfHQQxKT7-U-z8C4jT087nGQCeyzs3JM_GUpARTEdPWisBp0JTPjAUkOWf4a654uWaY1WwnAJiZyfbVvFTYxU7jXqNSw4L8WNLGZUw7mTJOnNGLFTkoZdCSECi8zijmhdPEg.GQggwlq0wdKAdBQcPR0_ww",
//            object : PlayIntegrityHelper.OnIntegrityResultListener {
//                override fun onSuccess(integrityResult: IntegrityResult, responseString: String) {
//                    Log.e("MainActivity", "onSuccess: "+responseString)
//                }
//
//                override fun onFailure(e: IntegrityException) {
//                    Log.e("MainActivity", "onFailure: "+e)
//                }
//
//            }
//        )
    }
}