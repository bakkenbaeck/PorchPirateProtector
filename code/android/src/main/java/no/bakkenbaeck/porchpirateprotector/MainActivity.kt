package no.bakkenbaeck.porchpirateprotector

import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import android.widget.TextView
import no.bakkenbaeck.pppshared.createApplicationScreenMessage

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<TextView>(R.id.main_text).text = createApplicationScreenMessage()
    }
}
