/************************************************
 *
 * Author: Lauren Adams
 * Assignment: Program 0
 * Class: Data Communications 4321
 *
 ************************************************/



package megex.serialization;

import java.io.*;
import java.util.Objects;

import static megex.serialization.SerializationUtility.*;

/**
 * This class specifies the methods for the class that frames messages
 */
public class Framer {

    /**
     * Data output stream to send framed message
     */
    private OutputStream out;




    /**
     * Construct framer with given output stream
     *
     * @param out output stream used to initialize function
     * @throws NullPointerException
     * if out is null
     */
    public Framer(OutputStream out){
        this.out = new DataOutputStream(Objects.requireNonNull(out, "input cannot be null"));
    }


    /**
     * Create a frame by adding the prefix length to the given message and sending the entire frame (i.e., prefix length, header, and payload)
     *
     * @param message
     *  next frame NOT including the prefix length (but DOES include the header)
     * @throws IOException
     * if I/O problem
     * @throws IllegalArgumentException
     * if invalid message (e.g., frame payload too long)
     * @throws NullPointerException
     * if message is null
     */
    public void putFrame(byte[] message) throws IOException {
        //check to make sure message is valid
        if (message == null){
            throw new NullPointerException("message is null");
        }

        if (message.length > MAXMESSAGELENGTH || message.length < HEADERLENGTH){
            throw new IllegalArgumentException("message length is invalid");
        }


        //get size of payload
        var payloadSize = message.length - HEADERLENGTH;
        //write payload in 3 bytes
        out.write((payloadSize >> (BYTESHIFT * 2)) & BYTEMASK);
        out.write((payloadSize >> BYTESHIFT) & BYTEMASK);
        out.write(payloadSize  & BYTEMASK);
        //write rest of message and flush
        out.write(message);
        out.flush();
    }


}
