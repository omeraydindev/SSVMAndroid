package ma.ssvmandroid.native

import dev.xdark.ssvm.VirtualMachine
import dev.xdark.ssvm.execution.Result
import dev.xdark.ssvm.mirror.InstanceJavaClass
import dev.xdark.ssvm.value.LongValue
import org.objectweb.asm.Type

class FileChannelImplNatives : NativeInitializer {
    override fun init(vm: VirtualMachine) {
        val fileChannelImpl = vm.findBootstrapClass("sun/nio/ch/FileChannelImpl") as InstanceJavaClass

        vm.`interface`.setInvoker(
            fileChannelImpl,
            "initIDs",
            Type.getMethodDescriptor(Type.LONG_TYPE)
        ) {
            it.result = LongValue.of(0)
            return@setInvoker Result.ABORT
        }
    }

    operator fun invoke(vm: VirtualMachine) {
        init(vm)
    }
}