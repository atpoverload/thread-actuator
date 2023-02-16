package clerk;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.stream.Stream;

/** Simple wrapper around dvfs access. */
public final class Dvfs {
  private static final String FREQ_PATH = "/sys/devices/system/cpu/cpu%d/cpufreq";
  private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();
  // the scalable frequencies that can be used on the cpus in KHz
  private static final int[] AVAILABLE_FREQUENCIES = readAvailableFrequencies();
  // TODO: the governors are not always the same so we may need to do some version control to
  // guarantee that the methods that change frequencies will not break
  private static final String[] AVAILABLE_GOVERNORS = readAvailableGovernors();
  private static final String DEFAULT_GOVERNOR = "ondemand";
  private static final String SCALABLE_GOVERNOR = "userspace";

  public static int[] getAvailableFrequencies() {
    return Arrays.copyOf(AVAILABLE_FREQUENCIES, AVAILABLE_FREQUENCIES.length);
  }

  public static String[] getAvailableGovernors() {
    return Arrays.copyOf(AVAILABLE_GOVERNORS, AVAILABLE_GOVERNORS.length);
  }

  // TODO: these don't report an understandable value or log an error
  /** Return current frequency of a cpu. */
  public static int getScalingFrequency(int cpu) {
    try (BufferedReader reader =
        new BufferedReader(new FileReader(getFrequencyComponent(cpu, "scaling_cur_freq")))) {
      return Integer.parseInt(reader.readLine());
    } catch (Exception e) {
      e.printStackTrace();
      return 0;
    }
  }

  // TODO: these don't report an understandable value or log an error
  /** Gets the currently set frequency of a cpu. */
  public static int getFrequency(int cpu) {
    try (BufferedReader reader =
        new BufferedReader(new FileReader(getFrequencyComponent(cpu, "cpuinfo_cur_freq")))) {
      return Integer.parseInt(reader.readLine());
    } catch (Exception e) {
      e.printStackTrace();
      return 0;
    }
  }

  /** Scales the frequency of a cpu. */
  public static void setFrequency(int cpu, int frequency) {
    setGovernor(cpu, SCALABLE_GOVERNOR);
    try (BufferedWriter reader =
        new BufferedWriter(new FileWriter(getFrequencyComponent(cpu, "scaling_setspeed")))) {
      reader.write(String.format("%d", frequency));
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /** Gets the current governor of a cpu. */
  public static String getGovernor(int cpu) {
    try (BufferedReader reader =
        new BufferedReader(new FileReader(getFrequencyComponent(cpu, "scaling_governor")))) {
      return reader.readLine();
    } catch (Exception e) {
      e.printStackTrace();
      return "";
    }
  }

  /** Sets the current governor of a cpu. */
  public static void setGovernor(int cpu, String governor) {
    try (BufferedWriter reader =
        new BufferedWriter(new FileWriter(getFrequencyComponent(cpu, "scaling_governor")))) {
      reader.write(governor);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /** Sets the governor of a cpu to ondemand, which is typically the default. */
  public static void reset(int cpu) {
    setGovernor(cpu, DEFAULT_GOVERNOR);
  }

  /** Sets the governor of all cpus to ondemand, which is typically the default. */
  public static void reset() {
    for (int cpu = 0; cpu < CPU_COUNT; cpu++) {
      reset(cpu);
    }
  }

  private static String getFrequencyComponent(int cpu, String component) {
    return String.join("/", String.format(FREQ_PATH, cpu), component);
  }

  private static int[] readAvailableFrequencies() {
    try (BufferedReader reader =
        new BufferedReader(
            new FileReader(getFrequencyComponent(0, "scaling_available_frequencies")))) {
      return Stream.of(reader.readLine().split(" ")).mapToInt(Integer::parseInt).sorted().toArray();
    } catch (Exception e) {
      e.printStackTrace();
      return new int[0];
    }
  }

  private static String[] readAvailableGovernors() {
    try (BufferedReader reader =
        new BufferedReader(
            new FileReader(getFrequencyComponent(0, "scaling_available_governors")))) {
      return reader.readLine().split(" ");
    } catch (Exception e) {
      e.printStackTrace();
      return new String[0];
    }
  }

  private Dvfs() {}

  /** a class wrapping the static methods for specific cpus. */
  public static final class Cpu {
    public final int cpu;

    public Cpu(int cpu) {
      this.cpu = cpu;
    }

    /** Gets the current frequency of the cpu. */
    public int getScalingFrequency() {
      return Dvfs.getScalingFrequency(cpu);
    }

    /** Gets the currently set frequency of the cpu. */
    public int getFrequency() {
      return Dvfs.getFrequency(cpu);
    }

    /** Scales the frequency of the cpu. */
    public void setFrequency(int frequency) {
      Dvfs.setFrequency(cpu, frequency);
    }

    /** Gets the current governor of the cpu. */
    public String getGovernor() {
      return Dvfs.getGovernor(cpu);
    }

    /** Sets the current governor of the cpu. */
    public void setGovernor(String governor) {
      Dvfs.setGovernor(cpu, governor);
    }

    /** Sets the governor of the cpu to ondemand, which is typically the default. */
    public void reset() {
      Dvfs.reset(cpu);
    }
  }

  public static void main(String[] args) throws Exception {
    System.out.println("DVFS initialized");

    System.out.println(String.format("CPU count: %d", CPU_COUNT));
    System.out.println(
        String.format("Available frequencies: %s", Arrays.toString(AVAILABLE_FREQUENCIES)));
    for (int i = 0; i < CPU_COUNT; i++) {
      Dvfs.Cpu cpu = new Dvfs.Cpu(i);
      System.out.println(
          String.format(
              "CPU %d - governor: %s, frequency: %d",
              cpu.cpu, cpu.getGovernor(), cpu.getFrequency()));
    }
  }
}
