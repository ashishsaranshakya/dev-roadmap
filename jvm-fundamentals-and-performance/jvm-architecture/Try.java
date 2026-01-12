public class Try {
	public static void main(String[] args) throws Exception{
		MyClassLoader loader =
                new MyClassLoader("C:/classes"); // directory containing Hello.class

        Class<?> clazz = loader.loadClass("Hello");

        Object obj = clazz.getDeclaredConstructor().newInstance();
        clazz.getMethod("sayHello").invoke(obj);

		
		System.out.println(String.class.getClassLoader());
        System.out.println(java.util.ArrayList.class.getClassLoader());
        System.out.println(Try.class.getClassLoader());
	}
}