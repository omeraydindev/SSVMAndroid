package ma.ssvmandroid

import android.util.Log
import dev.xdark.ssvm.VirtualMachine
import dev.xdark.ssvm.classloading.BootClassLoader
import dev.xdark.ssvm.classloading.ClassParseResult
import dev.xdark.ssvm.execution.VMException
import dev.xdark.ssvm.fs.HostFileDescriptorManager
import dev.xdark.ssvm.jvm.ManagementInterface
import dev.xdark.ssvm.mirror.InstanceJavaClass
import dev.xdark.ssvm.util.ClassUtil
import dev.xdark.ssvm.value.InstanceValue
import dev.xdark.ssvm.value.ObjectValue
import dev.xdark.ssvm.value.Value
import org.objectweb.asm.ClassReader
import java.io.File
import java.io.IOException
import java.io.PrintWriter
import java.io.StringWriter
import java.util.*
import java.util.zip.ZipFile

class SSVMTest(
    private val rtJar: ZipFile
) {
    private lateinit var vm: VirtualMachine

    private var initialized: Boolean = false
    val isInitialized: Boolean
        get() = initialized

    fun initVM() {
        vm = object : VirtualMachine() {
            override fun createFileDescriptorManager() = HostFileDescriptorManager()

            override fun createManagementInterface() = object : ManagementInterface {
                override fun getVersion() = "null"

                override fun getStartupTime() = System.currentTimeMillis()

                override fun getInputArguments() = listOf<String>()
            }

            override fun createBootClassLoader() = BootClassLoader {
                println("[VM] Loading: $it.class")

                try {
                    val entry = rtJar.getEntry("$it.class")
                        ?: return@BootClassLoader null

                    rtJar.getInputStream(entry).use { stream ->
                        val cr = ClassReader(stream)
                        val node = ClassUtil.readNode(cr)

                        return@BootClassLoader ClassParseResult(cr, node)
                    }
                } catch (e: IOException) {
                    System.err.println("[VM] Couldn't load class $it: ${Log.getStackTraceString(e)}")
                }

                return@BootClassLoader null
            }
        }

        vm.properties.apply {
            setProperty("sun.stderr.encoding", "UTF-8")
            setProperty("sun.stdout.encoding", "UTF-8")
            setProperty("sun.jnu.encoding", "UTF-8")
            setProperty("line.separator", "\n")
            setProperty("path.separator", ":")
            setProperty("file.separator", "/")
            setProperty("java.home", "/usr/lib/jvm")
            setProperty("user.home", "/home/mike")
            setProperty("user.dir", "/home/mike")
            setProperty("user.name", "mike")
            setProperty("os.version", "10.0")
            setProperty("os.arch", "amd64")
            setProperty("os.name", "Linux")
        }

        initialized = try {
            vm.bootstrap()

            // Don't enable JIT, it won't work in Android (see README.md)

            true
        } catch (e: Exception) {
            System.err.println("[VM] Couldn't start VM: ${Log.getStackTraceString(e)}")
            false
        }
    }

    private fun getVMSystemClassLoader(): Value {
        val ctx = vm.helper.invokeStatic(
            vm.symbols.java_lang_ClassLoader(),
            "getSystemClassLoader",
            "()Ljava/lang/ClassLoader;",
            arrayOf(),
            arrayOf(),
        )
        return ctx.result
    }

    fun addURL(jarFile: File) {
        // Add jar to system class loader
        val cl = getVMSystemClassLoader()

        addURL(cl, jarFile.absolutePath)
    }

    private fun addURL(loader: Value, jarPath: String) {
        val helper = vm.helper

        val fileClass = vm.findBootstrapClass("java/io/File", true) as InstanceJavaClass
        val fileInstance = vm.memoryManager.newInstance(fileClass)
        helper.invokeExact(
            fileClass,
            "<init>",
            "(Ljava/lang/String;)V",
            arrayOf(),
            arrayOf(fileInstance, helper.newUtf8(jarPath))
        )

        val uri = helper.invokeVirtual(
            "toURI",
            "()Ljava/net/URI;",
            arrayOf(),
            arrayOf(fileInstance)
        ).result

        val url = helper.invokeVirtual(
            "toURL",
            "()Ljava/net/URL;",
            arrayOf(),
            arrayOf(uri)
        ).result

        helper.invokeVirtual(
            "addURL",
            "(Ljava/net/URL;)V",
            arrayOf(),
            arrayOf(loader, url)
        )
    }

    fun invokeMainMethod(className: String) {
        val helper = vm.helper
        val symbols = vm.symbols

        try {
            val cl = getVMSystemClassLoader()

            val klass = helper.findClass(
                cl as ObjectValue,
                className,
                true
            ) as InstanceJavaClass

            val method = klass.getStaticMethod(
                "main",
                "([Ljava/lang/String;)V",
            )

            helper.invokeStatic(
                klass,
                method,
                arrayOf(),
                arrayOf(helper.emptyArray(symbols.java_lang_String()))
            )
        } catch (e: VMException) {
            helper.invokeVirtual(
                "printStackTrace",
                "()V",
                arrayOf(),
                arrayOf(e.oop)
            )
        }
    }

    fun release() {
        try {
            rtJar.close()
        } catch (e: Throwable) {
        }
    }

    companion object {
        fun throwableToString(throwable: InstanceValue): String? {
            Objects.requireNonNull(throwable, "throwable")
            val javaClass = throwable.javaClass
            val vm = javaClass.vm
            val helper = vm.helper
            try {
                val stringWriter = helper.newInstance(
                    vm.findBootstrapClass("java/io/StringWriter") as InstanceJavaClass,
                    "()V"
                )
                val printWriter = helper.newInstance(
                    vm.findBootstrapClass("java/io/PrintWriter") as InstanceJavaClass,
                    "(Ljava/io/Writer;)V",
                    stringWriter
                )
                helper.invokeVirtual(
                    "printStackTrace",
                    "(Ljava/io/PrintWriter;)V",
                    arrayOfNulls(0),
                    arrayOf<Value>(throwable, printWriter)
                )
                val throwableAsString = helper.invokeVirtual(
                    "toString",
                    "()Ljava/lang/String;",
                    arrayOfNulls(0),
                    arrayOf<Value>(stringWriter)
                ).result
                return helper.readUtf8(throwableAsString)
            } catch (ignored: VMException) {
            }
            val writer = StringWriter()
            helper.toJavaException(throwable).printStackTrace(PrintWriter(writer))
            return writer.toString()
        }
    }

}
