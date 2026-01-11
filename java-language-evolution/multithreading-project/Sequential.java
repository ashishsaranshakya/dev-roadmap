import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

public class Sequential {

    // -------- Shared Result --------
    private final ConcurrentHashMap<String, FileStats> STATS =
            new ConcurrentHashMap<>();

//    public static void main(String[] args) {
//        Path root = Paths.get("D:\\");
//
//        try {
//            walkDirectory(root);
//            printResult(root);
//        } catch (RuntimeException e) {
//            System.err.println("FAILED: " + e.getMessage());
//            throw e;
//        }
//    }

    public ConcurrentHashMap<String, FileStats> main() {
        Path root = Paths.get("D:\\");

        try {
            walkDirectory(root);

//            StringBuilder builder = new StringBuilder();
//
//            builder.append("Root: " + root);
//            builder.append("\n");
//
//            STATS.entrySet().stream()
//                    .sorted((a, b) ->
//                            Long.compare(
//                                    b.getValue().count.sum(),
//                                    a.getValue().count.sum()
//                            )
//                    )
//                    .forEach(e -> {
//                        FileStats s = e.getValue();
//                        builder.append("\n");
//                        builder.append(
//                                e.getKey() +
//                                        " -> count=" + s.count.sum() +
//                                        ", size=" + humanReadable(s.totalSize.sum())
//                        );
//                    });
//            return builder.toString();
            return STATS;
        } catch (RuntimeException e) {
            System.err.println("FAILED: " + e.getMessage());
            throw e;
        }
    }

    // -------- Sequential Directory Walk --------
    private void walkDirectory(Path dir) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {

            for (Path entry : stream) {
                if (Files.isDirectory(entry)) {
                    walkDirectory(entry); // recursion, no parallelism
                } else if (Files.isRegularFile(entry)) {
                    countFile(entry);
                }
            }

        } catch (AccessDeniedException e) {
            // Skip inaccessible directory
        } catch (IOException e) {
            // Fail-fast for everything else
            throw new RuntimeException(e);
        }
    }

    // -------- File Processing --------
    private void countFile(Path file) {
        String name = file.getFileName().toString();
        String ext = extractExtension(name);

        try {
            long size = Files.size(file);

            STATS.computeIfAbsent(ext, k -> new FileStats());
            STATS.get(ext).count().increment();
            STATS.get(ext).totalSize().add(size);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String extractExtension(String name) {
        int idx = name.lastIndexOf('.');
        if (idx == -1 || idx == name.length() - 1) {
            return "(no extension)";
        }
        return name.substring(idx);
    }

    // -------- Output --------
    private void printResult(Path root) {
        System.out.println("Root: " + root);
        System.out.println();

        STATS.entrySet().stream()
                .sorted((a, b) ->
                        Long.compare(
                                b.getValue().count().sum(),
                                a.getValue().count().sum()
                        )
                )
                .forEach(e -> {
                    FileStats s = e.getValue();
                    System.out.println(
                            e.getKey() +
                                    " -> count=" + s.count().sum() +
                                    ", size=" + humanReadable(s.totalSize().sum())
                    );
                });
    }

    private String humanReadable(long bytes) {
        if (bytes < 1024) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        String pre = "KMGTPE".charAt(exp - 1) + "";
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }
}
