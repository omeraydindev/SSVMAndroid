package ma.ssvmandroid

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.OutputStream
import java.io.PrintStream
import java.util.concurrent.Executors

class MainViewModel: ViewModel() {
    private val executor = Executors.newSingleThreadExecutor()

    private val isRunningTask = MutableLiveData(false)

    fun isRunningTask(): LiveData<Boolean> {
        return isRunningTask
    }

    fun runProgressTask(
        runnable: () -> Unit,
    ) {
        // don't interrupt existing task
        if (isRunningTask.value == true) {
            return
        }

        isRunningTask.value = true

        executor.execute {
            runnable()

            isRunningTask.postValue(false)
        }
    }
}
