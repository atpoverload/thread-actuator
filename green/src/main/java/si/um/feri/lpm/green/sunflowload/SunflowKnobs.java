package si.um.feri.lpm.green.sunflowload;

import java.util.Map;

public final class SunflowKnobs {
  private final int threads;
  private final int resolution;
  private final int aaMin;
  private final int aaMax;
  private final Map<Integer, Integer> actuation;

  public SunflowKnobs(
      int threads, int resolution, int aaMin, int aaMax, Map<Integer, Integer> actuation) {
    this.threads = threads;
    this.resolution = resolution;
    this.aaMin = aaMin;
    this.aaMax = aaMax;
    this.actuation = actuation;
  }

  public int threads() {
    return this.threads;
  };

  public int resolution() {
    return this.resolution;
  };

  public int aaMin() {
    return this.aaMin;
  };

  public int aaMax() {
    return this.aaMax;
  };

  public Map<Integer, Integer> actuation() {
    return this.actuation;
  };
}
