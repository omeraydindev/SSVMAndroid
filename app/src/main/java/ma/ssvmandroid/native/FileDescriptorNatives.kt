package ma.ssvmandroid.native

import dev.xdark.ssvm.VirtualMachine
import dev.xdark.ssvm.api.MethodInvoker
import dev.xdark.ssvm.mirror.InstanceJavaClass
import org.objectweb.asm.Type

class FileDescriptorNatives : NativeInitializer {
    override fun init(vm: VirtualMachine) {
        val fileDescriptor = vm.findBootstrapClass("java/io/FileDescriptor") as InstanceJavaClass

        vm.`interface`.setInvoker(
            fileDescriptor,
            "init",
            Type.getMethodDescriptor(Type.VOID_TYPE),
            MethodInvoker.noop()
        )
    }
}