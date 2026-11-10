package com.steve.monopo

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(48, 48, 48, 48)
        }
        layout.addView(ImageView(this).apply { setImageResource(R.drawable.ic_steve) })
        layout.addView(TextView(this).apply {
            text = "Monopo"
            textSize = 26f
            gravity = Gravity.CENTER
        })
        layout.addView(Button(this).apply {
            text = "Activer la bulle"
            setOnClickListener { activerBulle() }
        })
        layout.addView(Button(this).apply {
            text = "Désactiver la bulle"
            setOnClickListener {
                stopService(Intent(this@MainActivity, BubbleService::class.java))
            }
        })
        layout.addView(Button(this).apply {
            text = "Synchroniser une application"
            setOnClickListener { startActivity(Intent(this@MainActivity, AppPickerActivity::class.java)) }
        })
        layout.addView(Button(this).apply {
            text = "Autoriser la détection des applis"
            setOnClickListener { startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)) }
        })
        setContentView(layout)
    }

    private fun activerBulle() {
        if (!Settings.canDrawOverlays(this)) {
            startActivity(
                Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            )
            Toast.makeText(
                this,
                "Autorise l'affichage par-dessus, puis reviens et réappuie",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        val intent = Intent(this, BubbleService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }
}
