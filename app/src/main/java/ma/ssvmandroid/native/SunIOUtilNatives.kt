package ma.ssvmandroid.native

import dev.xdark.ssvm.VirtualMachine
import dev.xdark.ssvm.api.MethodInvoker
import dev.xdark.ssvm.execution.Result
import dev.xdark.ssvm.mirror.InstanceJavaClass
import dev.xdark.ssvm.value.IntValue
import org.objectweb.asm.Type

class SunIOUtilNatives : NativeInitializer {
    override fun init(vm: VirtualMachine) {
        val ioUtils = vm.findBootstrapClass("sun/nio/ch/IOUtil") as InstanceJavaClass

        // no-op, already handled in the real vm
        vm.`interface`.setInvoker(
            ioUtils,
            "initIDs",
            Type.getMethodDescriptor(Type.VOID_TYPE),
            MethodInvoker.noop()
        )

        vm.`interface`.setInvoker(
            ioUtils,
            "iovMax",
            Type.getMethodDescriptor(Type.INT_TYPE)
        ) {
            // Not sure if its correct to return a random value here
            it.result = IntValue.of(0)
            return@setInvoker Result.ABORT
        }
    }
}