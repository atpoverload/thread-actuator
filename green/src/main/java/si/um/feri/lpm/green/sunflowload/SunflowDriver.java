package si.um.feri.lpm.green.sunflowload;

import java.util.HashMap;
import java.util.Random;
import jdvfs.Dvfs;

public final class SunflowDriver {
  private static final Random rng = new Random();

  private static final HashMap<Integer, Integer> buildActuation() {
    String[][] governors = Dvfs.getAvailableGovernors();
    HashMap<Integer, Integer> actuation = new HashMap<>();
    for (int cpu = 0; cpu < Runtime.getRuntime().availableProcessors(); cpu++) {
      for (String governor : Dvfs.getAvailableGovernors(cpu)) {
        if (governor.equals("userspace") && rng.nextFloat() > 0.50) {
          int[] freqs = Dvfs.getAvailableFrequencies(cpu);
          actuation.put(cpu, freqs[rng.nextInt(freqs.length)]);
        }
      }
    }
    return actuation;
  }

  public static void main(String[] args) {
    HashMap<Integer, Integer> actuation = buildActuation();
    System.out.println("ACTUATION: " + actuation);
    new SunflowRunner(
            new SunflowKnobs(Runtime.getRuntime().availableProcessors(), 128, -1, 1, actuation))
        .run();
  }
}
