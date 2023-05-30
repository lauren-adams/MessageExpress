/************************************************
 *
 * Author: Lauren Adams
 * Assignment: Program 1
 * Class: Data Communications 4321
 *
 ************************************************/

package megex.serialization;
import java.util.Objects;

import static megex.serialization.SerializationUtility.*;
/**
 * This class specifies the methods for the Window_Update message object
 */
public class Window_Update extends Message{

    /**
     * private attribute for windows_update
     */
    private int increment;

    /**
     * Creates Window_Update message from given values
     * @param streamID stream id
     * @param increment increment values
     * @throws BadAttributeException if attribute invalid (see protocol spec)
     */
    public Window_Update(int streamID,
                         int increment)
            throws BadAttributeException{
        //check to make sure stream ID is not 0 in setStream
        super(streamID);
        setIncrement(increment);
    }

    /**
     * Get increment value
     * @return int increment value
     */
    public int getIncrement(){
        return this.increment;
    }

    /**
     * Set increment value
     * @param increment increment value
     * @throws BadAttributeException if invalid
     */
    public void setIncrement(int increment)
            throws BadAttributeException {
        //ensures R is ignored if setInc is called directly
        //increment = increment & INTMASK;
        //check to make sure the leftmost bit is not set
        if ((increment & RCHECK) != 0){
            throw new BadAttributeException("Increment R is not 0",
                    String.valueOf(increment));
        }
        if (increment < 1){
            throw new BadAttributeException("Increment is not within range",
                    String.valueOf(increment));
        }

        this.increment = increment;
    }

    /**
     * Returns string of the form
     * Window_Update: StreamID=streamId increment=inc
     * For example
     * Window_Update: StreamID=5 increment=1024
     * @return string
     */
    public String toString(){
        return "Window_Update: StreamID=" + getStreamID()
                + " increment=" + getIncrement();
    }

    /**
     * set equality for a Windows_Update object
     * @param o object being compared
     * @return boolean
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Window_Update that = (Window_Update) o;
        return increment == that.increment;
    }

    /**
     * creates a hashcode with elements of the message
     * @return int hashcode
     */
    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), increment);
    }


    /**
     * Gets Windows_Update code
     * @return code associated with Object
     */
    @Override
    public byte getCode() {
        return WUTYPE;
    }

    /**
     * enforces that streamId cannot be 0
     * @param streamId new stream id value
     * @throws BadAttributeException if streamID is 0
     */
    @Override
    protected int validateStreamID(int streamId) throws BadAttributeException{
        if (streamId < 0){
            throw new BadAttributeException("stream id cannot be less than 0", String.valueOf(streamId));
        }
        return streamId;
        //super.setStreamID(streamId);
    }
}
