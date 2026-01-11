import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.atomic.LongAdder;

public class Parallel {
    // -------- Scoped Values --------
    static ScopedValue<Path> ROOT_PATH = ScopedValue.newInstance();
    static ScopedValue<Path> CURRENT_DIR = ScopedValue.newInstance();


    // -------- Shared Result --------
    private final ConcurrentHashMap<String, FileStats> STATS =
            new ConcurrentHashMap<>();

//    public static void main(String[] args) throws Exception {
//        Path root = Paths.get("D:\\");
//
//        ScopedValue.where(ROOT_PATH, root)
//                .run(() -> {
//                    try {
//                        index(root);
//                        printResult();
//                    } catch (Exception e) {
//                        System.err.println(e.getMessage());
//                        throw new RuntimeException(e);
//                    }
//                });
//    }

    public ConcurrentHashMap<String, FileStats> main() throws Exception {
        Path root = Paths.get("D:\\");

        ScopedValue.where(ROOT_PATH, root)
                .run(() -> {
                    try {
                        index(root);

//                        builder.append("Root: " + root);
//                        builder.append("\n");
//
//                        STATS.entrySet().stream()
//                                .sorted((a, b) ->
//                                        Long.compare(
//                                                b.getValue().count.sum(),
//                                                a.getValue().count.sum()
//                                        )
//                                )
//                                .forEach(e -> {
//                                    FileStats s = e.getValue();
//                                    builder.append("\n");
//                                    builder.append(
//                                            e.getKey() +
//                                                    " -> count=" + s.count.sum() +
//                                                    ", size=" + humanReadable(s.totalSize.sum())
//                                    );
//                                });
                    } catch (Exception e) {
                        System.err.println(e.getMessage());
                        throw new RuntimeException(e);
                    }
                });

        return STATS;
    }

    // -------- Entry Point --------
    private void index(Path root) throws Exception {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            scope.fork(() -> walkDirectory(root));
            scope.join();
            scope.throwIfFailed();
        }
    }

    // -------- Directory Walker --------
    private Void walkDirectory(Path dir) {

        ScopedValue.where(CURRENT_DIR, dir).run(() -> {
            try (var scope = new StructuredTaskScope.ShutdownOnFailure();
                 DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {

                for (Path entry : stream) {
                    if (Files.isDirectory(entry)) {
                        scope.fork(() -> walkDirectory(entry));
                    } else if (Files.isRegularFile(entry)) {
                        countFile(entry);
                    }
                }

                scope.join();
                scope.throwIfFailed();
            } catch (AccessDeniedException ex) {
                // Skip inaccessible directory
            } catch (IOException | InterruptedException | ExecutionException e) {
                // Fail-fast for all other problems
                throw new RuntimeException(e);
            }
        });

        return null;
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
            // This is NOT AccessDeniedException-specific,
            // so let it fail-fast
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
    private void printResult() {
        System.out.println("Root: " + ROOT_PATH.get());
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
