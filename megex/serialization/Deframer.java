/************************************************
 *
 * Author: Lauren Adams
 * Assignment: Program 0
 * Class: Data Communications
 *
 ************************************************/


package megex.serialization;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;

import static megex.serialization.SerializationUtility.*;

/**
 * This class has the methods for getting the next frame
 * of a framed message
 */
public class Deframer {


    /**
     * This Data inout stream will be read from to get framed data
     */
    private DataInputStream in;

    /**
     * Construct deframer with given input stream
     *
     * @param in
     * byte source
     * @throws NullPointerException
     * if out is null
     */
    public Deframer(InputStream in){
        this.in = new DataInputStream(Objects.requireNonNull(in, "input cannot be null"));
    }

    /**
     * Returns length constructed from the first three bytes of the frame
     * @return int
     * the length as an int
     * @throws IOException if cannot read from input
     */
    public int getLength() throws IOException {
        //reads length from input
        int byte1 = in.readUnsignedByte();
        int byte2 = in.readUnsignedByte();
        int byte3 = in.readUnsignedByte();
        return  byte3 + (byte2  << BYTESHIFT) + (byte1 << (BYTESHIFT * 2));

    }

    /**
     * Get the next frame
     *
     * @return byte[]
     *  next frame NOT including the length (but DOES include the header)
     * @throws IOException
     * if I/O error occurs -> went to read 3 bytes and not 3 bytes
     * @throws IllegalArgumentException
     * if bad value in input stream (e.g., bad length) -> convert three bytes and not valid
     * @throws EOFException
     * if premature EOF -> cant read length, payload, header
     */
    public byte[] getFrame() throws IOException {
        //get length from input stream
        int length = getLength();

        //check for length
        if (length > MAXMESSAGELENGTH - HEADERLENGTH){
            throw new IllegalArgumentException("Bad length");
        }

        //read in rest of message
        byte[] ret = new byte[length + HEADERLENGTH];
        in.readFully(ret);
        return ret;
    }

}
