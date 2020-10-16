package iooojik.dev.qrreader.ui

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Color.BLACK
import android.graphics.Color.WHITE
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import iooojik.dev.qrreader.R
import java.util.*


class CreateQRActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_qr)
        initialization()
    }

    private fun initialization() {
        val buttonCreateQR = findViewById<Button>(R.id.buttonCreateQR)
        buttonCreateQR.setOnClickListener(this)
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener(this)
    }

    @Throws(WriterException::class)
    private fun encodeAsBitmap(
        contents: String,
        format: BarcodeFormat,
        img_width: Int,
        img_height: Int
    ): Bitmap? {
        val contentsToEncode = contents ?: return null
        var hints: Map<EncodeHintType?, Any?>? = null
        val encoding: String = "UTF-8"
        hints = EnumMap(EncodeHintType::class.java)
        hints.put(EncodeHintType.CHARACTER_SET, encoding)
        val writer = MultiFormatWriter()
        val result: BitMatrix
        result = try {
            writer.encode(contentsToEncode, format, img_width, img_height, hints)
        } catch (iae: IllegalArgumentException) {
            // Unsupported format
            return null
        }
        val width = result.width
        val height = result.height
        val pixels = IntArray(width * height)
        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (result[x, y]) BLACK else WHITE
            }
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }


    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.buttonCreateQR -> {
                val textField = findViewById<EditText>(R.id.textField)
                if (!textField.text.isNullOrEmpty()) {
                    val imgQR = findViewById<ImageView>(R.id.imageViewQR)
                    imgQR.visibility = View.VISIBLE
                    val bitmap = encodeAsBitmap(textField.text.toString(), BarcodeFormat.QR_CODE, 200, 200)
                    imgQR.setImageBitmap(bitmap)
                    val buttonShare = findViewById<Button>(R.id.buttonShare)
                    buttonShare.setOnClickListener {
                        if (isExternalStorageWritable()) {
                            if (bitmap != null) {
                                val imgSaved = MediaStore.Images.Media.insertImage(
                                    contentResolver, bitmap,
                                    UUID.randomUUID().toString() + ".png", "drawing note"
                                )
                                shareImageUri(Uri.parse(imgSaved))
                            }
                        }
                    }
                    buttonShare.visibility = View.VISIBLE
                } else Snackbar.make(p0, "Вы не ввели текст", Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(Color.parseColor("#24242b"))
                    .setTextColor(Color.parseColor("#ffffff")).show()
            }

            R.id.fab -> {
                finish()
            }
        }
    }

    private fun isExternalStorageWritable(): Boolean {
        val state = Environment.getExternalStorageState()
        return Environment.MEDIA_MOUNTED == state
    }

    private fun shareImageUri(uri: Uri) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_STREAM, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.type = "image/png"
        startActivity(intent)
    }
}