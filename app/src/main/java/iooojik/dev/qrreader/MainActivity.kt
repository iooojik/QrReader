package iooojik.dev.qrreader

import android.content.Intent
import android.os.Bundle
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import androidx.appcompat.app.AppCompatActivity
import iooojik.dev.qrreader.ui.MenuActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val builder = VmPolicy.Builder()
        StrictMode.setVmPolicy(builder.build())
        startActivity(Intent(applicationContext, MenuActivity::class.java))
    }
}