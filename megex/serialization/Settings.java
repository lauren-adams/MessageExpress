/************************************************
 *
 * Author: Lauren Adams
 * Assignment: Program 1
 * Class: Data Communications 4321
 *
 ************************************************/

package megex.serialization;

import static megex.serialization.SerializationUtility.*;

/**
 * This class specifies the methods for the Settings message object
 */
public class Settings extends Message{
    /**
     * Creates Settings message
     * @throws BadAttributeException if attribute invalid (not thrown in this case)
     */
    public Settings()
            throws BadAttributeException{
            super(0);

    }

    /**
     * Returns string of the form
     * Settings: StreamID=0
     *
     * For example
     * Settings: StreamID=0
     * @return string
     */
    public String toString(){
        return "Settings: StreamID=" + getStreamID();
    }


    /**
     * enforces that streamId must be 0
     * @param streamId new stream id value
     * @throws BadAttributeException if streamID is not 0
     */

    @Override
    public int validateStreamID(int streamId) throws BadAttributeException {
        if (streamId != 0){
            throw new BadAttributeException("stream id MUST be 0", String.valueOf(streamId));
        }
        return streamId;
    }

    /**
     * Gets Settings code
     * @return code associated with Settings
     */
    @Override
    public byte getCode() {
        return SETTINGSTYPE;
    }
}
