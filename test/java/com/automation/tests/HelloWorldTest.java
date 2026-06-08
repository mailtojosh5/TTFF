import org.testng.Assert;
import org.testng.annotations.Test;

public class HelloWorldTest {
    @Test
    public void testHelloWorld() {
        String hello = "Hello, World!";
        Assert.assertEquals(hello, "Hello, World!");
    }
}