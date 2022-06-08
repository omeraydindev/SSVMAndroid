package ma.ssvmandroid.native

import dev.xdark.ssvm.VirtualMachine
import dev.xdark.ssvm.execution.Result
import dev.xdark.ssvm.mirror.InstanceJavaClass
import dev.xdark.ssvm.value.InstanceValue
import dev.xdark.ssvm.value.IntValue
import dev.xdark.ssvm.value.Value
import org.objectweb.asm.Type
import java.io.File

class OldFileSystemNativesEx : NativeInitializer {
    override fun init(vm: VirtualMachine) {
        val fs: InstanceJavaClass = (vm.findBootstrapClass("java/io/WinNTFileSystem")
                ?: vm.findBootstrapClass("java/io/UnixFileSystem")) as InstanceJavaClass

        vm.`interface`.setInvoker(
            fs,
            "checkAccess",
            Type.getMethodDescriptor(
                Type.BOOLEAN_TYPE,
                Type.getType(File::class.java),
                Type.INT_TYPE
            )
        ) {
            val value = it.locals.load<Value>(1)
            val path: String =
                vm.helper.readUtf8((value as InstanceValue).getValue("path", "Ljava/lang/String;"))
            val result = if (File(path).canRead()) 1 else 0
            it.result = IntValue.of(result)
            return@setInvoker Result.ABORT
        }

        vm.`interface`.setInvoker(
            fs,
            "delete0",
            Type.getMethodDescriptor(Type.BOOLEAN_TYPE, Type.getType(File::class.java))
        ) {
            val value = it.locals.load<Value>(1)
            val path: String =
                vm.helper.readUtf8((value as InstanceValue).getValue("path", "Ljava/lang/String;"))
            val result = if (File(path).delete()) 1 else 0
            it.result = IntValue.of(result)
            return@setInvoker Result.ABORT
        }

        vm.`interface`.setInvoker(
            fs, "createDirectory", Type.getMethodDescriptor(
                Type.BOOLEAN_TYPE, Type.getType(File::class.java)
            )
        ) {
            val value = it.locals.load<Value>(1)
            val path: String =
                vm.helper.readUtf8((value as InstanceValue).getValue("path", "Ljava/lang/String;"))
            val result = if (File(path).mkdir()) 1 else 0
            it.result = IntValue.of(result)
            return@setInvoker Result.ABORT
        }
    }


}