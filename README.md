To run benchmarks:

$ mvn clean package

$ java -jar target/benchmarks.jar

To see the produced assembly:

$ java -jar target/benchmarks.jar -prof perfasm


* Inspired largely by jmh benchmarking strategy in: https://github.com/jpountz/vectorized-prefix-sum