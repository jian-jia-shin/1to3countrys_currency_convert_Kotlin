package com.home.currency_converter_countrys_usekotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.home.currency_converter_countrys_usekotlin.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findView()
    }

    private fun findView() {
        val enterConverter = findViewById<Button>(R.id.enterConverter)
        val enterChanged  = findViewById<Button>(R.id.enterChanged)
        val enterAboutUse = findViewById<Button>(R.id.enterAboutUse)
        enterConverter.setOnClickListener {
            val intent = Intent()
            intent.setClass(this@MainActivity, Converter_Page::class.java)
            startActivity(intent)
        }
    }
}