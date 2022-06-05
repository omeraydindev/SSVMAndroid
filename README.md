# SSVMAndroid
[SSVM](https://github.com/xxDark/SSVM) but on Android basically. Uses an `rt.jar` from OpenJDK8 to load boot classes (_8u262-b10-linux-x64_ to be exact). 

JIT works by running D8 to convert the Java bytecode to DEX, then the result is loaded using a `InMemoryDexClassLoader`.

## Screenshots
<img src="https://user-images.githubusercontent.com/45513948/172046957-9b976b19-0bbf-4c27-8720-525856d14117.png" width="350" />

## Thanks
Thanks to [xxDark](https://github.com/xxDark) for helping me set this up!
