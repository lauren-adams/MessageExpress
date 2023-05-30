/************************************************
 *
 * Author: Lauren Adams
 * Assignment: Program 1
 * Class: Data Communications 4321
 *
 ************************************************/

package megex.serialization;

import com.twitter.hpack.Decoder;
import com.twitter.hpack.Encoder;

import java.io.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.Vector;

import static megex.serialization.SerializationUtility.*;

/**
 * This class specifies the methods that encode and decode messages and byte streams
 */
public class MessageFactory {
    /**
     * Creates a message factory
     */
    public MessageFactory(){

    }

    /**
     * Byte array for encoding and decoding
     */
    private final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
    /**
     * Data output stream for encoding decoding
     */
    private final DataOutputStream out = new DataOutputStream(byteStream);

    /**
     * constant used for encode/decode setting max header size
     */
    private static final int MAXHEADERSZ = 1024;
    /**
     * constant used for encode/decode setting max header
     */
    private static final int MAXHEADERTBLSZ = 1024;
    /**
     * constant used for encode
     */
    private final Encoder encoder = new Encoder(MAXHEADERTBLSZ);

    private final Decoder decoder = new Decoder(MAXHEADERSZ, MAXHEADERTBLSZ);



    /**
     * Deserializes message from given bytes
     *
     * @param msgBytes message bytes
     * @return specific Message resulting from deserialization
     * @throws BadAttributeException if validation failure
     * @throws NullPointerException if msgBytes is null
     */
    public Message decode (byte[] msgBytes) throws BadAttributeException, NullPointerException {

        Objects.requireNonNull(msgBytes, "message cannot be null");

        //throw error if not properly sized
        if (msgBytes.length < HEADERLENGTH){
            throw new BadAttributeException("invalid length of header",
                    msgBytes.toString());
        }

        //get stream id from array
        int streamID = byteToInt(msgBytes, STREAMIDINDEX);

        switch (msgBytes[TYPEINDEX]) {
            case DATATYPE -> {
                if ((msgBytes[FLAGSINDEX] & BADFLAGS) > 0) {
                    throw new BadAttributeException("bad flags",
                            String.valueOf(msgBytes[FLAGSINDEX]));
                }
                return new Data(streamID, (msgBytes[FLAGSINDEX] & ENDFLAGS) != 0,
                        Arrays.copyOfRange(msgBytes, HEADERLENGTH, msgBytes.length));
            }
            case SETTINGSTYPE -> {
                //check if bad stream type for Settings
                if (streamID != 0) {
                    throw new BadAttributeException("Stream cannot be nonzero",
                            String.valueOf(streamID));
                }
                return new Settings();
            }
            case WUTYPE -> {
                //check structure of window_update
                if (msgBytes.length != WUSIZE) {
                    throw new BadAttributeException("Not structured as specified",
                            String.valueOf(streamID));
                }
                //get increment 31 bit int
                return new Window_Update(streamID, byteToInt(msgBytes, HEADERLENGTH));
            }
            case HEADERSTYPE -> {
                // decode header list from header block
                if ((msgBytes[FLAGSINDEX] & BADFLAGS) > 0) {
                    throw new BadAttributeException("bad flags",
                            String.valueOf(msgBytes[FLAGSINDEX]));

                }
                if ((msgBytes[FLAGSINDEX] & 0x4) == 0) {
                    throw new BadAttributeException("required flags not set",
                            String.valueOf(msgBytes[FLAGSINDEX]));
                }
                try {
                    return decodeHeaders(msgBytes, streamID);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            default ->
                //throw exception if incorrect type
                    throw new BadAttributeException("type is not valid",
                            String.valueOf(msgBytes[TYPEINDEX]));
        }

    }

    /**
     * Helper function used to encode the header object
     * @param msgBytes original message being encoded
     * @param streamID integer version of StreamId
     * @return header constructed from the byte array
     * @throws BadAttributeException if name/value pair is invalid
     * @throws IOException if reading in data fails
     */
    private Headers decodeHeaders(byte[] msgBytes, Integer streamID) throws BadAttributeException, IOException {

        ByteArrayInputStream bs;
        if ((msgBytes[FLAGSINDEX] & 0x20) > 0) {
            bs = new ByteArrayInputStream(Arrays.copyOfRange(msgBytes, 11, msgBytes.length));
        } else {
            bs = new ByteArrayInputStream(Arrays.copyOfRange(msgBytes, HEADERLENGTH, msgBytes.length));
        }

        DataInputStream in = new DataInputStream(bs);
        Vector<String> names = new Vector<>();
        Vector<String> values = new Vector<>();

        Headers h = new Headers(streamID, (msgBytes[FLAGSINDEX] & ENDFLAGS) != 0);
        decoder.decode(in, (name, value, sensitive) -> {
                names.add(b2s(name));
                values.add(b2s(value));
        });

        for (int i = 0; i < names.size(); i++){
            h.addValue(names.elementAt(i), values.elementAt(i));
        }
        decoder.endHeaderBlock();
        return h;
    }


    /**
     * serializes message
     * @param msg message to serialize
     * @return serialized message
     * @throws NullPointerException if message is null
     */
    public byte[] encode(Message msg) throws NullPointerException{
        Objects.requireNonNull(msg, "message cannot be null");
        byte[] ret = new byte[0];
        // call correct encoding function based on type
        switch(msg.getCode()){
            case DATATYPE -> ret = encodeData((Data)msg);
            case WUTYPE -> ret = encodeWindow_Update((Window_Update) msg);
            case SETTINGSTYPE -> ret = encodeSettings();
            case HEADERSTYPE -> {
                try {
                    ret = encodeHeaders((Headers) msg);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return ret;
    }

    /**
     * Encode Data object as a byte Array
     *
     * @param msg Data object being encoded
     * @return byte array of Data object
     */
    private byte[] encodeData(Data msg){
        //make appropriately sized byte array
        byte[] ret = new byte[HEADERLENGTH + msg.getData().length];
        ret[TYPEINDEX] = DATATYPE;
        //set flags to reflect Data values
        if (msg.isEnd()){
            ret[FLAGSINDEX] = ENDFLAGS;
        } else {
            ret[FLAGSINDEX] = 0;
        }
        //write stream ID into the byte array
        intToByte(ret, msg.getStreamID(), STREAMIDINDEX);
        //copy data between Data and arrays
        for (int i = 0; i < msg.getData().length; i++){
            ret[i + HEADERLENGTH] = msg.getData()[i];
        }
        return ret;
    }

    /**
     * Helper method that converts an int to a byte array
     *
     * @param ret byte array with int values
     * @param val the integer being encoded
     * @param start the index to write the array
     */
    private void intToByte(byte[] ret, int val, int start) {
        //write into a 4 byte array
        ret[start] = (byte) ((val >> BYTESHIFT * 3) & BYTEMASK);
        ret[start + 1] = (byte) ((val >> BYTESHIFT * 2) & BYTEMASK);
        ret[start + 2] = (byte) ((val >> BYTESHIFT) & BYTEMASK);
        ret[start + 3] = (byte) (val & BYTEMASK);
    }


    /**
     * Helper method that converts an int to a byte array
     *
     * @param msgBytes byte array with int values
     * @param start the index to read the array
     * @return int increment read out of byte stream
     */
    private int byteToInt(byte[] msgBytes, int start) {
        //translates 4 byte array into int
        int hi =  (((msgBytes[start] & BYTEMASK) << BYTESHIFT * 3)
                | ((msgBytes[start + 1] & BYTEMASK) << BYTESHIFT * 2)
                | ((msgBytes[start + 2] & BYTEMASK) << BYTESHIFT) |
                (msgBytes[start + 3] & BYTEMASK));
        return (hi & (~(1 << 31)));
    }



    /**
     * Encode Settings object as a byte Array
     *
     * @return byte array of Settings object
     */
    private byte[] encodeSettings(){
        //makes settings array and sets appropriate flags
        byte[] ret = new byte[HEADERLENGTH];
        Arrays.fill(ret, 0, HEADERLENGTH, (byte) 0x0);
        ret[TYPEINDEX] = SETTINGSTYPE;
        ret[FLAGSINDEX] = ENDFLAGS;
        return ret;
    }

    /**
     * Encode Windows_Update object as a byte Array
     *
     * @param msg Windows_Update object being encoded
     * @return byte array of Windows_Update object
     */
    private byte[] encodeWindow_Update(Window_Update msg){
        //makes Window_update array and initializes appropriate values
        byte[] ret = new byte[WUSIZE];
        ret[TYPEINDEX] = WUTYPE;
        ret[FLAGSINDEX] = 0x0;
        //write 31 bit stream id and increment to the array
        intToByte(ret, msg.getStreamID(), STREAMIDINDEX);
        intToByte(ret, msg.getIncrement(), HEADERLENGTH);
        return ret;
    }


    /**
     * Encode Headers object as a byte Array
     *
     * @param msg Headers object being encoded
     * @return byte array of Headers object
     */
    private byte[] encodeHeaders(Headers msg) throws IOException {
        byte[] ret = new byte[4];
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // write appropriate type/flags field
        out.write(HEADERSTYPE);
        if (msg.isEnd()){
            out.write(ENDFLAGS + 0x4);
        } else {
            out.write(0x4);
        }

        // translate streamId to byte array
        intToByte(ret, msg.getStreamID(), 0);
        out.write(ret);

        // Encode name/value pairs with encoder
        for(String name : msg.getNames()){
            encoder.encodeHeader(out, s2b(name), s2b(msg.getValue(name)), false);
        }

        return out.toByteArray();
    }

}
