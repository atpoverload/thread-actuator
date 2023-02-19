# `jdvfs`
a small library that provides access to linux's [`DVFS`](https://www.kernel.org/doc/html/v4.14/admin-guide/pm/cpufreq.html) system through Java.

# setup
`DVFS` requires the `intel_pstate` to be disabled to use scaling governors. you will need to update `etc/default/grub` (or where ever your grub file lives) by adding `intel_pstate=disable` to `GRUB_CMDLINE_LINUX_DEFAULT` and restarting your system. here's an example of the change:

```
# from old "etc/default/grub"
GRUB_CMDLINE_LINUX_DEFAULT="console=tty0 console=ttyS1,115200n8"

# from new "etc/default/grub"
GRUB_CMDLINE_LINUX_DEFAULT="console=tty0 console=tty1 console=ttyS1,115200n8 intel_pstate=disable"
```

this can be reverted by changing the grub file back to its original contents and restarting.

# building

you can run `mvn clean install && sudo java -cp target/jdvfs-0.1.0.jar jdvfs.SmokeTest` to confirm that `DVFS` is available. if it does not work, we have provided some debugging tips that may help direct you.

# notes

<!-- TODO(timur): we can build some light tooling/security to help with some of these -->
this API is not secure currently. any user may change the cpus at anytime, so there is both a thread-safety AND a process-safety concern when using this on a multi-user system. since the API modifies the system, `DVFS` will not be reset unless you do it manually with `Dvfs.reset()`.
