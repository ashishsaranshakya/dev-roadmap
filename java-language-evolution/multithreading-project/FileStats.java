import java.util.concurrent.atomic.LongAdder;

//public class FileStats {
//    final LongAdder count = new LongAdder();
//    final LongAdder totalSize = new LongAdder();
//}

public record FileStats(LongAdder count, LongAdder totalSize) {
    public FileStats() {
        this(new LongAdder(), new LongAdder());
    }
}