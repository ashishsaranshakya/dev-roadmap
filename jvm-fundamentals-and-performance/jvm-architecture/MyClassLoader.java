import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class MyClassLoader extends ClassLoader {

    private final String classDir;

    public MyClassLoader(String classDir) {
        this.classDir = classDir;
    }

    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        try {
            String fileName = className.replace('.', '/') + ".class";
            Path classPath = Path.of(classDir, fileName);

            byte[] classBytes = Files.readAllBytes(classPath);

            return defineClass(className, classBytes, 0, classBytes.length);
        } catch (IOException e) {
            throw new ClassNotFoundException(className, e);
        }
    }
}
