package megex.serialization.test;
import megex.serialization.*;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * tests for the Data.java class
 */
public class DataTest {

    /**
     * default constructor for DataTest
     */
    public DataTest() {
    }

    /**
     * tests to see if constructor of correct data object is successful
     *
     * @throws BadAttributeException if data is invalid
     */
    @DisplayName("Constructor Test")
    @Test
    public void testConstructor() throws BadAttributeException {
        Data d = new Data(2, false, new byte[2]);
        assertEquals(d.toString(), "Data: StreamID=2 isEnd=false data=2");
    }

    /**
     * Tests if proper error is thrown from invalid stream id
     * @throws BadAttributeException if stream is 0
     */
    @DisplayName("BadAttribute streamID test")
    @Test
    public void testStreamID() throws BadAttributeException {
        assertThrows(BadAttributeException.class, () -> {
            Data d = new Data(-1, true, new byte[2]);
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
            Data d = new Data(5, true, new byte[2]);
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
            Data d = new Data(0, true, null);
        });
    }
}
