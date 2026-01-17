import java.util.*;

public class AllocationPressure {

	static final int MB = 1024 * 1024;

	public static void main(String[] args) throws Exception {
		List<byte[]> sink = new ArrayList<>();
		long start = System.currentTimeMillis();

		while (System.currentTimeMillis() - start < 60_000) { // 60 seconds
			// Allocate ~10 MB per iteration
			for (int i = 0; i < 10; i++) {
				sink.add(new byte[MB]);
			}

			// Drop references periodically (simulate request end)
			if (sink.size() > 50) {
				sink.clear();
			}

			Thread.sleep(50); // pacing
		}
	}
}

// java -Xms512m -Xmx512m \
//      -XX:+UseSerialGC \
//      -Xlog:gc*:file=gc-serial.log:time,uptime,level,tags \
//      AllocationPressure

// java -Xms512m -Xmx512m \
//      -XX:+UseG1GC \
//      -XX:MaxGCPauseMillis=200 \
//      -Xlog:gc*:file=gc-g1.log:time,uptime,level,tags \
//      AllocationPressure

// java -Xms512m -Xmx512m \
//      -XX:+UseZGC \
//      -Xlog:gc*:file=gc-zgc.log:time,uptime,level,tags \
//      AllocationPressure
