/************************************************
 *
 * Author: Lauren Adams
 * Assignment: Program 4
 * Class: Data Communications 4321
 *
 ************************************************/

package jack.serialization;
import static jack.serialization.JackUtility.*;

/**
 * Represents a Jack message
 */
public abstract class Message {

    /**
     * constructs a message, necessary for Javadoc
     */
    public Message() {
    }

    /**
     * Deserialize message from given bytes
     * @param msgBytes message bytes
     * @return specific Message resulting from deserialization
     * @throws IllegalArgumentException if validation fails
     */
    public static Message decode(byte[] msgBytes)
            throws IllegalArgumentException {
        if (msgBytes == null || msgBytes.length < MINJACK
                || msgBytes.length > UDPMAX || msgBytes[1] != ' '){
            throw new IllegalArgumentException( "invalid data to decode");
        }
        String msg = new String(msgBytes, CHARENC);
        msg = msg.substring(msg.indexOf(" ") + 1);
        switch(msgBytes[0]){
            case OPQUERY -> {
                return new Query(msg);
            }
            case OPRES -> {
                   return createResponse(msg);
            }
            case OPNEW -> {
                return new New(splitHost(msg), splitPort(msg));
            }
            case OPACK -> {
                return new ACK(splitHost(msg), splitPort(msg));
            }
            case OPERR -> {
                return new Error(msg);
            }
            default -> throw new IllegalArgumentException("validation " +
                    "fails: illegal op");
        }
    }

    /**
     * helper method to split a sevrice into it's halves
     * @param str service to split
     * @return array with each half stored
     */
    private static Integer splitPort(String str) throws IllegalArgumentException {
        if (!str.contains(SPLITER)){
            throw new IllegalArgumentException("Invalid Service structure");
        }
        return Integer.parseInt(str.split(SPLITER)[1]);
    }

    /**
     * helper method to split a sevrice into it's halves
     * @param str service to split
     * @return array with each half stored
     */
    private static String splitHost(String str) throws IllegalArgumentException {
        if (!str.contains(SPLITER)){
            throw new IllegalArgumentException("Invalid Service structure");
        }
        return str.split(SPLITER)[0];
    }

    /**
     * Creates a response from a String
     * @param msg byte array to transform
     * @return Response message
     */
    private static Message createResponse(String msg){
        Response r = new Response();
        if (msg.length() != 0) {
            String[] split = msg.split(" ");
            for (String s : split) {
                r.addService(splitHost(s), splitPort(s));
            }
        }
        return r;
    }




    /**
     * Serialize message
     * @return serialized message
     */
    public abstract byte[] encode();

    /**
     * Get operation
     * @return operation
     */
    public abstract String getOperation();
}
