package ma.ssvmandroid.native

import dev.xdark.ssvm.VirtualMachine

interface NativeInitializer {

    fun init(vm: VirtualMachine)
}