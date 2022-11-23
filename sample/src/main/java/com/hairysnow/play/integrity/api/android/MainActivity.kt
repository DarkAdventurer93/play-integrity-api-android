package com.hairysnow.play.integrity.api.android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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
        playIntegrityHelper = PlayIntegrityHelper(
            IntegrityConfiguration.Builder(
                this,
                "https://www.baidu.com",
                15_000,
                true
            ).build()
        )
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
    }
}