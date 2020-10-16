package iooojik.dev.qrreader.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import iooojik.dev.qrreader.AppСonstants.regex
import iooojik.dev.qrreader.BuildConfig
import iooojik.dev.qrreader.R
import java.io.File
import java.util.*


class QrDemoActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var text : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_qr_demo)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
        initialization()
    }

    private fun initialization(){
        //получаем текст, который был в qr
        val args = intent.extras
        if (args != null) {
            text = args.getString("decodeQR").toString()
        }
        //показываем найденный qr-код
        val img = findViewById<ImageView>(R.id.imageViewQR)
        img.setImageBitmap(encodeAsBitmap(text.toString(), BarcodeFormat.QR_CODE, 200, 200))
        //показываем текст
        val textView = findViewById<TextView>(R.id.decodedQR)
        //если текст - это ссылка, то открываем её в браузере
        val openInBrowser = findViewById<Button>(R.id.openInBrowser)
        if (!text.startsWith("https://") && !text.startsWith("http://")){
            openInBrowser.visibility = View.GONE
        } else openInBrowser.setOnClickListener(this)
        val buttonCopyText = findViewById<Button>(R.id.buttonCopyText)
        buttonCopyText.setOnClickListener(this)
        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener(this)
        if (text.startsWith("file:/")){
            val openFile = findViewById<Button>(R.id.openFile)
            openFile.visibility = View.VISIBLE
            openFile.setOnClickListener {
                openFileDir()
            }
            buttonCopyText.visibility = View.GONE
            textView.text = "Обнаружен файл. Вы можете открыть его по кнопке ниже."
        } else textView.text = text
    }

    private fun openFileDir(){
        try {
            val temp = text.split(regex)
            //Log.e("праььа", temp[0] + " " + temp[1])
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(Uri.parse(temp[0]), temp[1])
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(intent)
        } catch (e: Exception){
            Log.e("opening file error", e.toString())
        }
    }

    @Throws(WriterException::class)
    private fun encodeAsBitmap(
        contents: String,
        format: BarcodeFormat,
        img_width: Int,
        img_height: Int
    ): Bitmap? {
        //создание qr-кода
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
                pixels[offset + x] = if (result[x, y]) Color.BLACK else Color.WHITE
            }
        }
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
        return bitmap
    }

    override fun onClick(p0: View?) {
        when(p0!!.id){
            R.id.openInBrowser -> {
                var url = text
                if (!url.startsWith("https://") && !url.startsWith("http://")) {
                    url = "http://$url";
                }
                startActivity(
                    Intent.createChooser(
                        Intent(Intent.ACTION_VIEW, Uri.parse(url)),
                        "Открыть в..."
                    )
                )
            }
            R.id.buttonCopyText -> {
                val clipboard: ClipboardManager =
                    getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("", text)
                clipboard.setPrimaryClip(clip)
                Snackbar.make(p0, "Скопировано", Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(Color.parseColor("#24242b"))
                    .setTextColor(Color.parseColor("#ffffff")).show()
            }
            R.id.fab -> {
                finish()
            }
        }
    }
}