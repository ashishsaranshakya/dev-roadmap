import java.util.HashMap;
import java.util.Map;

public class StaticMapLeak {

    // This static map is the leak
    private static final Map<Integer, byte[]> LEAK = new HashMap<>();

    public static void main(String[] args) throws Exception {
        int counter = 0;

        while (true) {
            // Allocate 1 MB per entry
            LEAK.put(counter, new byte[1024 * 1024]);
            counter++;

            if (counter % 50 == 0) {
                System.out.println("Entries in static map: " + counter);
            }

            Thread.sleep(50);
        }
    }
}
