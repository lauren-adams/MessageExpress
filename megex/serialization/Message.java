/************************************************
 *
 * Author: Lauren Adams
 * Assignment: Program 1
 * Class: Data Communications 4321
 *
 ************************************************/

package megex.serialization;

import java.util.Objects;



/**
 * This class specifies the methods for the Message object
 */
public abstract class Message {

    /**
     * private attribute of Method that identifies stream
     */
    private int streamID;

    /**
     * constructor for Message object
     *
     * @param streamID the integer id of the stream passing the message
     * @throws BadAttributeException if streamID is invalid
     */
    protected Message(int streamID) throws BadAttributeException {
        setStreamID(streamID);
    }

    /**
     * Returns type code for message
     * @return byte type code
     */
    public abstract byte getCode();

    /**
     * ensures string is appropriate for type
     * @param streamID value being validated
     * @return streamID
     * @throws BadAttributeException if not valid
     */
    protected abstract int validateStreamID(int streamID) throws BadAttributeException;

    /**
     * Returns the stream ID
     *
     * @return message stream ID
     */
    public int getStreamID(){
        return this.streamID;
    }

    /**
     * Sets the stream id in the frame. Stream ID validation depends on specific message type
     *
     * @param streamId new stream id value
     * @throws BadAttributeException if input stream id is invalid
     */
    public void setStreamID(int streamId)
            throws BadAttributeException{
        this.streamID = validateStreamID(streamId);
    }


    /**
     * set equality for a Message object
     * @param o object being compared
     * @return boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message message = (Message) o;
        return getCode() == message.getCode() && streamID == message.streamID;
    }


    /**
     * creates a hashcode with elements of the message
     * @return int hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(getCode(), streamID);
    }
}
