package ma.ssvmandroid.native

import dev.xdark.ssvm.VirtualMachine
import dev.xdark.ssvm.api.MethodInvoker
import dev.xdark.ssvm.execution.Result
import dev.xdark.ssvm.mirror.InstanceJavaClass
import dev.xdark.ssvm.value.LongValue
import org.objectweb.asm.Type

class SunNativeThreadNatives : NativeInitializer {
    override fun init(vm: VirtualMachine) {
        val nativeThread = vm.findBootstrapClass("sun/nio/ch/NativeThread") as InstanceJavaClass

        vm.`interface`.setInvoker(
            nativeThread,
            "init",
            Type.getMethodDescriptor(Type.VOID_TYPE),
            MethodInvoker.noop()
        )

        vm.`interface`.setInvoker(
            nativeThread,
            "current",
            Type.getMethodDescriptor(Type.LONG_TYPE)
        ) {
            // TODO: not sure what value to return here
            it.result = LongValue.of(0)
            return@setInvoker Result.ABORT
        }
    }
}