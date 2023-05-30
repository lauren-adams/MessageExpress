/************************************************
 *
 * Author: Lauren Adams
 * Assignment: Program 0
 * Class: Data Communications 4321
 *
 ************************************************/



package megex.serialization.test;

import megex.serialization.Deframer;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


import java.io.*;
import java.util.Arrays;

/**
 * tests for Deframer.java class
 */
public class DeframerTest {

    /**
     * constructor for DeframerTest
     */
    public DeframerTest() {

    }

    /**
     * input stream used in test methods
     */
    private ByteArrayInputStream in;


    /**
     * tests ioexception thrown by the readFully method in deframer
     */
    @Test
    @DisplayName("test IOException")
    public void ioExceptionTest(){
        //initialize byte array
        byte[] test = new byte[103];
        test[0] = 0;
        test[1] = 0;
        test[2] = 100;
        byte[] ans = new byte[100];
        //create byte array
        this.in = new ByteArrayInputStream(test);
        Deframer d = new Deframer(in);
        Assertions.assertThrows(IOException.class, () -> {
            byte[] msg = d.getFrame();
        });
    }


    /**
     * tests that deframer successfully returns next frame
     *
     * @throws IOException if cannot read data
     */
    @DisplayName("test basic deframe")
    @Test
    public void testDeframeBasic() throws IOException {
        byte[] test = new byte[103];
        test[0] = 0;
        test[1] = 0;
        test[2] = 94;
        byte[] ans = new byte[100];
        this.in = new ByteArrayInputStream(test);
        Deframer d = new Deframer(in);
        byte[] msg = d.getFrame();
        assert(Arrays.equals(msg, ans));
    }



    /**
     * tests that EOF is thrown with an empty array
     */
    @DisplayName("test EOF for empty array")
    @Test
    public void testPrematureEOF(){
        byte[] test = new byte[0];
        this.in = new ByteArrayInputStream(test);
        Deframer d = new Deframer(in);
        Assertions.assertThrows(EOFException.class, () -> {
            byte[] msg = d.getFrame();
        });

    }


    /**
     * tests Illegal argument exception if bad length
     */
    @DisplayName("Test throws exception with invalid length")
    @Test
    public void testInvalidLength(){
        byte[] test = new byte[103];
        test[0] = 0x00;
        test[1] = 0x50;
        test[2] = 0x00;
        this.in = new ByteArrayInputStream(test);
        Deframer d = new Deframer(in);

        //make msg throw ioexception
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            byte[] msg = d.getFrame();
        });
    }



    /**
     * constructor throws null if null
     */
    @DisplayName("Test null constructor")
    @Test
    public void testConstructor(){
        Assertions.assertThrows(NullPointerException.class, () -> {
            Deframer d = new Deframer(null);
        });
    }
}
