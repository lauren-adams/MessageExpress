package megex.serialization.test;

import megex.serialization.BadAttributeException;
import static org.junit.jupiter.api.Assertions.*;

import megex.serialization.Data;
import megex.serialization.Settings;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;


/**
 * Tests for Settings.java class
 */
public class SettingsTest {

    /**
     * constructor for settings test
     */
    public SettingsTest() {
    }

    /**
     * Tests that settings constructor fulfils its role
     * @throws BadAttributeException if settings is invalid
     */
    @DisplayName("constructor test")
    @Test
    public void testConstructor() throws BadAttributeException {
        Settings s = new Settings();
        assertEquals(s.toString(), "Settings: StreamID=0");
    }

    /**
     * Tests that settings constructor fulfils its role
     * @throws BadAttributeException if settings is invalid
     */
    @DisplayName("constructor streamID test")
    @Test
    public void testSetStreamId() throws BadAttributeException {
        Settings s = new Settings();
        assertEquals(s.toString(), "Settings: StreamID=0");
        assertThrows(BadAttributeException.class, () -> {
            s.setStreamID(5);
        });
        s.setStreamID(0);
    }
}
