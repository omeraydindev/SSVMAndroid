package ma.ssvmandroid

import android.app.ProgressDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ma.ssvmandroid.databinding.ActivityMainBinding
import java.io.File
import java.io.OutputStream
import java.io.PrintStream
import java.lang.StringBuilder
import java.util.concurrent.Executors
import java.util.zip.ZipFile

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var ssvmTest: SSVMTest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        listenToStd()

        val rtJar = ZipFile(extractAsset(ASSET_RT_JAR))
        ssvmTest = SSVMTest(rtJar)

        binding.btnInitVM.setOnClickListener {
            if (ssvmTest.isInitialized) {
                toast("VM already initialized")
                return@setOnClickListener
            }

            initVM()
        }

        binding.btnRunTest.setOnClickListener {
            if (!ssvmTest.isInitialized) {
                toast("Initialize the VM first")
                return@setOnClickListener
            }

            ssvmTest.invokeMainMethod(TEST_CLASS_FQN)
        }
    }

    override fun onDestroy() {
        _binding = null
        ssvmTest.release()
        super.onDestroy()
    }

    private fun listenToStd() {
        val ps = PrintStream(object : OutputStream() {
            val sb = StringBuilder()

            override fun write(p0: Int) {
                val c = p0.toChar()
                sb.append(c)

                if (c == '\n') {
                    runOnUiThread {
                        binding.txLogs.append(sb.toString())
                        binding.scLogs.fullScroll(View.FOCUS_DOWN)
                        sb.setLength(0)
                    }
                }
            }
        })

        System.setOut(ps)
        System.setErr(ps)
    }

    private fun initVM() {
        @Suppress("DEPRECATION")
        val dialog = ProgressDialog.show(this, "SSVM",
            "Initializing", true, false)

        Executors.newSingleThreadExecutor().execute {
            ssvmTest.initVM()

            val testJar = extractAsset(ASSET_TEST_JAR)
            ssvmTest.addURL(testJar)

            runOnUiThread {
                dialog.dismiss()
                toast("Done")
            }
        }
    }

    private fun toast(text: String) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

    private fun extractAsset(assetName: String) =
        File(filesDir, assetName).let { file ->
            assets.open(assetName).use { inputStream ->
                file.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
            file
        }

    companion object {
        private const val TAG = "MainActivity"

        private const val ASSET_RT_JAR = "rt.jar"
        private const val ASSET_TEST_JAR = "Test.jar"
        private const val TEST_CLASS_FQN = "com/test/Test"
    }
}
