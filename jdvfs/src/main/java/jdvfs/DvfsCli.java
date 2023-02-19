package jdvfs;

import java.util.Arrays;
import java.util.logging.Logger;

public final class DvfsCli {
  private static final Logger logger = LoggerUtil.getLogger();

  private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
  private static final int[][] AVAILABLE_FREQUENCIES = Dvfs.getAvailableFrequencies();
  private static final String[][] AVAILABLE_GOVERNORS = Dvfs.getAvailableGovernors();

  private static final int PADDING = (int) Math.floor(Math.log10((double) CPU_COUNT)) + 1;

  private static double khzToGhz(int freq) {
    return (double) Math.round(freq / 1000) / 1000;
  }

  private static String cpuSummary(Dvfs.Cpu cpu) {
    return String.format(
        String.join("\n", "CPU %" + PADDING + "d", "governors: %s", "frequencies: %s"),
        cpu.cpu,
        Arrays.toString(Dvfs.getAvailableGovernors(cpu.cpu)),
        Arrays.toString(cpu.getAvailableFrequencies()));
  }

  private static String cpuSnapshot(Dvfs.Cpu cpu) {
    return String.format(
        "CPU %" + PADDING + "d - governor: %s, frequency - real: %.3f GHz, observed: %.3f GHz",
        cpu.cpu,
        cpu.getGovernor(),
        khzToGhz(cpu.getFrequency()),
        khzToGhz(cpu.getObservedFrequency()));
  }

  private static void printSummary() {
    logger.info("DVFS summary");

    logger.info(String.format("CPU count: %d", CPU_COUNT));
    for (int cpu = 0; cpu < CPU_COUNT; cpu++) {
      logger.info(cpuSummary(new Dvfs.Cpu(cpu)));
    }
  }

  private static void printSnapshot() {
    for (int cpu = 0; cpu < CPU_COUNT; cpu++) {
      logger.info(cpuSnapshot(new Dvfs.Cpu(cpu)));
    }
  }

  public static void main(String[] args) throws Exception {
    if ((args.length == 0) || Arrays.stream(args).anyMatch(s -> s.equals("--summary"))) {
      printSummary();
    }

    if ((args.length == 0) || Arrays.stream(args).anyMatch(s -> s.equals("--snapshot"))) {
      printSnapshot();
    }

    if (Arrays.stream(args).anyMatch(s -> s.equals("--reset"))) {
      Dvfs.reset();
      logger.info("all cpus reset to the default");
    }
  }
}
