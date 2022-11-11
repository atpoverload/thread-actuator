package clerk;

import net.openhft.affinity.Affinity;

public final class Driver {
  private Driver() {}

  public static void main(String[] args) {
    int[] freqs = Dvfs.getAvailableFrequencies();
    Dvfs.setFrequency(0, freqs[0]);
    Affinity.setAffinity(0);
    Dvfs.reset(0);
  }
}
