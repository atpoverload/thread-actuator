package clerk.experiments;

import static java.util.stream.Collectors.joining;

import clerk.Dvfs;
import java.util.Arrays;
import java.util.stream.IntStream;
import org.sunflow.PluginRegistry;
import org.sunflow.SunflowAPI;
import org.sunflow.core.Display;
import org.sunflow.image.Color;

public class ActuatedSunflow {
  static class DummyDisplay implements Display {

    public void imageBegin(int w, int h, int bucketSize) {}

    public void imagePrepare(int x, int y, int w, int h, int id) {}

    public void imageUpdate(int x, int y, int w, int h, Color[] data, float[] alpha) {}

    public void imageFill(int x, int y, int w, int h, Color c, float alpha) {}

    public void imageEnd() {}
  }

  public static void main(String[] args) {
    // need to reset dvfs before and after because it changes the system
    Dvfs.reset();

    // get system params
    int cpus = Runtime.getRuntime().availableProcessors();
    int[] freqs = Dvfs.getAvailableFrequencies();

    // encoding is an array of frequency values for the system
    //  - 0 means do not use the core for actuation
    //  - any other values are in KHz
    // for a system with 4 cpu cores, [0 10000000 0 20000000 ] would represent the following:
    //  - no actuation on the first and third cores
    //  - set the second core to 1GHz
    //  - set the fourth core to 2GHz

    // 50% chance to set a cpu at a frequency chosen at random
    // choose configuration
    int[] parameters =
        IntStream.range(0, cpus)
            .map(c -> Math.random() > 0.5 ? freqs[((int) Math.random() * freqs.length)] : 0)
            .toArray();
    int threadCount = (int) Arrays.stream(parameters).filter(i -> i > 0).count();

    System.setProperty(
      "clerk.actuation.frequencies",
      Arrays.stream(parameters).mapToObj(Integer::toString).collect(joining(",")));

    // changes the bucket rendered to the actuated one
    PluginRegistry.imageSamplerPlugins.registerPlugin("bucket", ActuatedBucketRenderer.class);
    CornellBox scene = new CornellBox(threadCount);
    scene.build();
    scene.render(SunflowAPI.DEFAULT_OPTIONS, new DummyDisplay());

    // need to reset dvfs before and after because it changes the system
    Dvfs.reset();
  }
}
