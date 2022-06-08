package ma.ssvmandroid.native

import dev.xdark.ssvm.VirtualMachine
import dev.xdark.ssvm.api.MethodInvoker
import dev.xdark.ssvm.execution.Result
import dev.xdark.ssvm.mirror.InstanceJavaClass
import dev.xdark.ssvm.value.IntValue
import org.objectweb.asm.Type
import java.io.FileDescriptor

class SunFileDispatcherNatives : NativeInitializer {
    override fun init(vm: VirtualMachine) {
        val fileDispatcher = vm.findBootstrapClass("sun/nio/ch/FileDispatcherImpl") as InstanceJavaClass

        // no-op, handled in real vm
        vm.`interface`.setInvoker(
            fileDispatcher,
            "init",
            Type.getMethodDescriptor(Type.VOID_TYPE),
            MethodInvoker.noop()
        )

        vm.`interface`.setInvoker(
            fileDispatcher,
            "release0",
            Type.getMethodDescriptor(
                Type.VOID_TYPE,
                Type.getType(FileDescriptor::class.java),
                Type.LONG_TYPE,
                Type.LONG_TYPE)
        ) {
            // Not sure what to delegate here
            return@setInvoker Result.ABORT
        }

        vm.`interface`.setInvoker(
            fileDispatcher,
            "lock0",
            Type.getMethodDescriptor(
                Type.INT_TYPE,
                Type.getType(FileDescriptor::class.java),
                Type.BOOLEAN_TYPE,
                Type.LONG_TYPE,
                Type.LONG_TYPE,
                Type.BOOLEAN_TYPE)
        ) {
            it.result = IntValue.of(0)
            return@setInvoker Result.ABORT
        }
    }
}