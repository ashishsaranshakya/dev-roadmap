public class CpuHeavy {

    static long heavyComputation() {
        long sum = 0;
        for (int i = 0; i < 200_000_000; i++) {
            sum += (i % 7) * (i % 13);
        }
        return sum;
    }

    public static void main(String[] args) throws Exception {
        while (true) {
            heavyComputation();
            Thread.sleep(100);
        }
    }
}
