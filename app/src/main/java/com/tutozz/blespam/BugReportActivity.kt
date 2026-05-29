package com.tutozz.blespam

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class BugReportActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bug)
    }

    fun openSocialLink(view: View) {
        try {
            val url = AppConfig.SOCIAL_LINK
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } catch (e: Exception) {
            // ignore
        }
    }

    fun sendReport(view: View) {
        finish()
    }
}
