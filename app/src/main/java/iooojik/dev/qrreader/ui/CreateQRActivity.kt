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
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
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
import iooojik.dev.qrreader.AppСonstants
import iooojik.dev.qrreader.AppСonstants.regex
import iooojik.dev.qrreader.R
import ir.esfandune.filepickerDialog.ui.PickerDialog
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
        val addURLtoQR = findViewById<Button>(R.id.addURLtoQR)
        addURLtoQR.setOnClickListener(this)
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
                createQR(p0, textField.text.toString())
                textField.clearFocus()
                window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
                hideKeyboard()
            }

            R.id.fab -> {
                finish()
                hideKeyboard()
            }

            R.id.addURLtoQR -> {
                PickerDialog.FilePicker(this).onFileSelect { clickedFile ->
                    createQR(p0, Uri.fromFile(clickedFile).toString() + regex +
                            MimeTypeMap.getSingleton().
                            getMimeTypeFromExtension(MimeTypeMap.
                            getFileExtensionFromUrl(clickedFile.toURI().toString()).toString()))
                }
            }
        }
    }
    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)
    }


    private fun createQR(p0: View?, text: String){

        if (!text.isNullOrEmpty()) {
            val imgQR = findViewById<ImageView>(R.id.imageViewQR)
            imgQR.visibility = View.VISIBLE
            val bitmap = encodeAsBitmap(
                text,
                BarcodeFormat.QR_CODE,
                200,
                200
            )
            imgQR.setImageBitmap(bitmap)
            val buttonShare = findViewById<Button>(R.id.buttonShare)
            buttonShare.setOnClickListener {
                if (isExternalStorageWritable()) {
                    if (bitmap != null) {
                        val imgSaved = MediaStore.Images.Media.insertImage(
                            contentResolver, bitmap,
                            UUID.randomUUID().toString() + ".png", "saved qr"
                        )
                        shareImageUri(Uri.parse(imgSaved))
                    }
                }
            }
            buttonShare.visibility = View.VISIBLE

        } else p0?.let {
            Snackbar.make(it, "Вы не ввели текст", Snackbar.LENGTH_SHORT)
                .setBackgroundTint(Color.parseColor("#24242b"))
                .setTextColor(Color.parseColor("#ffffff")).show()
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