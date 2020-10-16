package iooojik.dev.qrreader.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import iooojik.dev.qrreader.R
import iooojik.dev.qrreader.qr.BarcodeCaptureActivity

class MenuActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        initialization()
    }

    private fun initialization() {
        requestPermissions()
        val buttonScanQR = findViewById<TextView>(R.id.buttonScanQR)
        val buttonCreateQR = findViewById<TextView>(R.id.buttonCreateQR)
        buttonCreateQR.setOnClickListener(this)
        buttonScanQR.setOnClickListener(this)
    }

    private fun requestPermissions() : Boolean{
        val perms = arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        //проверяем наличие разрешения на использование геолокации пользователя
        var permissionStatus = PackageManager.PERMISSION_GRANTED

        for (perm in perms) {
            if (ContextCompat.checkSelfPermission(
                    applicationContext,
                    perm
                ) == PackageManager.PERMISSION_DENIED
            ) {
                permissionStatus = PackageManager.PERMISSION_DENIED
                break
            }
        }
        if (permissionStatus != PackageManager.PERMISSION_GRANTED) ActivityCompat.requestPermissions(
            this,
            perms,
            1
        ) else return true
        return false
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.buttonScanQR -> {
                if (requestPermissions())
                    startActivity(Intent(applicationContext, BarcodeCaptureActivity::class.java))
            }

            R.id.buttonCreateQR -> {
                if (requestPermissions())
                    startActivity(Intent(applicationContext, CreateQRActivity::class.java))
            }
        }
    }
}