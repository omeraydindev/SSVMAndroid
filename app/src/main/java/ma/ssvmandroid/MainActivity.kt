package ma.ssvmandroid

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import dev.xdark.ssvm.execution.VMException
import ma.ssvmandroid.databinding.ActivityMainBinding
import java.util.zip.ZipFile

class MainActivity : AppCompatActivity() {
    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MainViewModel by viewModels()

    private lateinit var ssvmTest: SSVMTest

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val rtJar = ZipFile(extractAsset(ASSET_RT_JAR))
        ssvmTest = SSVMTest(rtJar)

        binding.btnInitVM.setOnClickListener {
            if (ssvmTest.isInitialized) {
                binding.root.showSnackbar("VM is already initialized", "Reinit") {
                    initVM()
                }
            } else {
                initVM()
            }
        }

        binding.btnRunTest.setOnClickListener {
            if (!ssvmTest.isInitialized) {
                binding.root.showSnackbar("Initialize the VM first", "Init") {
                    initVM()
                }
            } else {
                invokeMain()
            }
        }

        viewModel.isRunningTask().observe(this) {
            binding.progressBar.isVisible = it
            binding.btnInitVM.isEnabled = !it
            binding.btnRunTest.isEnabled = !it
        }

        listenToStdout {
            runOnUiThread {
                binding.txLogs.append(it)
            }
            binding.scLogs.post {
                binding.scLogs.fullScroll(View.FOCUS_DOWN)
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        ssvmTest.release()
        super.onDestroy()
    }

    private fun initVM() {
        viewModel.runProgressTask {
            val time = System.currentTimeMillis()

            catchVMException {
                // init VM
                ssvmTest.initVM()

                // add test JAR
                val testJar = extractAsset(ASSET_TEST_JAR)
                ssvmTest.addURL(testJar)
            }

            println("[VM] Initialized in ${System.currentTimeMillis() - time} ms")
        }
    }

    private fun invokeMain() {
        viewModel.runProgressTask {
            catchVMException {
                ssvmTest.invokeMainMethod(TEST_CLASS_FQN)
            }
        }
    }

    private fun catchVMException(runnable: () -> Unit) {
        try {
            runnable()
        } catch (ex: Throwable) {
            val cause = ex.cause
            System.err.println(
                "[VM] VM exception: ${
                    if (cause is VMException)
                        SSVMTest.throwableToString(cause.oop)
                    else
                        Log.getStackTraceString(ex)
                }"
            )
        }
    }

    companion object {
        private const val ASSET_RT_JAR = "rt.jar"
        private const val ASSET_TEST_JAR = "Test.jar"
        private const val TEST_CLASS_FQN = "com/test/Test"
    }
}
