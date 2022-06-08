package ma.ssvmandroid.native

import dev.xdark.ssvm.VirtualMachine
import dev.xdark.ssvm.api.MethodInvoker
import dev.xdark.ssvm.execution.Result
import dev.xdark.ssvm.mirror.InstanceJavaClass
import dev.xdark.ssvm.value.IntValue
import dev.xdark.ssvm.value.Value
import org.objectweb.asm.Type

class RandomAccessFileNatives : NativeInitializer {
    override fun init(vm: VirtualMachine) {
        val randomAccessFile = vm.findBootstrapClass("java/io/RandomAccessFile") as InstanceJavaClass

        vm.`interface`.setInvoker(
            randomAccessFile,
            "initIDs",
            Type.getMethodDescriptor(Type.VOID_TYPE),
            MethodInvoker.noop()
        )

        vm.`interface`.setInvoker(
            randomAccessFile,
            "open",
            Type.getMethodDescriptor(
                Type.VOID_TYPE,
                Type.getType(String::class.java),
                Type.INT_TYPE
            )
        ) {
            val value = it.locals.load<Value>(1)
            val path: String = vm.helper.readUtf8(value)
            val modeValue = it.locals.load<IntValue>(2)
            vm.fileDescriptorManager.open(path, modeValue.asInt())
            return@setInvoker Result.ABORT
        }

        // no-op, handled in the open() method
        vm.`interface`.setInvoker(
            randomAccessFile,
            "open0",
            Type.getMethodDescriptor(Type.VOID_TYPE, Type.INT_TYPE),
            MethodInvoker.noop()
        )

        vm.`interface`.setInvoker(
            randomAccessFile,
            "close0",
            Type.getMethodDescriptor(Type.VOID_TYPE)
        ) {
            // TODO: read the path field on the random access file and close that
            return@setInvoker Result.ABORT
        }
    }
}