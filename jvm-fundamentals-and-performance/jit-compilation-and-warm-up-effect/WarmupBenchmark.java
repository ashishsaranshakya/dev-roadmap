public class WarmupBenchmark {

    static long work() {
        long sum = 0;
        // for (int i = 0; i < 50_000_000; i++) {
        for (int i = 0; i < 5_000_000; i++) {
            sum += i;
        }
        return sum;
    }

    public static void main(String[] args) {
        for (int run = 1; run <= 10; run++) {
            long start = System.nanoTime();
            long result = work();
            long timeMs = (System.nanoTime() - start) / 1_000_000;

            System.out.println("Run " + run + ": " + timeMs + " ms (sum=" + result + ")");
        }
    }
}
