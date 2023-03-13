package si.um.feri.lpm.green.sunflowload;

import static java.util.stream.Collectors.joining;

import com.google.protobuf.util.Durations;
import com.google.protobuf.util.Timestamps;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;
import jdvfs.Dvfs;
import jrapl.Powercap;
import jrapl.RaplDifference;

public final class SunflowDriver {
  private static final Random rng = new Random();
  
  // binds everyone to a single, low-frequency cpu
  private static final HashMap<Integer, Integer> minimumActuation() {
    HashMap<Integer, Integer> actuation = new HashMap<>();
    int cpu = rng.nextInt(Runtime.getRuntime().availableProcessors());
    for (String governor : Dvfs.getAvailableGovernors(cpu)) {
      if (governor.equals("userspace")) {
        int[] freqs = Dvfs.getAvailableFrequencies(cpu);
        actuation.put(cpu, freqs[0]);
        continue;
      }
    }
    return actuation;
  }

  // binds everyone to all the cpus which are maximized
  private static final HashMap<Integer, Integer> maximumActuation() {
    HashMap<Integer, Integer> actuation = new HashMap<>();
    for (int cpu = 0; cpu < Runtime.getRuntime().availableProcessors(); cpu++) {
      for (String governor : Dvfs.getAvailableGovernors(cpu)) {
        if (governor.equals("userspace")) {
          int[] freqs = Dvfs.getAvailableFrequencies(cpu);
          actuation.put(cpu, freqs[freqs.length - 1]);
          continue;
        }
      }
    }
    return actuation;
  }

  // set each cpu randomly
  private static final HashMap<Integer, Integer> randomActuation() {
    HashMap<Integer, Integer> actuation = new HashMap<>();
    for (int cpu = 0; cpu < Runtime.getRuntime().availableProcessors(); cpu++) {
      for (String governor : Dvfs.getAvailableGovernors(cpu)) {
        if (governor.equals("userspace") && rng.nextFloat() > 0.50) {
          int[] freqs = Dvfs.getAvailableFrequencies(cpu);
          actuation.put(cpu, freqs[rng.nextInt(freqs.length)]);
          continue;
        }
      }
    }
    return actuation;
  }

  private static final void printActuation(HashMap<Integer, Integer> actuation) throws Exception {
    for (int cpu : actuation.keySet()) {
      System.out.println(String.format("actuation,%d,%d", cpu, actuation.get(cpu)));
    }
  }

  private static BitSet setupActuation(Map<Integer, Integer> actuation) {
    BitSet affinity = new BitSet(Runtime.getRuntime().availableProcessors());
    for (int cpu : actuation.keySet()) {
      Dvfs.setFrequency(cpu, actuation.get(cpu));
      affinity.set(cpu);
    }
    return affinity;
  }

  public static void main(String[] args) throws Exception {
    Dvfs.reset();
    HashMap<Integer, Integer> actuation = new HashMap<>();
    if (args.length > 0) {
      switch (args[0]) {
        case "min":
          actuation = minimumActuation();
          break;
        case "max":
          actuation = maximumActuation();
          break;
        case "random":
          actuation = randomActuation();
          break;
        default:
          break;
      }
    }
    printActuation(actuation);
    ArrayList<RaplDifference> diffs = new ArrayList<>();
    for (int i = 0; i < 50; i++) {
      var first = Powercap.sample();
      Dvfs.reset();
      setupActuation(actuation);
      new SunflowRunner(
              new SunflowKnobs(Runtime.getRuntime().availableProcessors(), 128, -1, 1))
          .run();
      Dvfs.reset();
      diffs.add(Powercap.difference(first, Powercap.sample()));
    }
    System.out.println(
        diffs
            .stream()
            .map(
                d ->
                    String.format(
                        "energy,%d,%.3f",
                        Durations.toMillis(Timestamps.between(d.getStart(), d.getEnd())),
                        d.getReadingList()
                            .stream()
                            .mapToDouble(r -> r.getPackage() + r.getDram())
                            .sum()))
            .collect(joining("\n")));
  }
}
