package megex.serialization.test;

import static org.junit.jupiter.api.Assertions.*;

import megex.serialization.*;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.util.Arrays;

/**
 * tests for MessageFactory.java class
 */
public class MessageFactoryTest {

    /**
     * constructor for message factory test
     */
    public MessageFactoryTest() {
    }

    /**
     * tests basic decode function for data object
     * @throws BadAttributeException if data is invalid
     * @throws IOException if error reading
     */
    @DisplayName("Decode Data")
    @Test
    public void testDecodeData() throws BadAttributeException, IOException {
        byte[] msg = new byte[7];
        msg[0] = 0x0;
        msg[1] = 0x0;
        msg[2] = 0;
        msg[3] = 0;
        msg[4] = 0;
        msg[5] = 3;
        msg[6] = 21;
        MessageFactory mf = new MessageFactory();
        Data d = (Data) mf.decode(msg);
        byte[] data = d.getData();
        assertEquals(data[0], 21);
    }


    /**
     * tests basic decode function for data object
     * @throws BadAttributeException if data is invalid
     * @throws IOException if error reading
     */
    @DisplayName("Decode Data Big")
    @Test
    public void testDecodeDataBig() throws BadAttributeException, IOException {
        byte[] msg = new byte[11];
        msg[0] = 0x0;
        msg[1] = 0x0;
        msg[2] = 0;
        msg[3] = 0;
        msg[4] = 0;
        msg[5] = 3;
        msg[6] = 21;
        msg[7] = 21;
        msg[8] = 21;
        msg[9] = 21;
        msg[10] = 21;

        MessageFactory mf = new MessageFactory();
        Data d = (Data) mf.decode(msg);
        byte[] data = d.getData();
        assertArrayEquals(d.getData(), Arrays.copyOfRange(msg, 6, 11));
    }



    /**
     * tests basic decode function for data object
     * @throws BadAttributeException if data is invalid
     * @throws IOException if error reading
     */
    @DisplayName("Decode Data Big")
    @Test
    public void testDecodeDataRset() throws BadAttributeException, IOException {
        //byte[] msg = new byte[]{ 0, 1, 0, 0, 0, -1, 1, 2, 3, 4, 5};
        byte[] msg = new byte[]{0, 1, 127, -1, -1, -1, 1, 2, 3, 4, 5};
        /*msg[0] = 0x0;
        msg[1] = 0x1;
        msg[2] = 0;
        msg[3] = 0;
        msg[4] = 0;
        msg[5] = -1;
        msg[6] = 21;
        msg[7] = 21;
        msg[8] = 21;
        msg[9] = 21;
        msg[10] = 21;*/

        MessageFactory mf = new MessageFactory();
        Data d = (Data) mf.decode(msg);
        byte[] data = d.getData();
        assertArrayEquals(d.getData(), Arrays.copyOfRange(msg, 6, 11));
    }

    /**
     * tests that null encode function will throw correct exception
     */
    @DisplayName("Test null encode")
    @Test
    public void testNullEncode(){
        Assertions.assertThrows(NullPointerException.class, () -> {
            MessageFactory mf = new MessageFactory();
            mf.encode(null);
        });
    }

    /**
     * test that null decode function will throw correct error
     */
    @DisplayName("Test null decode")
    @Test
    public void testNullDecode(){
        Assertions.assertThrows(NullPointerException.class, () -> {
            MessageFactory mf = new MessageFactory();
            mf.decode(null);
        });
    }

    /**
     * test correct error is thrown when the header is the wrong length
     */
    @DisplayName("Test header length")
    @Test
    public void testlengthDecode(){
        Assertions.assertThrows(BadAttributeException.class, () -> {
            MessageFactory mf = new MessageFactory();
            mf.decode(new byte[3]);
        });
    }

    /**
     * tests that when the type is invalid the correct error is thrown
     */
    @DisplayName("Test bad type")
    @Test
    public void testbadtype(){
        Assertions.assertThrows(BadAttributeException.class, () -> {
            MessageFactory mf = new MessageFactory();
            byte[] test = new byte[10];
            test[0] = 0x5;
            mf.decode(test);
        });
    }


    /**
     * basic decode for windows update object
     * @throws BadAttributeException if windows_update object is invalid
     * @throws IOException if error reading
     */
    @DisplayName("Decode WindowsUpdate")
    @Test
    public void testDecodeWindowsUpdate() throws BadAttributeException, IOException {
        byte[] msg = new byte[10];
        msg[0] = 0x8;
        msg[1] = 0x0;
        msg[2] = 0;
        msg[3] = 0;
        msg[4] = 0;
        msg[5] = 3;
        msg[6] = 0;
        msg[7] = 0;
        msg[8] = 0;
        msg[9] = 1;

        MessageFactory mf = new MessageFactory();
        Window_Update wu = (Window_Update) mf.decode(msg);
        assertEquals(wu.getIncrement(), 1);
    }

    /**
     * basic decode for settings object
     * @throws BadAttributeException if settings object is invalid
     * @throws IOException if error reading
     */
    @DisplayName("Decode Settings")
    @Test
    public void testDecodeSettings() throws BadAttributeException, IOException {
        byte[] msg = new byte[6];
        msg[0] = 0x4;
        msg[1] = 0x1;
        msg[2] = 0;
        msg[3] = 0;
        msg[4] = 0;
        msg[5] = 0;

        MessageFactory mf = new MessageFactory();
        Settings s = (Settings) mf.decode(msg);
        assertEquals(s.toString(), "Settings: StreamID=0");
    }

    /**
     * basic decode for settings with invalid StreamID
     * @throws BadAttributeException if settings object is invalid
     */
    @DisplayName("Decode Settings Invalid StreamId")
    @Test
    public void testDecodeSettingsBadStreamID() throws BadAttributeException {
        byte[] msg = new byte[6];
        msg[0] = 0x4;
        msg[1] = 0x1;
        msg[2] = 0;
        msg[3] = 2;
        msg[4] = 0;
        msg[5] = 9;

        MessageFactory mf = new MessageFactory();

        Assertions.assertThrows(BadAttributeException.class, () -> {
            Settings s = (Settings) mf.decode(msg);
        });

    }

    /**
     * tests basic encoding of data object
     * @throws BadAttributeException if data is invalid
     * @throws IOException if error reading
     */
    @DisplayName("Encode Data")
    @Test
    public void testEncodeData() throws BadAttributeException, IOException {
        byte[] data = new byte[1];
        data[0] = 5;
        Data d = new Data(3, false, data);
        MessageFactory m = new MessageFactory();
        byte[] results = m.encode(d);

        byte[] msg = new byte[7];
        msg[0] = 0x0;
        msg[1] = 0x0;
        msg[2] = 0;
        msg[3] = 0;
        msg[4] = 0;
        msg[5] = 3;
        msg[6] = 5;

        assertArrayEquals(msg, results);
    }

    /**
     * basic encode for window_update object
     * @throws BadAttributeException if window update object is invalid
     * @throws IOException if error reading
     */
    @DisplayName("Encode WindowsUpdate")
    @Test
    public void testEncodeWindowsUpdate() throws BadAttributeException, IOException {
        Window_Update wu = new Window_Update(3, 1);
        MessageFactory m = new MessageFactory();
        byte[] results = m.encode(wu);

        byte[] msg = new byte[10];
        msg[0] = 0x8;
        msg[1] = 0x0;
        msg[2] = 0;
        msg[3] = 0;
        msg[4] = 0;
        msg[5] = 3;
        msg[6] = 0;
        msg[7] = 0;
        msg[8] = 0;
        msg[9] = 1;

        assertArrayEquals(msg, results);
    }

    /**
     * basic encode for settings object
     * @throws BadAttributeException if invalid encoding
     * @throws IOException if error reading
     */
    @DisplayName("Encode Settings")
    @Test
    public void testEncodeSettings() throws BadAttributeException, IOException {
        Settings s = new Settings();
        MessageFactory m = new MessageFactory();
        byte[] results = m.encode(s);

        byte[] msg = new byte[6];
        msg[0] = 0x4;
        msg[1] = 0x1;
        msg[2] = 0;
        msg[3] = 0;
        msg[4] = 0;
        msg[5] = 0;

        assertArrayEquals(msg, results);
    }

    /**
     * tests basic decode function correctly sets isEnd
     * @throws BadAttributeException if data is invalid
     * @throws IOException if error reading
     */
    @DisplayName("Decode Data isEnd")
    @Test
    public void testDecodeDataIsEnd() throws BadAttributeException, IOException {
        byte[] msg = new byte[8];
        msg[0] = 0x0;
        msg[1] = 0x1;
        msg[2] = 0;
        msg[3] = 0;
        msg[4] = 0;
        msg[5] = 3;
        msg[6] = 21;
        msg[7] = 33;
        MessageFactory mf = new MessageFactory();
        Data d = (Data) mf.decode(msg);
        byte[] data = d.getData();
        assertAll(() -> assertEquals(data[0], 21), () -> assertEquals(data[1], 33), () ->assertTrue(d.isEnd()));
        //check to make sure isEnd reflects not isEnd
        msg[1] = 0x0;
        Data d2 = (Data) mf.decode(msg);
        assertFalse(d2.isEnd());


    }


    /**
     * tests basic decode function correctly sets isEnd
     * @throws BadAttributeException if data is invalid
     */
    @DisplayName("Decode Data Bad flags")
    @Test
    public void testDecodeBadFlags() throws BadAttributeException {
        byte[] msg = new byte[8];
        msg[0] = 0x0;
        msg[1] = 0x8;
        msg[2] = 0x8;
        msg[3] = 0;
        msg[4] = 0;
        msg[5] = 3;
        msg[6] = 21;
        msg[7] = 33;
        MessageFactory mf = new MessageFactory();
        Assertions.assertThrows(BadAttributeException.class, () -> {
            mf.decode(msg);
        });
    }


    /**
     * tests basic ignores R bit when recieving and is unset when sending
     * @throws BadAttributeException if data is invalid
     * @throws IOException if error reading
     */
    @DisplayName("Encode Data StreamId")
    @Test
    public void testBigStreamID() throws BadAttributeException, IOException {
        byte[] data = new byte[1];
        data[0] = 5;
        //test ignores last bit
        Data d = new Data(0x44444444, false, data);
        MessageFactory m = new MessageFactory();
        byte[] results = m.encode(d);

        //constructs byte array to compare
        byte[] msg = new byte[7];
        msg[0] = 0x0;
        msg[1] = 0x0;
        msg[2] = (byte) 0x44;
        msg[3] = (byte) 0x44;
        msg[4] = (byte) 0x44;
        msg[5] = (byte) 0x44;
        msg[6] = 5;

        assertArrayEquals(msg, results);
    }

    /**
     * tests basic ignores R bit when recieving and is unset when sending
     * @throws BadAttributeException if data is invalid
     * @throws IOException if error reading
     */
    @DisplayName("Encode Headers StreamId")
    @Test
    public void testEncodeHeaders() throws BadAttributeException, IOException {
        //test ignores last bit
        Headers h = new Headers(5,false);
        h.addValue(":method", "GET");
        h.addValue(":path", "http://cnn.com");
        MessageFactory m = new MessageFactory();
        byte[] results = m.encode(h);
        byte[] msg = new byte[]{1, 4, 0, 0, 0, 5, -126, 68, -118, -99, 41, -82, -29, 12, 18, -86, -105, 33, -23 };


        //constructs byte array to compare
        String a = "";

        for (int i = 0; i < results.length; i++) {
            a += results[i] + " ";
        }
        //assertEquals(a, results.toString());
        assertArrayEquals(results, msg);
    }


    /**
     * tests basic decode function correctly sets isEnd
     * @throws BadAttributeException if data is invalid
     * @throws IOException if error reading
     */
    @DisplayName("Decode Headers")
    @Test
    public void testDecodeHeaders() throws BadAttributeException, IOException {
        byte[] msg = new byte[]{1, 4, 0, 0, 0, 5, -126, 68, -118, -99, 41, -82, -29, 12, 18, -86, -105, 33, -23 };
        //byte[] msg = new byte[]{1, 4, 0, 0, 0, 3, -120, 127, 24, -47, -122, -2, -65, 68, -110, -34, -13, 73, -92, -1, -121, -24, -18, 22, -83, -24, 122, -15, 52, 10, 41, -66, -41, 43, 100, -113, 127, -99, 60, 53, 52, -18, 77, -77, 71, -20, 11, 27, -76, -46, -112, 87, -74, -28, -28, -50, 95, -54, 48, -19, -119, -90, -11, -32, -15, -5, -93, 31, 67, -15, -17, -109, -93, -108, -29, 78, -24, -91, -42, 18, -35, -4, -40, -73, 23, -21, -38, -77, -106, -2, -1, 127, 24, -117, 11, -113, 1, -25, -124, -5, 64, 0, 2, 39, 95, -41, 127, 23, -122, 52, -123, -87, 38, 79, -81, 127, 23, -123, 11, 33, 121, -106, -65, 127, 23, -120, 11, -113, 1, -25, 95, 121, -90, -1, 127, 21, -116, 37, -124, 100, 68, -126, -24, -1, 101, -1, 126, 32, -125, 126, -104, -90, 70, -32, -79, -1, -10, -12, -9, -86, -21, 127, -106, 30, 88, 119, -24, -77, 69, -19, 67, -49, 52, 16, 127, -43, -45, -46, 88, -115, -82, -40, -24, 49, 62, -108, -92, 126, 86, 28, -59, -128, 31, 108, -106, -61, 97, -66, -108, 3, -86, 67, 93, -118, 8, 2, 101, 64, -73, 113, -74, -18, 52, 5, 49, 104, -33, 98, -103, -2, 71, 44, -110, -58, 71, -102, 19, -27, 104, 18, 56, 22, -36, 11, 108, -78, 40, -39, 0, -103, 124, 98, 47, -13, 95, -121, 53, 35, -104, -84, 87, 84, -33, -40, -48, -49, -47, -50, -51, -52, 127, 12, -107, -36, 23, 30, 8, 91, 120, 45, -76, -69, 110, 0, 44, -77, -11, -57, 112, 62, -72, -32, 19, -97, 123, -123, -43, 97, -90, 53, 95, -53, 15, 13, 6, 49, 51, 49, 56, 51, 52};


        MessageFactory mf = new MessageFactory();
        Headers h = (Headers) mf.decode(msg);
        assertEquals(h.toString(), "Headers: StreamID=5 isEnd=false ([:method=GET][:path=http://cnn.com])");
        assertFalse(h.isEnd());

    }


    /**
     * tests basic decode function correctly sets isEnd
     * @throws BadAttributeException if data is invalid
     * @throws IOException if error reading
     */
    @DisplayName("HeadersErrors")
    @Test
    public void testHeadersErrors() throws BadAttributeException, IOException {
        byte[] msg = new byte[]{1, 0x20, 0, 0, 0, 5, -126, 68, -118, -99, 41, -82, -29, 12, 18, -86, -105, 33, -23 };

        Assertions.assertThrows(BadAttributeException.class, () -> {
            MessageFactory mf = new MessageFactory();
            mf.decode(msg);
        });
        msg[1] = 0x8;
        Assertions.assertThrows(BadAttributeException.class, () -> {
            MessageFactory mf = new MessageFactory();
            mf.decode(msg);
        });
        msg[1] = 0x0;
        Assertions.assertThrows(BadAttributeException.class, () -> {
            MessageFactory mf = new MessageFactory();
            mf.decode(msg);
        });

    }

    /**
     * tests basic decode function correctly sets isEnd
     * @throws BadAttributeException if data is invalid
     * @throws IOException if error reading
     */
    @DisplayName("Decode Data isEnd")
    @Test
    public void testDecodeDataIsEnd2() throws BadAttributeException, IOException {
        MessageFactory mf = new MessageFactory();
        byte[] msg = new byte[]{1, 36, 0, 0, 0, 4, -126, 68, -125, 98, 83, -97, 64, -122, -71, -36, -74, 32, -57, -85, -121, -57, -65, 126, -74, 2, -72, 127};
        Headers d = (Headers)mf.decode(msg);

        //assertAll(() -> assertEquals(data[0], 21), () -> assertEquals(data[1], 33), () ->assertTrue(d.isEnd()));
        //check to make sure isEnd reflects not isEnd

        Headers d2 = (Headers) mf.decode(msg);
        System.out.println(d);
        System.out.println(d2);


    }


}
