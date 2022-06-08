package ma.ssvmandroid.native

import dev.xdark.ssvm.VirtualMachine
import dev.xdark.ssvm.api.MethodInvoker
import dev.xdark.ssvm.execution.Result
import dev.xdark.ssvm.mirror.InstanceJavaClass
import org.objectweb.asm.Type
import java.io.FileDescriptor

class SunFileKeyNatives : NativeInitializer {
    override fun init(vm: VirtualMachine) {
        val fileKey = vm.findBootstrapClass("sun/nio/ch/FileKey") as InstanceJavaClass

        vm.`interface`.setInvoker(
            fileKey,
            "initIDs",
            Type.getMethodDescriptor(Type.VOID_TYPE),
            MethodInvoker.noop()
        )

        vm.`interface`.setInvoker(
            fileKey,
            "init",
            Type.getMethodDescriptor(
                Type.VOID_TYPE,
                Type.getType(FileDescriptor::class.java)
            ),
            MethodInvoker.noop()
        )
    }
}