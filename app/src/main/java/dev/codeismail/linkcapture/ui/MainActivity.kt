package dev.codeismail.linkcapture.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dev.codeismail.linkcapture.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
