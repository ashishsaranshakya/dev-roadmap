public class CNFDemo {

    public static void main(String[] args) {
        try {
            Class.forName("com.fake.DoesNotExist");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
