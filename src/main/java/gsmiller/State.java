package gsmiller;

import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;

import java.util.Random;

@org.openjdk.jmh.annotations.State(Scope.Benchmark)
public class State {

  @Param({"64", "128", "256"})
  int size;

  float[] vectorA;
  float[] vectorB;

  float result;

  private static final Random r = new Random();

  @Setup(Level.Trial)
  public void setup() {
    vectorA = new float[size];
    vectorB = new float[size];
    for (int i = 0; i < size; i++) {
      vectorA[i] = r.nextFloat(1024f); // todo: ?
      vectorB[i] = r.nextFloat(1024f); // todo: ?
    }
  }

}
