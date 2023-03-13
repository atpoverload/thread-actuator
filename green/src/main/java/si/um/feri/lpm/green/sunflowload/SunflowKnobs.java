package si.um.feri.lpm.green.sunflowload;

public final class SunflowKnobs {
  private final int threads;
  private final int resolution;
  private final int aaMin;
  private final int aaMax;

  public SunflowKnobs(
      int threads, int resolution, int aaMin, int aaMax) {
    this.threads = threads;
    this.resolution = resolution;
    this.aaMin = aaMin;
    this.aaMax = aaMax;
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
}
