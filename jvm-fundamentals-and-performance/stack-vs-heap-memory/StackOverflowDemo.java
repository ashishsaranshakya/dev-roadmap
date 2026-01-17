public class StackOverflowDemo {

	static long depth = 0;

	static void recurse() {
		depth++;
		recurse();
	}

	public static void main(String[] args) {
		try {
			recurse();
		} catch (StackOverflowError e) {
			System.out.println("Stack depth reached: " + depth);
			// throw e;
		}
	}
}

// java StackOverflowDemo
// java -Xss256k StackOverflowDemo 
// java -Xss2m   StackOverflowDemo
