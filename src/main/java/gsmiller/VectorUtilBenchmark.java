package gsmiller;

import java.util.concurrent.TimeUnit;

import jdk.incubator.vector.FloatVector;
import jdk.incubator.vector.VectorOperators;
import jdk.incubator.vector.VectorSpecies;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;

@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 1, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1, jvmArgsPrepend = {"--add-modules=jdk.incubator.vector"})
@org.openjdk.jmh.annotations.State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
public class VectorUtilBenchmark {

  @Setup(Level.Trial)
  public void setup() {
//    verifyCorrect();
  }

  private static final VectorSpecies<Float> FLOAT_VECTOR_SP = FloatVector.SPECIES_PREFERRED;

  @Benchmark
  public void dotProductVectorized(State state, Blackhole bh) {
    float[] vecA = state.vectorA;
    float[] vecB = state.vectorB;

    FloatVector a, b;
    FloatVector res = FloatVector.zero(FLOAT_VECTOR_SP);

    int bound = FLOAT_VECTOR_SP.loopBound(vecA.length);
    int l = FLOAT_VECTOR_SP.length();
    int i = 0;
    for (; i < bound; i += l) {
      a = FloatVector.fromArray(FLOAT_VECTOR_SP, vecA, i);
      b = FloatVector.fromArray(FLOAT_VECTOR_SP, vecB, i);
      res = a.mul(b).add(res);
    }

    float r = res.reduceLanes(VectorOperators.ADD);
    for (; i < vecA.length; i++) {
      r += vecA[i] * vecB[i];
    }

    state.result = r;
    bh.consume(r);
  }

  @Benchmark
  public void dotProductScalar(State state, Blackhole bh) {
    float[] vecA = state.vectorA;
    float[] vecB = state.vectorB;

    float r = 0;
    for (int i = 0; i < vecA.length; i++) {
      r += vecA[i] * vecB[i];
    }

    state.result = r;
    bh.consume(r);
  }

  public void verifyCorrect() {
    var bh = new Blackhole("Today's password is swordfish. I understand instantiating Blackholes directly is dangerous.");

    for (int size : new int[] { 128, 130, 1024}) {
      var state = new State();
      state.size = size;
      state.setup();
      dotProductScalar(state, bh);
      float expectedResult = state.result;
      dotProductVectorized(state, bh);
      if (expectedResult != state.result && Math.abs(expectedResult - state.result) > 0.00001) {
        throw new AssertionError("Incorrect result. Expected: " + expectedResult + ", got: " + state.result + " " + Math.abs(expectedResult - state.result));
      }
    }
  }
}
