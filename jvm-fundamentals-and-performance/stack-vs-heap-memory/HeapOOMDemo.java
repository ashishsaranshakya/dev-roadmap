import java.util.*;

public class HeapOOMDemo {

    public static void main(String[] args) {
        List<byte[]> memory = new ArrayList<>();

        while (true) {
            memory.add(new byte[10_000_000]); // ~10MB
            System.out.println("Allocated blocks: " + memory.size());
        }
    }
}

// java HeapOOMDemo
// java -Xmx128m HeapOOMDemo
