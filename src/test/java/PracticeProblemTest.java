//import static org.junit.jupiter.api.Assertions.*;
//import org.junit.jupiter.api.*;
//import java.io.*;
//
//public class PracticeProblemTest {
//
//    @Test
//    public void testOutput()
//    {
//        PrintStream originalOut = System.out;
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(bos));
//
//        // action
//        PracticeProblem.q1();
//
//        // assertion
//        assertEquals("There once was a man from St. Ives.\n", bos.toString());
//
//        // undo the binding in System
//        System.setOut(originalOut);
//    }
//
//    @Test
//    public void testInputandOutput()
//    {
//        String data = "Users Input";
//        System.setIn(new ByteArrayInputStream(data.getBytes()));
//
//        PrintStream originalOut = System.out;
//        ByteArrayOutputStream bos = new ByteArrayOutputStream();
//        System.setOut(new PrintStream(bos));
//
//        // action
//        PracticeProblem.q1();
//
//        // assertion
//        assertEquals("There once was a man from St. Ives.\n", bos.toString());
//
//        // undo the binding in System
//        System.setOut(originalOut);
//    }
//
//    @Test
//    public void testQ3()
//    {
//
//    }
//}
