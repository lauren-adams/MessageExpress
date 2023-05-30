/************************************************
 *
 * Author: Lauren Adams
 * Assignment: Program 1
 * Class: Data Communications 4321
 *
 ************************************************/

package jack.serialization;


import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

/**
 * describes constants used by messages in Jack
 */
public class JackUtility {

    /**
     * constructor for Serialization utility class
     */
    public JackUtility() {
    }

    /**
     * MaxUDP payload size
     */
    public static final int UDPMAX = 65527;

    /**
     * Max port number
     */
    public static final int PORTMAX = 65535;

    /**
     * MMinimum valid message
     */
    public static final int MINJACK = 2;

    /**
     * op code of ACK
     */
    public static final char OPACK = 'A';

    /**
     * op code of Error
     */
    public static final char OPERR = 'E';

    /**
     * op code of New
     */
    public static final char OPNEW = 'N';

    /**
     * op code of Query
     */
    public static final char OPQUERY = 'Q';

    /**
     * op code of Response
     */
    public static final char OPRES = 'R';

    /**
     * char to used to divide port and host
     */
    public static final String SPLITER = ":";

    /**
     * constant used for encode/decode setting charset
     */
    static final Charset CHARENC = StandardCharsets.US_ASCII;


}