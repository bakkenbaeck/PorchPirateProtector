package no.bakkenbaeck.porchpirateprotector.activity

import androidx.appcompat.app.AppCompatActivity

import android.os.Bundle
import no.bakkenbaeck.porchpirateprotector.R
import no.bakkenbaeck.pppshared.api.HttpBinClient

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}
