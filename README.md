# thread-actuator
a work repository for a thread->cpu actutation system for linux

# setup
`DVFS` requires the `intel_pstate` to be disabled so that scaling governors can be used. you will need to change the contents of `etc/default/grub` on the system you are going to try to use DVFS on by adding `intel_pstate-disable` to `GRUB_CMDLINE_LINUX_DEFAULT`:

```
# old grub
GRUB_CMDLINE_LINUX_DEFAULT="console=tty0 console=ttyS1,115200n8"
# new grub
GRUB_CMDLINE_LINUX_DEFAULT="console=tty0 console=tty1 console=ttyS1,115200n8 intel_pstate=disable"
```

they you can restart your system and you should be able to set the DVFS governors/frequencies.

# running

<!-- TODO(timur): do we actually need sudo? -->
you can run `sudo smoke_test.sh` to confirm that DVFS can be changed. if that works, you should be good to use it with other deps.

# notes

<!-- TODO(timur): we can build some light tooling/security to help with some of these -->
this API is not secure currently. any user may change the cpus at anytime, so there is both a thread-safety AND a process-safety concern when using this on a multi-user system. since the API modifies the system, DVFS will not be reset unless you do it manually with `Dvfs.reset()`.
