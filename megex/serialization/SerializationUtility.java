/************************************************
 *
 * Author: Lauren Adams
 * Assignment: Program 1
 * Class: Data Communications 4321
 *
 ************************************************/


package megex.serialization;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


/**
 * describes constants used by messages in Megex
 */
public class SerializationUtility {

    /**
     * constructor for Serialization utility class
     */
    public SerializationUtility() {
    }

    /**
     *  proper value to use to mask bytes for bit manipulation
     */
    public static final int BYTEMASK = 0xff;
    /**
     * value to mask out last value in 31 bit int
     */
    public static final int INTMASK = 0x7fffffff;
    /**
     * type indicator for data object
     */
    public static final int DATATYPE = 0x0;
    /**
     * type indicator for a settings object
     */
    public static final int SETTINGSTYPE = 0x4;
    /**
     * type indicator for a Window_Update object
     */
    public static final int WUTYPE = 0x8;
    /**
     * flags indicator for a badflags indicator
     */
    public static final int BADFLAGS = 0x8;
    /**
     * flags indicator for an endflag
     */
    public static final int ENDFLAGS = 0x1;
    /**
     * length of header
     */
    public static final int HEADERLENGTH = 6;
    /**
     * constant value used to shift bits
     */
    public static final int BYTESHIFT = 8;
    /**
     * Maximum length for a message object
     */
    public static final int MAXMESSAGELENGTH = 16390;

    /**
     * index for type
     */
    public static final int TYPEINDEX = 0;
    /**
     * index for flags
     */
    public static final int FLAGSINDEX = 1;
    /**
     * index for stream id
     */
    public static final int STREAMIDINDEX = 2;

    /**
     * index for payload
     */
    public static final int MAX31BITSIZE = 2147483647;

    /**
     * size of windows update object
     */
    public static final int WUSIZE = 10;

    /**
     * mask to check the R bit
     */
    public static final int RCHECK = 0x80000000;
    /**
     * Max length of data object
     */
    public static final int DATAMAXLEN = 16384;

    /**
     * Headers type indicator
     */
    public static final int HEADERSTYPE = 0x1;

    /**
     * constant used for encode/decode setting charset
     */
    private static final Charset CHARENC = StandardCharsets.US_ASCII;

    /**
     * converts string to byte
     * @param v string value to convert
     * @return byte array of string
     */
    public static byte[] s2b(String v) {
        return v.getBytes(CHARENC);
    }

    /**
     * converts byte to string
     * @param b byte array to convert
     * @return String version of byte array
     */
    public static String b2s(byte[] b) {
        return new String(b, CHARENC);
    }

}
