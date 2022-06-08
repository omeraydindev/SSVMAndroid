package ma.ssvmandroid

import dev.xdark.ssvm.VMInitializer
import dev.xdark.ssvm.VirtualMachine
import ma.ssvmandroid.native.*

class DefaultVMInitializer : VMInitializer {

    private val nativeInitializers = setOf(
        FileChannelImplNatives(),
        FileDescriptorNatives(),
        RandomAccessFileNatives(),
        OldFileSystemNativesEx(),
        SunFileDispatcherNatives(),
        SunFileKeyNatives(),
        SunSharedFileLockTableNatives(),
        SunIOUtilNatives(),
        SunNativeThreadNatives()
    )

    override fun initBegin(vm: VirtualMachine) {
        vm.properties.apply {
            setProperty("sun.stderr.encoding", "UTF-8")
            setProperty("sun.stdout.encoding", "UTF-8")
            setProperty("sun.jnu.encoding", "UTF-8")
            setProperty("line.separator", "\n")
            setProperty("path.separator", ":")
            setProperty("file.separator", "/")
            setProperty("user.dir", "/home/mike")
            setProperty("user.name", "mike")
            setProperty("os.version", "10.0")
            setProperty("os.arch", "amd64")
            setProperty("os.name", "Linux")
            setProperty("file.encoding", "UTF-8")
        }

        // Contains android stuffs
        vm.getenv().clear()
    }

    override fun nativeInit(vm: VirtualMachine) {
        nativeInitializers.forEach {
            it.init(vm)
        }
    }
}