package megex.serialization.test;

import megex.serialization.*;

import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


/**
 * Tets for Window_Update.java class
 */
public class Window_UpdateTest {

    /**
     * constructor for WUTest
     */
    public Window_UpdateTest() {
    }

    /**
     * tests no exception is thrown for stream type
     * @throws BadAttributeException if increment is invalid
     */
    @DisplayName("streamID test")
    @Test
    public void testConstructorStreamID() throws BadAttributeException {
        Window_Update wu = new Window_Update(0, 1);
        Window_Update wu2 = new Window_Update(5, 1);
        wu.setStreamID(0);
        assertEquals(wu.getIncrement(), wu2.getIncrement());
    }

    /**
     * Tests R is ignored for invalid increment with R bit set
     * @throws BadAttributeException if increment is invalid
     */
    @DisplayName("BadAttribute increment test")
    @Test
    public void testConstructorBadIncrement() throws BadAttributeException {
        assertThrows(BadAttributeException.class, () -> {
            //Window_Update d = new Window_Update(0, 1000000000);
            Window_Update wu = new Window_Update(1, 0xffffffff);
        });
    }

    /**
     * Tests that a good increment can be set
     * @throws BadAttributeException if reserved bit is set
     */
    @DisplayName("GoodAttribute increment test")
    @Test
    public void testConstructorGoodIncrement() throws BadAttributeException {
        Window_Update wu = new Window_Update(1, 0x0fffffff);
        assertEquals(wu.getIncrement(), 0x0fffffff);
    }

    /**
     * tests that constructor constructs the correct string value
     * @throws BadAttributeException if invalid data
     */
    @DisplayName("Constructor test")
    @Test
    public void testConstructor() throws BadAttributeException {
        Window_Update wu = new Window_Update(9, 78);
        assertEquals(wu.toString(), "Window_Update: StreamID=9 increment=78");
    }

    /**
     * Tests if proper error is thrown from invalid increment
     * @throws BadAttributeException if stream is 0
     */
    @DisplayName("BadAttribute increment test")
    @Test
    public void testIncrement() throws BadAttributeException {
        assertThrows(BadAttributeException.class, () -> {
            Window_Update d = new Window_Update(0, 1000000000);
            d.setIncrement(0);
        });
    }



}
