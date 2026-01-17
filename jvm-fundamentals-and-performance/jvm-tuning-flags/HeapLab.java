import java.util.*;

public class HeapLab {

    static final int MB = 1024 * 1024;

    public static void main(String[] args) throws Exception {
        long t0 = System.currentTimeMillis();

        // Startup work (class loading + allocations)
        List<byte[]> warmup = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            warmup.add(new byte[MB]);
        }
        warmup.clear();

        long startupMs = System.currentTimeMillis() - t0;
        System.out.println("Startup(ms): " + startupMs);

        // Steady allocation pressure
        List<byte[]> sink = new ArrayList<>();
        long start = System.currentTimeMillis();
        while (System.currentTimeMillis() - start < 60_000) {
            for (int i = 0; i < 10; i++) {
                sink.add(new byte[MB]);
            }
            if (sink.size() > 50) sink.clear();
            Thread.sleep(50);
        }
    }
}

// java -Xms256m -Xmx256m \
//      -XX:+UseG1GC \
//      -Xlog:gc*:file=gc-small.log:time,uptime,level,tags \
//      HeapLab

// java -Xms2g -Xmx2g \
//      -XX:+UseG1GC \
//      -Xlog:gc*:file=gc-large.log:time,uptime,level,tags \
//      HeapLab
