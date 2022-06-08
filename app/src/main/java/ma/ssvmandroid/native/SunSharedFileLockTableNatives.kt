package ma.ssvmandroid.native

import dev.xdark.ssvm.VirtualMachine
import dev.xdark.ssvm.api.MethodInvoker
import dev.xdark.ssvm.execution.Result
import dev.xdark.ssvm.mirror.InstanceJavaClass
import org.objectweb.asm.Type

class SunSharedFileLockTableNatives : NativeInitializer {
    override fun init(vm: VirtualMachine) {
        val sharedFileLockIds = vm.findBootstrapClass("sun/nio/ch/SharedFileLockTable") as InstanceJavaClass

        vm.`interface`.setInvoker(
            sharedFileLockIds,
            "initIDs",
            Type.getMethodDescriptor(Type.VOID_TYPE),
            MethodInvoker.noop()
        )
    }
}