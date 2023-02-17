package clerk;

public class SmokeTest {
  private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
  private static final int[] AVAILABLE_FREQUENCIES = Dvfs.getAvailableFrequencies();

  public static void main(String[] args) throws Exception {
    Dvfs.reset();

    for (int freq : AVAILABLE_FREQUENCIES) {
      for (int n = 0; n < CPU_COUNT; n++) {
        Dvfs.Cpu cpu = new Dvfs.Cpu(n);
        cpu.setFrequency(freq);
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 2000) {
          System.out.println(
              String.format(
                  "CPU %d - governor: %s, scaling frequency: %d, frequency: %d",
                  cpu.cpu, cpu.getGovernor(), cpu.getScalingFrequency(), cpu.getFrequency()));
          Thread.sleep(100);
        }
        cpu.reset();
      }
    }
    Dvfs.reset();
  }
}
