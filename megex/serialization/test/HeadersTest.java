/************************************************
 *
 * Author: Lauren Adams
 * Assignment: Program 2
 * Class: Data Communications 4321
 *
 ************************************************/

package megex.serialization.test;
import com.twitter.hpack.Encoder;
import megex.serialization.*;

import static megex.serialization.SerializationUtility.s2b;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

//[1, 4, 0, 0, 0, 3, -120, 127, 23, -48, -122, -2, -65, 68, -111, 53, -2, -2, -30, -37, 110, 87, -36, -69, -67, -49, -33, 81, 101, -31, 75, -38, 61, 98, 19, -81, 118, -28, -117, 112, -50, 122, -83, 100, -117, 126, -5, 15, 115, -37, 127, -125, -73, 117, 122, -25, -80, 87, -72, -97, 39, -92, -34, -59, -118, 107, 113, -94, 67, -5, -29, 79, -110, 2, 30, 22, -125, 44, -3, -8, 14, 57, 39, -65, -8, -1, -31, 79, -53, -9, 127, 19, -117, 11, -113, 1, -25, -124, -5, 64, 0, 2, 39, 95, -46, 127, 18, -122, 52, -123, -87, 38, 79, -81, 127, 18, -123, 11, 33, 121, -106, -65, 127, 18, -120, 11, -113, 1, -25, 95, 121, -90, -1, 127, 15, -116, 37, -124, 100, 68, -126, -24, -1, 101, -1, 126, 32, -125, 126, -104, -90, 70, -32, -79, -1, -10, -12, -9, -86, -21, 127, -106, 30, 88, 119, -24, -77, 69, -19, 67, -49, 52, 16, 127, -49, -51, -37, 88, -115, -82, -40, -24, 49, 62, -108, -92, 126, 86, 28, -59, -128, 31, 108, -106, -61, 97, -66, -108, 3, -86, 67, 93, -118, 8, 2, 101, 64, -73, 113, -74, -18, 52, 5, 49, 104, -33, 98, -103, -2, 71, 44, -110, -58, 71, -102, 19, -27, 104, 18, 56, 22, -36, 11, 108, -78, 40, -39, 0, -103, 124, 98, 47, -13, 95, -121, 53, 35, -104, -84, 87, 84, -33, -46, -48, -49, 85, 1, 48, -49, -50, -51, 127, 13, -107, -36, 23, 30, 8, 91, 125, -95, 5, 112, 54, 33, 54, -17, -41, 29, -64, -6, -29, -128, 79, -1, 123, -123, -43, 97, -90, 53, 95, -52, 15, 13, 6, 49, 51, 49, 56, 51, 52]
//[1, 4, 0, 0, 0, 1, -120, 64, -113, -14, -76, -42, -41, 65, -57, 33, 108, 90, -38, -24, 56, -28, 52, -97, -53, -122, -2, -65, 68, -111, 23, -12, 108, -55, -27, -61, -100, -100, 115, 116, -25, -49, 54, -97, -53, -36, -42, -96, 94, -57, -93, -96, 91, -27, -121, 116, 93, 3, -18, 125, -5, -76, -8, -21, -8, 28, 7, -110, 120, -95, -49, -125, 71, -96, 94, -37, -25, -94, 61, -113, 41, -84, -113, 37, 54, -2, 88, 88, -25, -89, -66, 106, 123, -17, 62, -105, 122, -48, 117, -90, 64, -116, -14, -76, -57, 60, -53, 76, 90, -117, 96, -46, 99, -43, -117, 11, -113, 1, -25, -124, -5, 64, 0, 2, 39, 95, 64, -113, -14, -76, -57, 60, -53, 82, 84, -114, 98, -44, 91, 6, -109, 30, -81, 1, 49, 64, -107, -14, -76, -57, 60, -53, 33, 39, -80, -78, 44, 67, -44, -110, -44, -107, -117, 81, 15, 33, -86, -101, -122, 52, -123, -87, 38, 79, -81, 64, -108, -14, -76, -57, 60, -53, 33, 39, -80, -78, 44, 67, -44, -110, -44, -107, -88, 45, 83, 38, 127, -123, 11, 33, 121, -106, -65, 64, -102, -14, -76, -57, 60, -53, 82, 84, -115, 105, -114, 121, -106, -80, -88, 45, -99, -53, 34, -46, -102, -126, -83, 74, 77, 73, 127, -120, 11, -113, 1, -25, 95, 121, -90, -1, 64, -120, -14, -76, -57, 60, -53, 78, 52, 79, -116, 37, -124, 100, 68, -126, -24, -1, 101, -1, 126, 32, -125, 126, -104, -90, 70, -32, -79, -1, -10, -12, -9, -86, -21, 127, -106, 30, 88, 119, -24, -77, 69, -19, 67, -49, 52, 16, 127, 64, -114, -14, -76, -57, 60, -53, 33, 39, -80, 115, 21, 98, 80, 52, 35, -116, -47, -61, 62, -4, -111, 109, -63, -117, 38, -83, 48, -25, 118, -119, -31, 93, 7, 28, -101, -117, 103, 114, -39, 100, -106, -48, 122, -66, -108, 8, 20, -122, -69, 20, 16, 4, -54, -126, 5, -58, -98, -72, 32, -87, -117, 70, -1, 88, -115, -82, -40, -24, 49, 62, -108, -92, 126, 86, 28, -59, -128, 31, 108, -106, -61, 97, -66, -108, 3, -86, 67, 93, -118, 8, 2, 101, 64, -73, 113, -74, -18, 52, 5, 49, 104, -33, 98, -103, -2, 71, 44, -110, -58, 71, -102, 19, -27, 104, 18, 56, 22, -36, 11, 108, -78, 40, -39, 0, -103, 124, 98, 47, -13, 95, -121, 53, 35, -104, -84, 87, 84, -33, 82, -124, -113, -46, 74, -113, 97, -106, -48, 122, -66, -108, 8, 20, -122, -69, 20, 16, 4, -54, -126, 5, -58, -98, -72, 32, -87, -117, 70, -1, 124, -120, 10, -31, 83, -72, -20, -88, -56, -97, 85, 1, 48, 64, -119, -14, -78, 11, 103, 114, -56, -76, 126, -65, -109, 32, -55, 57, 86, -110, 95, 11, 117, -110, 95, 15, 8, 32, 8, 4, -75, -4, 60, -65, 64, -123, -14, -79, 6, 73, -53, 4, 77, 73, 83, 83, 64, -119, -14, -79, 6, 73, -54, -76, -26, 74, 63, 1, 48, 64, -123, -14, -78, 77, 73, 108, -107, -36, 23, 30, 8, 91, 125, -48, 9, 119, 27, 101, -42, 91, -6, -29, -72, 31, 92, 112, 50, -25, 123, -123, -43, 97, -90, 53, 95, 120, -120, -92, 126, 86, 28, -59, -127, -112, 3, 15, 13, 6, 49, 51, 49, 56, 51, 52]
/**
 * tests for the Headers.java class
 */
public class HeadersTest {

    /**
     * default constructor for HeadersTest
     */
    public HeadersTest() {
    }

    /**
     * tests to see if constructor of correct headers object is successful
     *
     * @throws BadAttributeException if data is invalid
     */
    @DisplayName("ValidityTest Test")
    @Test
    public void testValidity() throws BadAttributeException {
        Headers h = new Headers(2, true);
        h.addValue("hello", "FiENDR");
        assertThrows(BadAttributeException.class, () -> {
            h.addValue("NAME", "BAD");
        });
        assertThrows(BadAttributeException.class, () -> {
            h.addValue("NCHAR", "hellofriend()()()");
        });
        h.addValue("vischar", String.valueOf(0x20));
        assertThrows(BadAttributeException.class, () -> {
            h.addValue(null, null);
        });
        h.addValue("value", String.valueOf(0x9));
    }

    /**
     * Tests if proper error is thrown from invalid stream id
     * @throws BadAttributeException if stream is 0
     */
    @DisplayName("BadAttribute streamID test")
    @Test
    public void testStreamID() throws BadAttributeException {
        assertThrows(BadAttributeException.class, () -> {
            Headers d = new Headers(-1, true);
        });
    }

    /**
     * Tests if proper error is thrown from invalid stream id setter
     * @throws BadAttributeException if stream is 0
     */
    @DisplayName("BadAttribute streamID setter test")
    @Test
    public void testStreamIDSetter() throws BadAttributeException {
        assertThrows(BadAttributeException.class, () -> {
            Headers d = new Headers(5, true);
            d.setStreamID(0);
        });
    }

    /**
     * Test that proper error is thrown for invalid data object
     * @throws BadAttributeException if data is invalid
     */
    @DisplayName("BadAttribute invalid data test")
    @Test
    public void testInvalidData() throws BadAttributeException {
        assertThrows(BadAttributeException.class, () -> {
            Headers d = new Headers(-2, false);
        });
    }


    /**
     * tests to see if we can construct proper lists
     *
     * @throws BadAttributeException if data is invalid
     */
    @DisplayName("ValidityTest Test")
    @Test
    public void getValues() throws BadAttributeException {
        Headers h = new Headers(2, true);
        h.addValue("one", "ONE");
        h.addValue("two", "TWO");
        h.addValue("three", "THREE");
        String results = h.getNames().toString();
        assertEquals(results, "[one, two, three]");
    }

    /**
     * tests to see if we can construct toString
     *
     * @throws BadAttributeException if data is invalid
     */
    @DisplayName("toString Test")
    @Test
    public void testString() throws BadAttributeException {
        Headers h = new Headers(2, true);
        h.addValue("one", "ONE");
        h.addValue("two", "TWO");
        h.addValue("three", "THREE");
        assertEquals(h.toString(), "Headers: StreamID=2 isEnd=true ([one=ONE][two=TWO][three=THREE])");
    }

    /**
     * tests to see if we can construct proper lists
     *
     * @throws BadAttributeException if data is invalid
     */
    @DisplayName("List maintenence Test")
    @Test
    public void testList() throws BadAttributeException {
        Headers h = new Headers(2, true);
        h.addValue("one", "ONE");
        h.addValue("two", "TWO");
        h.addValue("three", "THREE");
        h.addValue("one", "ONE");
        String results = h.getNames().toString();
        assertEquals(results, "[two, three, one]");
    }


    /**
     * provided method to test headers
     * @throws BadAttributeException if header input incorrect
     * @throws IOException if reading causes error
     */
    @DisplayName("Provided Test")
    @Test
    public void testList2() throws BadAttributeException, IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        //out.write(new byte[] { 1, 0x4, 0, 0, 0, 1 });
        out.write(new byte[] { 1, 0x4, 0, 0, 0, 1 });
        new Encoder(1024).encodeHeader(out, s2b("x"), s2b("1"), false);

        // Actual
        Headers h = new Headers(1, false);
        h.addValue("x", "1");
        assertArrayEquals(out.toByteArray(), new MessageFactory().encode(h));
    }
}

