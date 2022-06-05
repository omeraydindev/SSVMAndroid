# SSVMAndroid
[SSVM](https://github.com/xxDark/SSVM) but on Android basically. Uses an `rt.jar` from OpenJDK8 to load boot classes. (_8u262-b10-linux-x64_ to be exact)

## Limitations
- JIT doesn't work since SSVM internally relies on `sun.misc.Unsafe` and the JIT part particularly relies on a method called `staticFieldOffset`, which sadly doesn't exist on Android's partial implementation of Unsafe.

## Screenshots
<img src="https://user-images.githubusercontent.com/45513948/172046957-9b976b19-0bbf-4c27-8720-525856d14117.png" width="350" />

## Thanks
Thanks to [xxDark](https://github.com/xxDark) for helping me set this up!
