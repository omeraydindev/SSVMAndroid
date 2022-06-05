package ma.ssvmandroid

import android.content.Context
import android.view.View
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.OutputStream
import java.io.PrintStream

fun Context.extractAsset(assetName: String) =
    File(filesDir, assetName).let { file ->
        assets.open(assetName).use { inputStream ->
            file.outputStream().use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        file
    }

fun listenToStdout(
    callback: (String) -> Unit,
) {
    val ps = PrintStream(object : OutputStream() {
        private val sb = StringBuilder()

        override fun write(p0: Int) {
            val c = p0.toChar()
            sb.append(c)

            if (c == '\n') {
                callback(sb.toString())
                sb.setLength(0)
            }
        }
    })

    System.setOut(ps)
    System.setErr(ps)
}

fun View.showSnackbar(
    message: String,
    actionText: String,
    actionCallback: (View) -> Unit,
) {
    val snackbar = Snackbar.make(this, message, Snackbar.LENGTH_SHORT)
    snackbar.setAction(actionText, actionCallback)
    snackbar.show()
}
