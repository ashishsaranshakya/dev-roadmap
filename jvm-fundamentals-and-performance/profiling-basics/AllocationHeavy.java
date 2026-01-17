import java.util.*;

public class AllocationHeavy {

    static void allocate() {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 50_000; i++) {
            list.add("value-" + i);
        }
    }

    public static void main(String[] args) throws Exception {
        while (true) {
            allocate();
            Thread.sleep(50);
        }
    }
}
