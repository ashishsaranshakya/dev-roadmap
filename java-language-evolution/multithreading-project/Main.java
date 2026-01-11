import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.StructuredTaskScope;

public class Main{
    Comparator<Map.Entry<String, FileStats>> FILE_STATS_COMPARATOR =
            (e1, e2) -> {
                int byCount = Long.compare(
                        e2.getValue().count().sum(),
                        e1.getValue().count().sum()
                );
                if (byCount != 0) {
                    return byCount;
                }

                return e1.getKey().compareTo(e2.getKey());
            };

    public static void main(String[] args) throws Exception {
        try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
            var res = new ArrayList<StructuredTaskScope.Subtask<Map<String, Object>>>();
            for (int i = 0; i < 10; i++) {
                res.add(scope.fork(() -> run()));
            }

            scope.join();
            scope.throwIfFailed();

            res.stream().forEach((entry) -> {
                Map<String, FileStats> seq = (ConcurrentHashMap<String, FileStats>)entry.get().get("sequential result");
                Map<String, FileStats> par = (ConcurrentHashMap<String, FileStats>)entry.get().get("parallel result");

                if(!equalStats(seq, par)){
                    System.out.println("sequential result = " + entry.get().get("sequential result"));
                    System.out.println();
                    System.out.println("parallel result = " + entry.get().get("parallel result"));
                    System.out.println();
                }
                System.out.println("sequential time = " + entry.get().get("sequential time"));
                System.out.println("parallel time = " + entry.get().get("parallel time"));
                System.out.println("---------------------------");
            });
        }
        catch (InterruptedException | ExecutionException e) {
            // Fail-fast for all other problems
            throw new RuntimeException(e);
        }
    }

    static boolean equalStats(Map<String, FileStats> a, Map<String, FileStats> b) {
        if (a.size() != b.size()) return false;

        for (Map.Entry<String, FileStats> e : a.entrySet()) {
            FileStats fa = e.getValue();
            FileStats fb = b.get(e.getKey());

            if ((fb == null) || (fa.count().sum() != fb.count().sum()) || (fa.totalSize().sum() != fb.totalSize().sum())) {
                System.out.println("Diff: " + fa);
                return false;
            }
        }
        System.out.println("Equalled");
        return true;
    }


    public static Map<String, Object> run() throws Exception{
        long sT = 0;
        long pT = 0;
        Date d = new Date();
        var seq = new Sequential();
        var s = seq.main();
        sT = (new Date()).getTime() -  d.getTime();

        d = new Date();
        var par = new Parallel();
        var p = par.main();
        pT = (new Date()).getTime() -  d.getTime();

        Map<String, Object> res = new HashMap<>();
        res.put("sequential time", sT);
        res.put("parallel time", pT);
        res.put("sequential result", s);
        res.put("parallel result", p);

        return res;
    }
}