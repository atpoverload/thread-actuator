package si.um.feri.lpm.green.sunflowload;

import org.sunflow.SunflowAPI;
import org.sunflow.core.Display;
import org.sunflow.image.Color;
import si.um.feri.lpm.green.sunflowload.scenes.CornellBox;

public class SunflowRunner implements Runnable {

  SunflowKnobs knobs;

  public SunflowRunner(SunflowKnobs knobs) {
    this.knobs = knobs;
  }

  static class DummyDisplay implements Display {

    public void imageBegin(int w, int h, int bucketSize) {}

    public void imagePrepare(int x, int y, int w, int h, int id) {}

    public void imageUpdate(int x, int y, int w, int h, Color[] data, float[] alpha) {}

    public void imageFill(int x, int y, int w, int h, Color c, float alpha) {}

    public void imageEnd() {}
  }

  @Override
  public void run() {
    var scene = new CornellBox(knobs);
    scene.build();
    scene.render(SunflowAPI.DEFAULT_OPTIONS, new DummyDisplay());
  }
}
