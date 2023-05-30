package jack.serialization.test;

import jack.serialization.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * class dor testing message class
 */
public class MessageTest {

    /**
     * creates a messageTest class object
     */
    public MessageTest() {
    }

    /**
     * tests making a query encoding and decoding it
     */
    @DisplayName("Encode/Decode Query")
    @Test
    public void testQuery() {

        String q = "Q goog";
        Message msg = Message.decode(q.getBytes(StandardCharsets.US_ASCII));

        assertEquals(msg.getOperation(), "Q");
        assertEquals(msg.toString(), "QUERY goog");
        assertArrayEquals(msg.encode(), q.getBytes());

    }

    /**
     * tests making an error encoding and decoding it
     */
    @DisplayName("Encode/Decode Error")
    @Test
    public void testError() {

        String e = "E bad thing";
        Message msg = Message.decode(e.getBytes(StandardCharsets.US_ASCII));

        assertEquals(msg.getOperation(), "E");
        assertEquals(msg.toString(), "ERROR bad thing");
        assertArrayEquals(msg.encode(), e.getBytes());

    }

    /**
     * tests making an error encoding and decoding it
     */
    @DisplayName("Encode/Decode Error Fail")
    @Test
    public void testErrorFail() {

        String e = "E";

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Message msg = Message.decode(e.getBytes(StandardCharsets.US_ASCII));
        });
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Message msg = Message.decode(null);
        });

    }

    /**
     * tests making a New encoding and decoding it
     */
    @DisplayName("Encode/Decode New")
    @Test
    public void testNew() {

        String n = "N wind:8000";
        Message msg = Message.decode(n.getBytes(StandardCharsets.US_ASCII));

        assertEquals(msg.getOperation(), "N");
        assertEquals(msg.toString(), "NEW [wind:8000]");
        assertArrayEquals(msg.encode(), n.getBytes());

    }

    /**
     * tests making an ACK encoding and decoding it
     */
    @DisplayName("Encode/Decode ACK")
    @Test
    public void testAck() {

        String n = "A wind:8000";
        Message msg = Message.decode(n.getBytes(StandardCharsets.US_ASCII));

        assertEquals(msg.getOperation(), "A");
        assertEquals(msg.toString(), "ACK [wind:8000]");
        assertArrayEquals(msg.encode(), n.getBytes());

    }

    /**
     * tests making a response encoding and decoding it
     */
    @DisplayName("Encode/Decode response")
    @Test
    public void testResponse() {
    //Make sure spaces make sense
        String n = "R wind:8000 fire:6000 ";
        Message msg = Message.decode(n.getBytes(StandardCharsets.US_ASCII));
        String n2 = "R fire:6000 wind:8000 ";
        assertEquals(msg.getOperation(), "R");
        assertEquals(msg.toString(), "RESPONSE [fire:6000][wind:8000]");
        assertArrayEquals(msg.encode(), n2.getBytes());

    }


    /**
     * tests making a response encoding and decoding it
     */
    @DisplayName("Encode/Decode response fail")
    @Test
    public void testResponseFail() {
        //Make sure spaces make sense
        String n = "R wind:800000 fire:6000 ";


        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Message msg = Message.decode(n.getBytes(StandardCharsets.US_ASCII));
        });

        String n2 = "R win--@@@d:8000 fire:6000 ";
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Message msg = Message.decode(n2.getBytes(StandardCharsets.US_ASCII));
        });

        String n3 = "N wind:8000";
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Message msg = Message.decode(n3.getBytes(StandardCharsets.US_ASCII));
            ((New) msg).setPort(800000);
        });

        String n4 = "N wind:8000";
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Message msg = Message.decode(n4.getBytes(StandardCharsets.US_ASCII));
            ((New) msg).setHost("...>>.");
        });

        String n5 = "N wind8000";
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Message msg = Message.decode(n5.getBytes(StandardCharsets.US_ASCII));
        });

        String n6 = "N wind:one";
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Message msg = Message.decode(n6.getBytes(StandardCharsets.US_ASCII));
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Service s = new Service(null,444);
        });


    }


    /**
     * tests making a response encoding and decoding it
     */
    @DisplayName("Encode/Decode response List")
    @Test
    public void testResponseList() {
        String n = "R wind:8000 fire:6000 ";
        Message msg = Message.decode(n.getBytes(StandardCharsets.US_ASCII));
        ((Response) msg).addService("lauren", 1900);
        ((Response) msg).addService("adams", 2000);
        ((Response) msg).addService("lauren", 2000);
        assertEquals(msg.getOperation(), "R");
        assertEquals(msg.toString(), "RESPONSE [adams:2000][fire:6000][lauren:1900][lauren:2000][wind:8000]");
    }


    /**
     * tests making if I can make anything with null values
     */
    @DisplayName("null fail")
    @Test
    public void testNullFail() {
        //Make sure spaces make sense
        String n = "R wind:8000 fire:6000 ";

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Message msg = Message.decode(n.getBytes(StandardCharsets.US_ASCII));
            ((Response) (msg)).addService(null, 33);
        });

        String n2 = "R win--@@@d:8000 fire:6000 ";
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Message msg = Message.decode(n2.getBytes(StandardCharsets.US_ASCII));
        });

        String n3 = "E wind:8000";
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Message msg = Message.decode(n3.getBytes(StandardCharsets.US_ASCII));
            ((jack.serialization.Error) msg).setErrorMessage(null);
        });

        String n9 = "E wind:8000";
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Message msg = Message.decode(n9.getBytes(StandardCharsets.US_ASCII));
            ((jack.serialization.Error) msg).setErrorMessage("");
        });

        String n5 = "Q helllp";
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Message msg = Message.decode(n5.getBytes(StandardCharsets.US_ASCII));
            ((Query) msg).setSearchString(null);
        });

        String n7 = "Q helllp";
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Message msg = Message.decode(n7.getBytes(StandardCharsets.US_ASCII));
            ((Query) msg).setSearchString("");
        });

        String n10 = "Q ";
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Message msg = Message.decode(n10.getBytes(StandardCharsets.US_ASCII));
        });

        String n12 = "E ";
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Message msg = Message.decode(n12.getBytes(StandardCharsets.US_ASCII));
        });

        String n13 = "N ";
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Message msg = Message.decode(n13.getBytes(StandardCharsets.US_ASCII));
        });


        String n4 = "N wind:8000";
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Message msg = Message.decode(n4.getBytes(StandardCharsets.US_ASCII));
            ((New) msg).setHost(null);
        });

        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Service s = new Service(null,444);
        });

        String n11 = "R ";
        Message msg = Message.decode(n11.getBytes(StandardCharsets.US_ASCII));

    }


}
