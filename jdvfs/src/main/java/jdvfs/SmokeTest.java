package jdvfs;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.logging.Logger;

public final class SmokeTest {
  private static final Logger logger = LoggerUtil.getLogger();

  private static boolean isGrubUpdated() {
    try (BufferedReader reader = new BufferedReader(new FileReader("/etc/default/grub"))) {
      return reader
          .lines()
          .anyMatch(
              e -> e.contains("GRUB_CMDLINE_LINUX_DEFAULT=") && e.contains("intel_pstate=disable"));
    } catch (Exception e) {
      return false;
    }
  }

  private SmokeTest() {}

  public static void main(String[] args) throws Exception {
    ArrayList<Integer> availableCpus = new ArrayList<>();
    for (int cpu = 0; cpu < Runtime.getRuntime().availableProcessors(); cpu++) {
      String filePath = String.format(Dvfs.FREQ_PATH, cpu);
      if (new File(filePath).exists()) {
        availableCpus.add(cpu);
      }
    }

    if (availableCpus.isEmpty()) {
      logger.info("'cpufreq' not found for this system. make sure you have it installed or are running as 'sudo'.");
      return;
    }

    logger.info(String.format("'cpufreq' available for cpus: %s", availableCpus));

    ArrayList<Integer> availableDvfs = new ArrayList<>();
    for (int cpu : availableCpus) {
      if (Dvfs.getFrequency(cpu) > 0) {
        availableDvfs.add(cpu);
      }
    }

    if (availableDvfs.isEmpty()) {
      if (!isGrubUpdated()) {
        logger.info(
            "'dvfs' not found for this system. make sure you modified your grub file"
                + " ('/etc/default/grub') by adding 'intel_pstate=disable'"
                + " to the 'GRUB_CMDLINE_LINUX_DEFAULT' entry.");
        return;
      } else {
        logger.info(
            "grub is updated but still unable to read scaling frequency."
                + " do you need to run as 'sudo'?");
      }
    }

    logger.info(String.format("'dvfs' available for cpus: %s", availableDvfs));
  }
}
