/************************************************
 *
 * Author: Lauren Adams
 * Assignment: Program 0
 * Class: Data Communications 4321
 *
 ************************************************/



package megex.serialization.test;


import megex.serialization.Framer;
import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import java.io.*;
import java.util.Arrays;


/**
 * tests for the Framer.java class
 */
public class FramerTest {

    /**
     * constructor for framer class
     */
    public FramerTest() {
    }

    /**
     * Tests frame cannot be initialized with null values
     * @throws NullPointerException
     *  if null output stream is provided
     */
    @DisplayName("Null constructor test")
    @Test
    public void testConstructor(){
        //initializes null Framer and throws null
        assertThrows(NullPointerException.class, () -> {
            Framer f = new Framer(null);
        });
    }




    /**
     * Tests that valid Illegal Argument is thrown when header is invalid
     *
     * @throws IllegalArgumentException if message has invalid header
     */
    @Test
    @DisplayName("Invalid header Test")
    public void testInvalidHeader() {
        //Initialize output, framer, and test array
        OutputStream out = new ByteArrayOutputStream();
        Framer f = new Framer(out);
        byte[] msg = new byte[2];
        //call framer to show the correct error is thrown
        assertThrows(IllegalArgumentException.class, () -> {
            f.putFrame(msg);
        });
    }

    /**
     * Tests that valid IOexception is thrown when header is invalid
     */
    @Test
    @DisplayName("Null message Test")
    public void testNullMessage() {
        //Initialize output, framer, and test array
        OutputStream out = new ByteArrayOutputStream();
        Framer f = new Framer(out);
        //call framer to show the correct error is thrown
        assertThrows(NullPointerException.class, () -> {
            f.putFrame(null);
        });
    }



    /**
     * Tests that proper length is outputted for larger frame
     * @throws IOException if data is invalid
     */
    @DisplayName("Test frame with bigger values")
    @Test
    public void testBigFrame() throws IOException {
        //initialize output and framer
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Framer f = new Framer(out);
        //create byte array and set it to distinct values
        byte[] msg = new byte[200];
        Arrays.fill(msg, 0, 6, (byte) 10);
        Arrays.fill(msg, 6, 200, (byte) 11);


        // frame the array and compare the data
        f.putFrame(msg);
        byte[] resultBytes = out.toByteArray();
        assertAll(() -> assertArrayEquals(msg, Arrays.copyOfRange(resultBytes, 3, 203)), () -> assertEquals(resultBytes[0], 0), () -> assertEquals(resultBytes[1], 0), () -> assertEquals(resultBytes[2], -62), () -> assertEquals(resultBytes.length, msg.length + 3));

    }


    /**
     * Tests that proper length is outputted for biggest frame
     *
     * @throws IOException if data is invalid
     */
    @DisplayName("Test frame with really big values")
    @Test
    public void testBigestFrame() throws IOException {
        //initialize output and framer
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Framer f = new Framer(out);
        //create byte array and set it to distinct values
        byte[] msg = new byte[14000];
        Arrays.fill(msg, 0, 6, (byte) 10);
        Arrays.fill(msg, 6, 14000, (byte) 11);


        // frame the array and compare the data
        f.putFrame(msg);
        byte[] resultBytes = out.toByteArray();
        assertAll(() -> assertArrayEquals(msg, Arrays.copyOfRange(resultBytes, 3, 14003)));

    }

    /**
     * Tests that proper length is outputted for frame with only header
     * @throws IOException if data is invalid
     */
    @Test
    @DisplayName("Test frame with no payload")
    public void testNopayload() throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Framer f = new Framer(out);
        byte[] msg = new byte[6];

        f.putFrame(msg);
        byte[] resultBytes = out.toByteArray();
        assertAll(() -> assertArrayEquals(msg, Arrays.copyOfRange(resultBytes, 3, 9)), () -> assertEquals(resultBytes[0], 0), () -> assertEquals(resultBytes[1], 0), () -> assertEquals(resultBytes[2], 0));

    }

    /**
     * Tests that proper length is outputted for frame with only header
     *
     * @throws IllegalArgumentException if length is too big
     */
    @DisplayName("Test for payload that is too long")
    @Test
    public void testLength() {
        //initialize necessary variables
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Framer f = new Framer(out);
        byte[] msg = new byte[1000000];
        //frames an overly large byte array and throws illigal argument exception
        assertThrows(IllegalArgumentException.class, () -> {
            f.putFrame(msg);
        });
    }



}
